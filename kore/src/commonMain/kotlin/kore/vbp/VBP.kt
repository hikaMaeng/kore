@file:Suppress("NOTHING_TO_INLINE", "FunctionName", "UNCHECKED_CAST")

package kore.vbp

import kore.vo.VO
import kore.vo.converter.ToNoConverter
import kore.vo.converter.ToNullField
import kore.vo.converter.ToVONoInitialized
import kore.vo.field.*
import kore.vo.field.list.*
import kore.vo.field.map.*
import kore.vo.field.value.*
import kore.vo.task.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.io.*
import kotlin.reflect.KClass

object VBP{
    private inline fun writeBytes(block:Buffer.()->Unit):ByteArray{
        val buffer:Buffer = Buffer()
        buffer.block()
        val byteArray:ByteArray = buffer.readByteArray()
        buffer.close()
        return byteArray
    }
    private inline fun readBytes(arr:ByteArray, block:Buffer.()->Any):Any{
        val buffer:Buffer = Buffer()
        buffer.write(arr)
        val v:Any = buffer.block()
        buffer.close()
        return v
    }
    private val parsers:HashMap<Any, (Any, ByteArray)->Pair<Any, ByteArray>?> = HashMap<Any, (Any, ByteArray)->Pair<Any, ByteArray>?>(100).also{
        val cInt:(Any, ByteArray)->Pair<Any, ByteArray>? = {_, v->
            if(v.size < 4) null else readBytes(v){readInt()} to v.copyOfRange(4, v.size)
        }
        val cShort:(Any, ByteArray)->Pair<Any, ByteArray>? = {_, v->
            if(v.size < 2) null else readBytes(v){readShort()} to v.copyOfRange(2, v.size)
        }
        val cLong:(Any, ByteArray)->Pair<Any, ByteArray>? = {_, v->
            if(v.size < 8) null else readBytes(v){readLong()} to v.copyOfRange(8, v.size)
        }
        val cFloat:(Any, ByteArray)->Pair<Any, ByteArray>? = {_, v->
            if(v.size < 4) null else readBytes(v){readFloat()} to v.copyOfRange(4, v.size)
        }
        val cDouble:(Any, ByteArray)->Pair<Any, ByteArray>? = {_, v->
            if(v.size < 8) null else readBytes(v){readDouble()} to v.copyOfRange(8, v.size)
        }
        val cBoolean:(Any, ByteArray)->Pair<Any, ByteArray>? = {_, v->
            readBytes(v){readByte().toInt() == 1} to v.copyOfRange(1, v.size)
        }
        val cUInt:(Any, ByteArray)->Pair<Any, ByteArray>? = {_, v->
            if(v.size < 4) null else readBytes(v){readUInt()} to v.copyOfRange(4, v.size)
        }
        val cUShort:(Any, ByteArray)->Pair<Any, ByteArray>? = {_, v->
            if(v.size < 2) null else readBytes(v){readUShort()} to v.copyOfRange(2, v.size)
        }
        val cULong:(Any, ByteArray)->Pair<Any, ByteArray>? = {_, v->
            if(v.size < 8) null else readBytes(v){readULong()} to v.copyOfRange(8, v.size)
        }
        val cString:(Any, ByteArray)->Pair<Any, ByteArray>? = {_, v->
            val index:Int = v.indexOf(0.toByte())
            if(index == -1) null else readBytes(v.copyOfRange(0, index)){readString()} to v.copyOfRange(index + 1, v.size)
        }
        it[Int::class] = cInt
        it[Short::class] = cShort
        it[Long::class] = cLong
        it[Float::class] = cFloat
        it[Double::class] = cDouble
        it[Boolean::class] = cBoolean
        it[UInt::class] = cUInt
        it[UShort::class] = cUShort
        it[ULong::class] = cULong
        it[String::class] = cString

        it[IntField::class] = cInt
        it[ShortField::class] = cShort
        it[LongField::class] = cLong
        it[FloatField::class] = cFloat
        it[DoubleField::class] = cDouble
        it[BooleanField::class] = cBoolean
        it[UIntField::class] = cUInt
        it[UShortField::class] = cUShort
        it[ULongField::class] = cULong
        it[StringField::class] = cString

        it[IntListField::class] = cInt
        it[ShortListField::class] = cShort
        it[LongListField::class] = cLong
        it[FloatListField::class] = cFloat
        it[DoubleListField::class] = cDouble
        it[BooleanListField::class] = cBoolean
        it[UIntListField::class] = cUInt
        it[UShortListField::class] = cUShort
        it[ULongListField::class] = cULong
        it[StringListField::class] = cString

        it[IntMapField::class] = cInt
        it[ShortMapField::class] = cShort
        it[LongMapField::class] = cLong
        it[FloatMapField::class] = cFloat
        it[DoubleMapField::class] = cDouble
        it[BooleanMapField::class] = cBoolean
        it[UIntMapField::class] = cUInt
        it[UShortMapField::class] = cUShort
        it[ULongMapField::class] = cULong
        it[StringMapField::class] = cString

//        it[EnumField::class] = {field, v->
//            val enums:Array<*> = (field as EnumField<*>).enums
//            val index:Int = readBytes(v){readInt()} as Int
//            if(index < enums.size) enums[index]!! else ToNoEnum(enums, v).terminate()
//        }
//        it[EnumListField::class] = {field, v->
//            val enums:Array<*> = (field as EnumListField<*>).enums
//            val index:Int = readBytes(v){readInt()} as Int
//            if(index < enums.size) enums[index]!! else ToNoEnum(enums, v).terminate()
//        }
//        it[EnumMapField::class] = {field, v->
//            val enums:Array<*> = (field as EnumMapField<*>).enums
//            val index:Int = readBytes(v){readInt()} as Int
//            if(index < enums.size) enums[index]!! else ToNoEnum(enums, v).terminate()
//        }
    }
    private val OPTIONAL_NULL:ByteArray = byteArrayOf(1.toByte())

