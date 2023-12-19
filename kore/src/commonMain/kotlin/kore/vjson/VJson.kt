@file:Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE", "FunctionName")

package kore.vjson

import kore.vo.VO
import kore.vo.converter.Converter
import kore.vo.converter.ToVONoInitialized
import kore.vo.field.Field
import kore.vo.field.VOField
import kore.vo.field.list.*
import kore.vo.field.map.*
import kore.vo.field.value.*
import kore.vo.task.Task
import kore.wrap.W
import kore.wrap.Wrap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlin.reflect.KClass

fun VO.toJSON():Wrap<String> = VJson.to(this)
fun <V:VO> V.fromJSON(json:String):Wrap<V> = VJson.from(this, json)
object VOJson {
    class Emitter(isString:Boolean, type:Int/** 0 value, 1 list, 2 map*/){
        val f:suspend FlowCollector<String>.(v:Any)->Unit = when(type){
            0-> if(isString) ({emit("\"$it\"")}) else ({emit("$it")})
            else-> ({
                val list:List<Any>? = if(type == 1) it as List<Any> else null
                val map: Map<String, Any>? = if(type == 2) it as Map<String, Any> else null
                val keys:Array<String>? = if(type == 2) map!!.keys.toTypedArray() else null
                emit(if(type == 0) "[" else "{")
                val size:Int = if(type == 1) list!!.size else keys!!.size
                var i:Int = 0
                do{
                    if(i != 0) emit(",")
                    if(type == 1) {
                        val v = list!![i]
                        emit(if(v is String) "\"$v\"" else "$v")
                    }else{
                        val key = keys!![i]
                        val v = map!![key]!!
                        emit("\"$key\":${if(v is String)"\"$v\"" else "$v"}")
                    }
                }while(++i < size)
                emit(if(type == 0) "]" else "}")
            })
        }
    }
    private val emitters:HashMap<KClass<*>, suspend FlowCollector<String>.(Any)->Unit> = HashMap<KClass<*>, suspend FlowCollector<String>.(Any)->Unit>(50).also{ map->
        map[VOField::class] = emitVO
        map[StringField::class] = Emitter(true, 0).f
        val emitValue:suspend FlowCollector<String>.(v:Any)->Unit = Emitter(false, 0).f
        arrayOf<KClass<*>>(IntField::class, IntField::class, ShortField::class, LongField::class, UIntField::class, UShortField::class, ULongField::class, FloatField::class, DoubleField::class, BooleanField::class).forEach {
            map[it] = emitValue
        }
        map[StringListField::class] = Emitter(true, 1).f
        val emitList:suspend FlowCollector<String>.(v: Any)->Unit = Emitter(false, 1).f
        arrayOf<KClass<*>>(IntListField::class, ShortListField::class, LongListField::class, UIntListField::class, UShortListField::class, ULongListField::class, FloatListField::class, DoubleListField::class, BooleanListField::class).forEach {
            map[it] = emitList
        }
        map[StringMapField::class] = Emitter(true, 2).f
        val emitMap:suspend FlowCollector<String>.(v:Any)->Unit = Emitter(false, 2).f
        arrayOf<KClass<*>>(IntMapField::class, ShortMapField::class, LongMapField::class, UIntMapField::class, UShortMapField::class, ULongMapField::class, FloatMapField::class, DoubleMapField::class, BooleanListField::class).forEach {
            map[it] = emitMap
        }
    }
    private val emitVO:suspend FlowCollector<String>.(Any)->Unit = {
        val vo:VO = it as VO
        val fields:HashMap<String, Field<*>> = vo.getFields() ?: ToVONoInitialized(vo, "a").terminate()
        val keys:List<String> = VO.keys(vo) ?: ToVONoInitialized(vo, "c").terminate()
        val tasks:HashMap<String, Task>? = vo.getTasks()
        emit("{")
        val size:Int = keys.size
        var i:Int = 0
        do{
            val key:String = keys[i]
            vo[key]?.let{v->
                val include:((String, Any?) -> Boolean)? = tasks?.get(key)?.include
                if(include == null || include(key, v)) fields[key]?.let{ field->
                    emitters[field::class]?.let{
                        if(i != 0) emit(",")
                        emit("\"$key\":")
                        it(v)
                    } ?: ToVONoInitialized(vo, "field:${field::class.simpleName}, v:$v").terminate()
                }
            }
        }while(++i < size)
        emit("}")
    }
    fun to(vo:VO):Flow<String> = flow{emitVO(vo)}
    class Updater<V:VO>(val vo:V){
//        val fields:HashMap<String, Field<*>> = vo.getFields() ?: ToVONoInitialized(vo, "a").terminate()
//        val keys:List<String> = VO.keys(vo) ?: ToVONoInitialized(vo, "c").terminate()
//        val tasks:HashMap<String, Task>? = vo.getTasks()
        data class Stack(val type:Any, val target:Any, val key:String = "")
        /** type, target, key*/
        private var targetStack:ArrayList<Stack> = ArrayList<Stack>(10).also{it.add(Stack(vo, vo))}

