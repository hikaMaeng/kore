@file:Suppress("NOTHING_TO_INLINE", "FunctionName", "UNCHECKED_CAST")

package kore.vbp

import kore.vo.VO
import kore.vo.converter.ToNoConverter
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
    private val parsers:HashMap<Any, (Any, ByteQueue)->Any?> = HashMap<Any, (Any, ByteQueue)->Any?>(100).also{
        val cInt:(Any, ByteQueue)->Any? = {_, v->
            if(v.size < 4) null else v.buffer(4){readInt()}
        }
        val cShort:(Any, ByteQueue)->Any? = {_, v->
            if(v.size < 2) null else v.buffer(2){readShort()}
        }
        val cLong:(Any, ByteQueue)->Any? = {_, v->
            if(v.size < 8) null else v.buffer(8){readLong()}
        }
        val cFloat:(Any, ByteQueue)->Any? = {_, v->
            if(v.size < 4) null else v.buffer(4){readFloat()}
        }
        val cDouble:(Any, ByteQueue)->Any? = {_, v->
            if(v.size < 8) null else v.buffer(8){readDouble()}
        }
        val cBoolean:(Any, ByteQueue)->Any? = {_, v->
            v.buffer(1){readByte().toInt() == 1}
        }
        val cUInt:(Any, ByteQueue)->Any? = {_, v->
            if(v.size < 4) null else v.buffer(4){readUInt()}
        }
        val cUShort:(Any, ByteQueue)->Any? = {_, v->
            if(v.size < 2) null else v.buffer(2){readUShort()}
        }
        val cULong:(Any, ByteQueue)->Any? = {_, v->
            if(v.size < 8) null else v.buffer(8){readULong()}
        }
        val cString:(Any, ByteQueue)->Any? = {_, v->
            val index:Int = v.indexOf(0.toByte())
            if(index == -1) null else v.buffer(index + 1){readString()}.dropLast(1)
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
                if(v != null && (include == null || include(key, v))){
                    val field:Field<*> = fields[key] ?: ToVONoInitialized(vo, "key:$key, v:$v").terminate()
                    val converter:suspend FlowCollector<ByteArray>.(Any)->Unit = target[field::class] ?: ToNoConverter(vo, "field:${field::class.simpleName}, key:$key, v:$v").terminate()
                    emit(byteArrayOf(i.toByte()))
                    converter(v)
                }
            }while(++i < size)
            emit(byteArrayOf(-1))
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
            emit(writeBytes{writeInt(size)})
            do{
                block(it[i++])
            }while(i < size)
        }
        target[VOListField::class] = getList{binarify(VOField::class, it)}
        target[VOSumListField::class] = target[VOListField::class]!!
        target[EnumListField::class] = getList{binarify(EnumField::class, it)}
        target[IntListField::class] = getList{binarify(IntField::class, it)}
        target[ShortListField::class] = getList{binarify(ShortField::class, it)}
        target[LongListField::class] = getList{binarify(LongField::class, it)}
        target[UIntListField::class] = getList{binarify(UIntField::class, it)}
        target[UShortListField::class] = getList{binarify(UShortField::class, it)}
        target[ULongListField::class] = getList{binarify(ULongField::class, it)}
        target[FloatListField::class] = getList{binarify(FloatField::class, it)}
        target[DoubleListField::class] = getList{binarify(DoubleField::class, it)}
        target[BooleanListField::class] = getList{binarify(BooleanField::class, it)}
        target[StringListField::class] = getList{binarify(StringField::class, it)}
        fun getMap(block:suspend FlowCollector<ByteArray>.(Any)->Unit):suspend FlowCollector<ByteArray>.(v: Any)->Unit = {
            it as Map<String, Any>
            val keys:Array<String> = it.keys.toTypedArray()
            val size:Int = keys.size
            var i:Int = 0
            emit(writeBytes{writeInt(size)})
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
        target[IntMapField::class] = getMap{binarify(IntField::class, it)}
        target[ShortMapField::class] = getMap{binarify(ShortField::class, it)}
        target[LongMapField::class] = getMap{binarify(LongField::class, it)}
        target[UIntMapField::class] = getMap{binarify(UIntField::class, it)}
        target[UShortMapField::class] = getMap{binarify(UShortField::class, it)}
        target[ULongMapField::class] = getMap{binarify(ULongField::class, it)}
        target[FloatMapField::class] = getMap{binarify(FloatField::class, it)}
        target[DoubleMapField::class] = getMap{binarify(DoubleField::class, it)}
        target[BooleanListField::class] = getMap{binarify(BooleanField::class, it)}
        target[StringMapField::class] = getMap{binarify(StringField::class, it)}
    }
    internal fun parseValue(type:Any, field:Any, v:ByteQueue):Any? = (parsers[type] ?: throw Throwable("invalid parser type $type, ${parsers[type]}, ${parsers[IntListField::class]}"))(field, v)
    private suspend inline fun FlowCollector<ByteArray>.binarify(type:KClass<*>, v:Any){
        (binarifier[type] ?: throw Throwable("invalid stringify type $type"))(v)
    }
    fun setParser(type:Any, parser:(Any, ByteQueue)->Any?){
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
                println("from ${s.joinToString(",")}, ${vo}")
                emitter.emit(vo)
                s
            }
            emitter.emit(vo)
        }
    }
    fun to(vo:VO):Flow<ByteArray> = flow{binarify(VOField::class, vo)}
}