    private val binarifier:HashMap<KClass<*>, suspend FlowCollector<ByteArray>.(Any)->Unit> = HashMap<KClass<*>, suspend FlowCollector<ByteArray>.(Any)->Unit>(50).also{target->
        target[VOField::class] = {
            val vo:VO = it as VO
            val fields:HashMap<String, Field<*>> = vo.getFields() ?: ToVONoInitialized(vo, "no fields").terminate()
            val keys:List<String> = VO.keys(vo) ?: ToVONoInitialized(vo, "no keys").terminate()
            val tasks:HashMap<String, Task>? = vo.getTasks()
            val size:Int = keys.size
            var i:Int = 0
            do{
                val key:String = keys[i]
                val include:((String, Any?) -> Boolean)? = tasks?.get(key)?.include
                val v:Any? = vo[key]
                println("key:$key, v:$v")
                if(v != null){
                    if(include == null || include(key, v)){
                        val field:Field<*> = fields[key] ?: ToVONoInitialized(vo, "key:$key, v:$v").terminate()
                        val converter:suspend FlowCollector<ByteArray>.(Any)->Unit = target[field::class] ?: ToNoConverter(vo, "field:${field::class.simpleName}, key:$key, v:$v").terminate()
                        converter(v)
                    }
                }else{
                    if(include == null || (include != Task.EXCLUDE && include != Task.OPTIONAL && include(key, v))) ToNullField(vo, "key:$key, v:$v").terminate()
                    else{
                        // 이 경우 옵셔널일 때만 ~를 마크함
                        if(include == Task.OPTIONAL){
                            println("optional key:$key, v:$v")
                            emit(OPTIONAL_NULL)
                        }
                    }

                }
            }while(++i < size)
        }
        target[VOSumField::class] = target[VOField::class]!!
        target[StringField::class] = {
            emit(writeBytes{
                writeString("$it")
                writeByte(0)
            })
        }
        target[IntField::class] = {emit(writeBytes{writeInt(it as Int)})}
        target[ShortField::class] = {emit(writeBytes{writeShort(it as Short)})}
        target[LongField::class] = {emit(writeBytes{writeLong(it as Long)})}
        target[UIntField::class] = {emit(writeBytes{writeUInt(it as UInt)})}
        target[UShortField::class] = {emit(writeBytes{writeUShort(it as UShort)})}
        target[ULongField::class] = {emit(writeBytes{writeULong(it as ULong)})}
        target[FloatField::class] = {emit(writeBytes{writeFloat(it as Float)})}
        target[DoubleField::class] = {emit(writeBytes{writeDouble(it as Double)})}
        target[BooleanField::class] = {emit(writeBytes{writeByte(if(it == true) 1 else 0)})}
        target[EnumField::class] = {emit(writeBytes{writeShort((it as Enum<*>).ordinal.toShort())})}
        fun getList(block:suspend FlowCollector<ByteArray>.(Any)->Unit):suspend FlowCollector<ByteArray>.(v: Any)->Unit = {
            it as List<Any>
            val size:Int = it.size
            var i:Int = 0
            emit(writeBytes{writeInt(it as Int)})
            do{
                block(it[i++])
            }while(i < size)
        }
        target[VOListField::class] = getList{binarify(VOField::class, it)}
        target[VOSumListField::class] = target[VOListField::class]!!
        target[EnumListField::class] = getList{binarify(EnumField::class, it)}
        val list:suspend FlowCollector<ByteArray>.(v: Any)->Unit = getList{binarify(it::class, it)}
        target[IntListField::class] = list
        target[ShortListField::class] = list
        target[LongListField::class] = list
        target[UIntListField::class] = list
        target[UShortListField::class] = list
        target[ULongListField::class] = list
        target[FloatListField::class] = list
        target[DoubleListField::class] = list
        target[BooleanListField::class] = list
        target[StringListField::class] = list
        fun getMap(block:suspend FlowCollector<ByteArray>.(Any)->Unit):suspend FlowCollector<ByteArray>.(v: Any)->Unit = {
            it as Map<String, Any>
            val keys:Array<String> = it.keys.toTypedArray()
            val size:Int = keys.size
            var i:Int = 0
            emit(writeBytes{writeInt(it as Int)})
            do{
                val key:String = keys[i++]
                emit(writeBytes{
                    writeString(key)
                    writeByte(0)
                })
                block(it[key]!!)
            }while(i < size)
        }
        target[VOMapField::class] = getMap{binarify(VOField::class, it)}
        target[VOSumMapField::class] = target[VOMapField::class]!!
        target[EnumMapField::class] = getMap{binarify(EnumField::class, it)}
        val map:suspend FlowCollector<ByteArray>.(v:Any)->Unit = getMap{binarify(it::class, it)}
        target[StringMapField::class] = map
        target[IntMapField::class] = map
        target[ShortMapField::class] = map
        target[LongMapField::class] = map
        target[UIntMapField::class] = map
        target[UShortMapField::class] = map
        target[ULongMapField::class] = map
        target[FloatMapField::class] = map
        target[DoubleMapField::class] = map
        target[BooleanListField::class] = map
    }
    internal fun parseValue(type:Any, field:Any, v:ByteArray):Pair<Any, ByteArray>? = (parsers[type] ?: throw Throwable("invalid parser type $type"))(field, v)
    private suspend inline fun FlowCollector<ByteArray>.binarify(type:KClass<*>, v:Any){
        (binarifier[type] ?: throw Throwable("invalid stringify type $type"))(v)
    }
    fun setParser(type:Any, parser:(Any, ByteArray)->Pair<Any, ByteArray>?){
        parsers[type] = parser}
    fun setBinarifier(type:KClass<*>, binary:suspend FlowCollector<ByteArray>.(Any)->Unit){
        binarifier[type] = binary
    }
    fun <V:VO> from(vo:V, value:Flow<ByteArray>):Flow<V> = flow{
        val emitter:FlowCollector<V> = this
        val parser:Parser<V> = Parser(vo)
        value.collect {
            var arr:ByteArray? = it
            while(arr != null) arr = parser(arr)?.let{(s, vo)->
                emitter.emit(vo)
                s
            }
        }
    }
    fun to(vo:VO):Flow<ByteArray> = flow{binarify(VOField::class, vo)}
}