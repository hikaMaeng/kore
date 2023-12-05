package kore.vjson

import kore.vo.VO
import kore.vo.converter.Converter
import kore.wrap.*

fun VO.toJSON():Wrap<String> = VJson.to(this)
fun <V:VO> V.fromJSON(json:String):Wrap<V> = VJson.from(this, json)

@Suppress("NOTHING_TO_INLINE")
object VJson:Converter<String> {
    override fun to(vo:VO):Wrap<String> = To.vo(vo)
    override fun <V:VO> from(vo:V, value:String):Wrap<V> = W(vo)//From.vo(vo, value, Cursor(0))
//
//    class Cursor(var v:Int)
//    /**
//     * 문자열을 인코딩 디코딩할 때 이스케이프해야하는 특수문자 처리를 정의함
//     * " :문자열 내부의 "는 이스케이핑한다
//     * \n, \r :문자열의 개행은 이스케이핑한다
//     */
//    private inline fun encodeString(v:Any?):String = "$v".replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\\\r")
//    private inline fun decodeString(v:String):String = v.replace("\\\"", "\"").replace("\\n", "\n").replace("\\r", "\r")
//
//
//    private val entry:Map.Entry<String, Field<*>> = object:Map.Entry<String, Field<*>>{
//        override val key:String get() = throw Error(eEntity.ERROR.encode_error,"")
//        override val value:Field<*> get() = throw Error(eEntity.ERROR.encode_error,"")
//    }
//
//    private inline fun openObject(serial:String,cursor:Cursor):Boolean{
//        return if(skipSep('{', serial, cursor)) true
//        else throw Error(eEntity.ERROR.decode_error,"invalid object,cursor:${cursor.v},serial[cursor.v] = ${serial.substring(cursor.v)},serial:$serial")
//    }
//    private inline fun openList(serial:String,cursor:Cursor):Boolean{
//        return if(skipSep('[', serial, cursor)) true
//        else throw Error(eEntity.ERROR.decode_error,"invalid list,cursor:${cursor.v},serial:$serial")
//    }
//    private inline fun key(serial:String, cursor:Cursor, report:Report):String?{
//        val key = decodeStringValue(serial,cursor,report) ?:return null
//        skipSpace(serial, cursor)
//        if(serial[cursor.v++] != ':') throw Error(eEntity.ERROR.decode_error,"invalid key form,key:${key},cursor:${cursor.v-1},serial:$serial")
//        return key
//    }
//
//
//    private inline fun skipSpace(serial:String, cursor:Cursor){
//        var isChanged = false
//        var i = cursor.v
//        var limit = 200
//        do{
//            val c = serial[i++]
//            if(c == ' ' || c == '\t' || c == '\n' || c == '\r'){
//                isChanged = true
//            } else break
//        }while(limit-- > 0)
//        if(isChanged) cursor.v = i-1
//    }
//    private inline fun skipSep(sep:Char, serial:String, cursor:Cursor):Boolean{
//        skipSpace(serial, cursor)
//        return if(serial[cursor.v] == sep){
//            cursor.v++
//            true
//        }else false
//    }
//    private inline fun skipNotSep(sep:Char, serial:String, cursor:Cursor):Boolean{
//        skipSpace(serial, cursor)
//        return serial[cursor.v] != sep
//    }
//    private inline fun skipComma(serial:String,cursor:Cursor){
//        skipSpace(serial, cursor)
//        if(serial.length >= cursor.v && serial[cursor.v] == ','){
//            cursor.v++
//        }
//    }
//    private fun passValue(key:String, serial:String, cursor:Cursor, report:Report){
//        skipSpace(serial, cursor)
//        when(val curr = serial[cursor.v]){
//            '['->{
//                openList(serial, cursor)
//                if(skipSep(']', serial, cursor)) skipComma(serial, cursor)
//                else{
//                    var idx = -1
//                    do{
//                        idx++
//                        passValue("$key-$idx", serial, cursor, report)
//                        if(skipSep(']', serial, cursor)){
//                            cursor.v++
//                            break
//                        }
//                        skipComma(serial, cursor)
//                    }while(true)
//                }
//            }
//            '{'->{
//                openObject(serial, cursor)
//                if(skipSep('}', serial, cursor)) skipComma(serial, cursor)
//                else{
//                    while(skipNotSep('}', serial, cursor)){
//                        val mapKey = decodeStringValue(serial, cursor, report)
//                        if(mapKey == null){
//                            report<Map<String,*>>(eEntity.ERROR.decode_error,"no passValue|$key map key null")
//                            break
//                        }
//                        cursor.v++
//                        passValue(mapKey, serial, cursor, report)
//                        if(skipSep('}', serial, cursor)) break
//                        skipComma(serial,cursor)
//                    }
//                }
//            }
//            'n'-> cursor.v += 4 //null 만큼 전진
//            't'-> cursor.v += 4 //true 만큼 전진
//            'f'-> cursor.v += 5 //false 만큼 전진
//            '"'-> decodeStringValue(serial, cursor, report) //문자열 및 이스케이프 확인하면서 전진
//            else->{
//                //종료 문자열 " \t\n\r,]}" 위치까지 전진
//                if("0123456789-.".indexOf(curr) != -1) cursor.v = serial.indexOfAny(SEP, cursor.v++)
//                else report(eEntity.ERROR.decode_error,"no passValue|$key|$curr")
//            }
//        }
//    }
}