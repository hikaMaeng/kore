@file:Suppress("NOTHING_TO_INLINE", "FunctionName", "UNCHECKED_CAST")

package kore.vbp

import kore.bytes.ByteQueue
import kore.vo.VO
import kore.vo.VOSum
import kore.vo.converter.ToNoConverter
import kore.vo.converter.ToNoEnum
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

        it[EnumField::class] = {field, v->
            if(v.size < 2) null else{
                val enums:Array<*> = when(field){
                    is EnumField<*>->field.enums
                    is EnumListField<*>->field.enums
                    is EnumMapField<*>->field.enums
                    else->throw Throwable("invalid enum field $field")
                }
                val index:Int = v.buffer(2){readShort()}.toInt()
                if(index < enums.size) enums[index]!! else ToNoEnum(enums, v).terminate()
            }
        }
        it[EnumListField::class] = it[EnumField::class]!!
        it[EnumMapField::class] = it[EnumField::class]!!
    }
    private val OPTIONAL_NULL:ByteArray = byteArrayOf(1.toByte())

    private val binarifier:HashMap<KClass<*>, suspend FlowCollector<ByteArray>.(Any, Any)->Unit> = HashMap<KClass<*>, suspend FlowCollector<ByteArray>.(Any, Any)->Unit>(50).also{target->
        target[VOField::class] = {it, f->
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
                    val converter:suspend FlowCollector<ByteArray>.(Any, Any)->Unit = target[field::class] ?: ToNoConverter(vo, "field:${field::class.simpleName}, key:$key, v:$v").terminate()
                    emit(byteArrayOf(i.toByte()))
                    converter(v, field)
                }
            }while(++i < size)
            emit(byteArrayOf(-1))
        }
        target[VOSumField::class] = {it, f->
            val sum:VOSum<VO> = when(f){
                is VOSumField<*>->f.sum
                is VOSumListField<*>->f.sum
                is VOSumMapField<*>->f.sum
                else->throw Throwable("invalid sum field $f")
            }
            emit(byteArrayOf(sum.type.indexOf(it::class).toByte()))
            target[VOField::class]!!(it, f)
        }
        target[StringField::class] = {it, f->
            emit(writeBytes{
                writeString("$it")
                writeByte(0)
            })
        }
        target[IntField::class] = {it, _->emit(writeBytes{writeInt(it as Int)})}
        target[ShortField::class] = {it, _->emit(writeBytes{writeShort(it as Short)})}
        target[LongField::class] = {it, _->emit(writeBytes{writeLong(it as Long)})}
        target[UIntField::class] = {it, _->emit(writeBytes{writeUInt(it as UInt)})}
        target[UShortField::class] = {it, _->emit(writeBytes{writeUShort(it as UShort)})}
        target[ULongField::class] = {it, _->emit(writeBytes{writeULong(it as ULong)})}
        target[FloatField::class] = {it, _->emit(writeBytes{writeFloat(it as Float)})}
        target[DoubleField::class] = {it, _->emit(writeBytes{writeDouble(it as Double)})}
        target[BooleanField::class] = {it, _->emit(writeBytes{writeByte(if(it == true) 1 else 0)})}
        target[EnumField::class] = {it, _->emit(writeBytes{writeShort((it as Enum<*>).ordinal.toShort())})}
        fun getList(block:suspend FlowCollector<ByteArray>.(Any, Any)->Unit):suspend FlowCollector<ByteArray>.(Any, Any)->Unit = {it, f->
            it as List<Any>
            val size:Int = it.size
            var i:Int = 0
            emit(writeBytes{writeInt(size)})
            do{
                block(it[i++], f)
            }while(i < size)
        }
        target[VOListField::class] = getList{it, f->binarify(VOField::class, it, f)}
        target[VOSumListField::class] = getList{it, f->binarify(VOSumField::class, it, f)}
        target[EnumListField::class] = getList{it, f->binarify(EnumField::class, it, f)}
        target[IntListField::class] = getList{it, f->binarify(IntField::class, it, f)}
        target[ShortListField::class] = getList{it, f->binarify(ShortField::class, it, f)}
        target[LongListField::class] = getList{it, f->binarify(LongField::class, it, f)}
        target[UIntListField::class] = getList{it, f->binarify(UIntField::class, it, f)}
        target[UShortListField::class] = getList{it, f->binarify(UShortField::class, it, f)}
        target[ULongListField::class] = getList{it, f->binarify(ULongField::class, it, f)}
        target[FloatListField::class] = getList{it, f->binarify(FloatField::class, it, f)}
        target[DoubleListField::class] = getList{it, f->binarify(DoubleField::class, it, f)}
        target[BooleanListField::class] = getList{it, f->binarify(BooleanField::class, it, f)}
        target[StringListField::class] = getList{it, f->binarify(StringField::class, it, f)}
        fun getMap(block:suspend FlowCollector<ByteArray>.(Any, Any)->Unit):suspend FlowCollector<ByteArray>.(Any, Any)->Unit = {it, f->
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
                block(it[key]!!, f)
            }while(i < size)
        }
        target[VOMapField::class] = getMap{it, f->binarify(VOField::class, it, f)}
        target[VOSumMapField::class] = getMap{it, f->binarify(VOSumField::class, it, f)}
        target[EnumMapField::class] = getMap{it, f->binarify(EnumField::class, it, f)}
        target[IntMapField::class] = getMap{it, f->binarify(IntField::class, it, f)}
        target[ShortMapField::class] = getMap{it, f->binarify(ShortField::class, it, f)}
        target[LongMapField::class] = getMap{it, f->binarify(LongField::class, it, f)}
        target[UIntMapField::class] = getMap{it, f->binarify(UIntField::class, it, f)}
        target[UShortMapField::class] = getMap{it, f->binarify(UShortField::class, it, f)}
        target[ULongMapField::class] = getMap{it, f->binarify(ULongField::class, it, f)}
        target[FloatMapField::class] = getMap{it, f->binarify(FloatField::class, it, f)}
        target[DoubleMapField::class] = getMap{it, f->binarify(DoubleField::class, it, f)}
        target[BooleanListField::class] = getMap{it, f->binarify(BooleanField::class, it, f)}
        target[StringMapField::class] = getMap{it, f->binarify(StringField::class, it, f)}
    }
    internal fun parseValue(type:Any, field:Any, v:ByteQueue):Any? = (parsers[type] ?: throw Throwable("invalid parser type $type, ${parsers[type]}, ${parsers[IntListField::class]}"))(field, v)
    private suspend inline fun FlowCollector<ByteArray>.binarify(type:KClass<*>, v:Any, field:Any){
        (binarifier[type] ?: throw Throwable("invalid stringify type $type"))(v, field)
    }
    fun setParser(type:Any, parser:(Any, ByteQueue)->Any?){
        parsers[type] = parser}
    fun setBinarifier(type:KClass<*>, binary:suspend FlowCollector<ByteArray>.(Any, Any)->Unit){
        binarifier[type] = binary
    }
    fun <V:VO> from(vo:V, value:Flow<ByteArray>):Flow<V> = flow{
        val emitter:FlowCollector<V> = this
        val parser:Parser<V> = Parser(vo)
        value.collect {
            var arr:ByteArray? = it
            while(arr != null) arr = parser(arr)?.let{(s, vo)->
//                println("from ${s.joinToString(",")}, ${vo}")
                emitter.emit(vo)
                s
            }
            emitter.emit(vo)
        }
    }
    fun to(vo:VO):Flow<ByteArray> = flow{binarify(VOField::class, vo, 0)}
}