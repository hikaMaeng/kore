@file:Suppress("NOTHING_TO_INLINE", "RegExpSingleCharAlternation")

package kore.vosn

import kore.vo.VO
import kore.vo.field.*
import kore.error.E
import kore.vo.converter.Converter
import kore.wrap.W
import kore.wrap.Wrap
import kotlin.reflect.KClass



inline fun VO.toVOSN():Wrap<String> = VOSN.to(this)
inline fun <V:VO> V.fromVOSN(vosn:String):Wrap<V> = VOSN.from(this, vosn)

/**
 * VOSN : Value Object Serialization Notation
 */
object VOSN:Converter<String>{

    const val OPTIONAL_NULL: String = "~"
    const val OPTIONAL_NULL_C: Char = '~'
    const val STRINGLIST_EMPTY: String = "!"
    const val STRINGLIST_EMPTY_C: Char = '!'

    class Cursor(val encoded:String, var v:Int){
        class DecodeNoListTeminator(val target:String): E(target)
        inline fun getAndNext():Char = encoded[v++]
        inline val isEnd:Boolean get() = encoded.length == v
        inline val curr:Char get() = encoded[v]
        inline val nextValue:String get(){
            val start = v
            v = encoded.indexOfAny(charArrayOf('|','@'), start)
            if(v == -1) v = encoded.length
            return encoded.substring(start, v)
        }
        inline val nextValueList:Wrap<List<String>> get(){
            val start = v
            v = encoded.indexOf('@', start)
            return if(v == -1) W(DecodeNoListTeminator(encoded.substring(start)))
            else W(encoded.substring(start, v++).split('|'))
        }
        inline fun loopItems(block:()->Wrap<*>):Wrap<Any>?{
            if(curr == '@'){
                v++
                return null
            }
            do{
                block().fail?.let {return it}
                if(v >= encoded.length){
                    return W(DecodeNoListTeminator(encoded.substring(v - 1)))
                }else when (getAndNext()) {
                    '|' -> {} /** 다음데이터 */
                    '@' ->return null /** 리스트끝 */
                    else ->return W(DecodeNoListTeminator(encoded.substring(v - 1)))
                }
            }while(true)
        }
    }
    /**
     * 문자열을 인코딩 디코딩할 때 이스케이프해야하는 특수문자 처리를 정의함
     * @ : 리스트와 맵의 종결자로 사용함
     * | : 모든 요소의 구분자로 사용함(리스트, 맵의 키와 값의 구분자, 엔티티 필드 구분자 등)
     * ~ : 필드가 옵셔널인 경우 null을 표현함
     * ! : 문자열 리스트의 빈 리스트를 표현함
     * \n, \r : 문자열의 개행은 이스케이핑한다
     */
    private val encodeStringRex:Regex = Regex("[@|~\n\r!]")
    private val encodeStringMap:Map<String, String> = hashMapOf(
        "\\" to "\\\\", "@" to "\\@", "|" to "\\|", "~" to "\\~", "!" to "\\!", "\n" to "\\n", "\r" to "\\r"
    )
    private val decodeStringRex:Regex = Regex("\\\\[@|~\n\r!]")
    private val decodeStringMap:Map<String, String> = hashMapOf(
        "\\\\" to "\\", "\\@" to "@", "\\|" to "|", "\\~" to "~", "\\!" to "!", "\\n" to "\n", "\\r" to "\r"
    )
    internal inline fun encodeString(v:Any?):String = "$v".replace(encodeStringRex){ encodeStringMap[it.value]!!}
    internal inline fun decodeString(v:String):String = v.replace(decodeStringRex){ decodeStringMap[it.value]!!}
    fun setTo(type:KClass<*>, block:(Any, Field<*>)->Wrap<String>){
        To.encoders[type] = block
    }
    fun setFrom(type:KClass<*>, block:(cursor: Cursor, field: Field<*>)->Wrap<Any>){
        From.decoders[type] = block
    }
    override fun to(vo: VO):Wrap<String> = To.vo(vo)
    override fun <V: VO> from(vo:V, value: String):Wrap<V> = From.vo(Cursor(value, 0), vo)
}