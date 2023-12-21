@file:Suppress("NOTHING_TO_INLINE", "FunctionName")

package kore.vjson

import kore.vo.VO
import kore.vo.field.value.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow

object JSON{
    private val _converters:HashMap<Any, (String)->Any> = hashMapOf(
        IntField::class to {it:String->it.toInt()},
        ShortField::class to {it:String->it.toShort()},
        LongField::class to {it:String->it.toLong()},
        DoubleField::class to {it:String->it.toDouble()},
        FloatField::class to {it:String->it.toFloat()},
        BooleanField::class to {it:String->it.toBoolean()},
        UIntField::class to {it:String->it.toUInt()},
        UShortField::class to {it:String->it.toUShort()},
        ULongField::class to {it:String->it.toULong()},
        String::class to {it:String->it},
        Int::class to {it:String->it.toInt()},
        Double::class to {it:String->it.toDouble()},
        Boolean::class to {it:String->it.toBoolean()},
        VO::class to {it:String->it},
        MutableList::class to {it:String->it},
        MutableMap::class to {it:String->it},
    )
    operator fun set(type:Any, converter:(String)->Any){
        _converters[type] = converter
    }
    operator fun get(type:Any):(String)->Any = _converters[type] ?: throw Throwable("invalid type $type")
    fun <V:VO> from(vo:V, value:Flow<String>):Flow<V> = flow{
        val emitter:FlowCollector<V> = this
        val parser:Parser<V> = Parser(vo)
        value
            .collect {
                var str:String? = it
                while(str != null) str = parser(str)?.let{(s, vo)->
                    emitter.emit(vo)
                    s
                }
            }
    }
}
class Parser<V:VO>(val vo:V){
    data class Stack(val type:Any, val target:Any, var key:String = "")
    private var curr:Stack = Stack(vo, vo)
    private val stack:ArrayList<Stack> = arrayListOf(curr)
    private var input:String? = null
    var state:Int = 100
    var next:Int = 0
    var s:String = ""
    var c:Int = 0
    var buffer:StringBuilder = StringBuilder(1000)
    var flushed:String = ""
    inline fun err(msg:String):Nothing = throw Throwable(msg + " $state, $c, ${s[c]}, $s")

    private val sequence:Iterator<Pair<String, V>?> = sequence{
        while(true) yield(input?.let{inp->
            input = null
            run(inp.ifEmpty {return@let null})?.let{it to vo}
        })
    }.iterator()
    operator fun invoke(s:String):Pair<String, V>?{
        input = s
        return sequence.next()
    }
    private inline fun run(input:String):String?{
        s = input
        c = 0
        while(c < s.length){
            when(state){
                0->_skipSpace()
                10->if(s[c++] == '"') state = 11 else err("invalid string start")
                11->readWhile(true){it != '"'}
                20->readWhile(false){it in "0123456789-."}
                30->wordRead("true")
                40->wordRead("false")
                50->wordRead("null")
                100->skipSpace(101)
                101->if(s[c++] == '{') skipSpace(102) else err("invalid object")
                102->stringRead(103)
                103->{
                    curr.key = flushed
                    skipSpace(104)
                }
                104->if(s[c++] == ':') skipSpace(105) else err("invalid colon next key")
                105->{ /** vo space before value */
                    val it:Char = s[c]
                    when{
                        it == '"'-> stringRead(106)
                        "0123456789-.".indexOf(it) != -1->numRead(106)
                        it == 't'->trueRead(106)
                        it == 'f'->falseRead(106)
                        it == 'n'->nullRead(106)
//                        it == '['->{
//                            state = 150
//                            invoke(v, cursor + 1)
//
//                        }
//                        it == '{'->{
//                            targetStack.add(VOJson.Updater.Stack(vo[key]!!, vo, key))
//                            skipSpace(102, v, c + 1)
//
//                        }
                        else->err("invalid VO value")
                    }
                }
                106->{ /** object value */
                    when{
                        curr.type is VO->(curr.target as? VO)?.let{
                            it[curr.key] = JSON[vo.getFields()?.get(curr.key)?.let{it::class} ?: err("invalid VO field")](flushed)
                            if(stack.size == 1){
                                state = 107
                                return s.substring(c)
                            }
                        }
//                        type is String && type.startsWith('m')->(target as MutableMap<String, Any>)[key] = convert(type.substring(1))
//                        type is String && type.startsWith('l')->(target as MutableList<Any>).add(convert(type.substring(1)))
//                        else->throw Throwable("invalid type $type")
                    }
                    skipSpace(108)
                }
                107->skipSpace(108)
                108->when(s[c++]){
                    ','->skipSpace(102)
                    '}'->{
                        if(stack.size == 1){
                            state = 1000
                            return null
                        }
                        val prev:Stack = curr
                        curr = stack.removeLast()
                        when{
                            curr.type is VO->(curr.target as? VO)?.let{
                                it[curr.key] = prev.target
                                skipSpace(108)
                            }
                        }
                    }
                }
                1000->{}
            }
        }
        return null
    }
    private inline fun prepare(to:Int, s:Int, useBuffer:Boolean){
        next = to
        state = s
        if(useBuffer) buffer.clear()
    }
    private inline fun readWhile(isDropLast:Boolean, block:(Char)->Boolean){
        do{
            val it = s[c++]
            if(block(it)) buffer.append(it) else {
                flushed = buffer.toString()
                state = next
                c -= if(isDropLast) 0 else 1
                break
            }
        }while(c < s.length)
    }
    private inline fun wordRead(word:String){
        do{
            val it = s[c++]
            if(buffer.length == word.length){
                flushed = buffer.toString()
                state = next
                break
            }else if(word[buffer.length] == it) buffer.append(it)
            else err("invalid $word")
        }while(c < s.length)
    }
    private inline fun skipSpace(to:Int) = prepare(to, 0, false)
    private inline fun stringRead(to:Int) = prepare(to, 10, true)
    private inline fun numRead(to:Int) = prepare(to, 20, true)
    private inline fun trueRead(to:Int) = prepare(to, 30, true)
    private inline fun falseRead(to:Int) = prepare(to, 40, true)
    private inline fun nullRead(to:Int) = prepare(to, 50, true)
    private inline fun _skipSpace(){
        do{
            val it = s[c]
            if(" \t\n\r".indexOf(it) != -1) c++ else{
                state = next
                break
            }
        }while(c < s.length)
    }
}