        private var state:Int = 100
        private var toState:Int = 0
        private var buffer:StringBuilder = StringBuilder(1000)
        private var flushed:String = ""
        private var key:String = ""
        private inline fun skipSpace(to:Int, v:String, c:Int):V?{
            toState = to
            state = 0
            return invoke(v, c)
        }
        private inline fun _skipSpace(v:String, c:Int):V?{
            var cursor = c
            do{
                val it = v[cursor]
                if(" \t\n\r".indexOf(it) != -1) cursor++ else break
            }while(cursor < v.length)
            return if(cursor == v.length) null else {
                state = toState
                invoke(v, cursor)
            }
        }
        private inline fun read(to:Int, s:Int, v:String, c:Int):V?{
            toState = to
            state = s
            buffer.clear()
            return invoke(v, c)
        }
        private inline fun _readWhile(v:String, c:Int, skipLast:Boolean, block:(Char)->Boolean):V?{
            var cursor = c
            var isFlushed = false
            do{
                val it = v[cursor++]
                if(block(it)) buffer.append(it) else {
                    flushed = buffer.toString()
                    isFlushed = true
                    break
                }
            }while(cursor < v.length)
            return if(isFlushed){
                state = toState
                invoke(v, cursor + if(skipLast) 1 else 0)
            }else null
        }
        private inline fun _wordRead(word:String, v:String, c:Int):V?{
            var cursor:Int = c
            var isFlushed = false
            do{
                val it = v[cursor++]
                if(buffer.length == 4){
                    flushed = buffer.toString()
                    isFlushed = true
                    break
                }else if(word[buffer.length] == it) buffer.append(it)
                else throw Throwable("invalid $word")
            }while(cursor < v.length)
            return if(isFlushed){
                state = toState
                invoke(v, cursor)
            }else null
        }
        private inline fun stringRead(to:Int, v:String, c:Int):V? = read(to, 10, v, c)
        private inline fun _stringStart(v:String, c:Int):V?{
            if(v[c] != '"') throw Throwable("invalid string start")
            state = 11
            return invoke(v, c + 1)
        }
        private inline fun _stringRead(v:String, c:Int):V? = _readWhile(v, c, true){it != '"'}
        private inline fun numRead(to:Int, v:String, c:Int):V? = read(to, 20, v, c)
        private inline fun _numRead(v:String, c:Int):V? = _readWhile(v, c, false){it in "0123456789-."}
        private inline fun trueRead(to:Int, v:String, c:Int):V? = read(to, 30, v, c)
        private inline fun _trueRead(v:String, c:Int):V? = _wordRead("true", v, c)
        private inline fun falseRead(to:Int, v:String, c:Int):V? = read(to, 40, v, c)
        private inline fun _falseRead(v:String, c:Int):V? = _wordRead("false", v, c)
        private inline fun nullRead(to:Int, v:String, c:Int):V? = read(to, 50, v, c)
        private inline fun _nullRead(v:String, c:Int):V? = _wordRead("null", v, c)
        private inline fun convert(type:String):Any = when(type) {
            "String"->flushed
            "Int"->flushed.toIntOrNull() ?: throw Throwable("invalid int $flushed")
            "Short"->flushed.toShortOrNull() ?: throw Throwable("invalid short $flushed")
            "Long"->flushed.toLongOrNull() ?: throw Throwable("invalid long $flushed")
            "UInt"->flushed.toUIntOrNull() ?: throw Throwable("invalid uint $flushed")
            "UShort"->flushed.toUShortOrNull() ?: throw Throwable("invalid ushort $flushed")
            "ULong"->flushed.toULongOrNull() ?: throw Throwable("invalid ulong $flushed")
            "Float"->flushed.toFloatOrNull() ?: throw Throwable("invalid float $flushed")
            "Double"->flushed.toDoubleOrNull() ?: throw Throwable("invalid double $flushed")
            "Boolean"->if(flushed == "true") true else if(flushed == "false") false else throw Throwable("invalid boolean $flushed")
            else->throw Throwable("invalid value field")
        }
        tailrec operator fun invoke(v:String, c:Int):V?{
            if(v.isEmpty() || c >= v.length) return null
            return when(state){
                0->_skipSpace(v, c)
                10->_stringStart(v, c)
                11->_stringRead(v, c)
                20->_numRead(v, c)
                30->_trueRead(v, c)
                40->_falseRead(v, c)
                50->_nullRead(v, c)

                /** space before object*/
                100->skipSpace(101, v, c)
                /** object start */
                101->if(v[c] != '{') throw Throwable("invalid object") else skipSpace(102, v, c + 1)
                /** object key */
                102->stringRead(103, v, c)
                103->{
                    key = flushed
                    skipSpace(104, v, c)
                }
                104->if(v[c] != ':') throw Throwable("invalid object") else skipSpace(105, v, c + 1)
                105->{ /** vo space before value */
                    val it:Char = v[c]
                    when{
                        it == '"'-> stringRead(106, v, c)
                        "0123456789-.".indexOf(it) != -1->numRead(106, v, c)
                        it == 't'->trueRead(106, v, c)
                        it == 'f'->falseRead(106, v, c)
                        it == 'n'->nullRead(106, v, c)
//                        it == '['->{
//                            state = 150
//                            invoke(v, cursor + 1)
//
//                        }
//                        it == '{'->{
//                            state = 160
//                            invoke(v, cursor + 1)
//
//                        }
//                        it == 'n'->{
//                            state = 170
//                            invoke(v, cursor + 1)
//
//                        }
                        else->throw Throwable("invalid VO value")
                    }
                }
                106->{ /** object value */
                    val (type, target) = targetStack.last()
                    when{
                        type is VO->(target as VO)[key] = convert(vo.getFields()?.get(key)?.typeName ?: throw Throwable("invalid VO field"))
                        type is String && type.startsWith('m')->(target as MutableMap<String, Any>)[key] = convert(type.substring(1))
                        type is String && type.startsWith('l')->(target as MutableList<Any>).add(convert(type.substring(1)))
                        else->throw Throwable("invalid type $type")
                    }
                    skipSpace(107, v, c + 1)
                }
                107->{
                    val it:Char = v[c]
                    when(it){
                        ','->skipSpace(102, v, c + 1)
                        '}'->{
                            val (type, target) = targetStack.removeAt(targetStack.size - 1)
                            if(targetStack.isEmpty()) return vo
                            else{
                                when{
                                    type is VO->skipSpace(107, v, c + 1)
                                    type is String && type.startsWith('m')->skipSpace(107, v, c + 1)
                                    type is String && type.startsWith('l')->skipSpace(107, v, c + 1)
                                    else->throw Throwable("invalid type $type")
                                }
                            }
                            skipSpace(108, v, c + 1)
                        }
                        else->throw Throwable("invalid object")
                    }
                }
                else->null
            }
        }
    }

    fun <V:VO> from(vo:V, value:Flow<String>):Flow<V> = flow{
        val emitter:FlowCollector<V> = this
        val updater:Updater<V> = Updater(vo)
        value
            .onCompletion{
                emitter.emit(vo)
            }
            .collect {
                updater(it, 0)?.let{v->emitter.emit(v)}
            }
    }
}

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