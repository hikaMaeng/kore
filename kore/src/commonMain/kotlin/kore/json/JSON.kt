@file:Suppress("NOTHING_TO_INLINE", "FunctionName", "UNCHECKED_CAST")

package kore.json

import kore.vo.VO
import kore.vo.converter.ToNoConverter
import kore.vo.converter.ToNoEnum
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
import kotlin.reflect.KClass

object JSON{
    private val parsers:HashMap<Any, (Any, String)->Any> = HashMap<Any, (Any, String)->Any>(100).also{
        val cInt:(Any, String)->Any = {_, v->v.toInt()}
        val cShort:(Any, String)->Any = {_, v->v.toShort()}
        val cLong:(Any, String)->Any = {_, v->v.toLong()}
        val cFloat:(Any, String)->Any = {_, v->v.toFloat()}
        val cDouble:(Any, String)->Any = {_, v->v.toDouble()}
        val cBoolean:(Any, String)->Any = {_, v->v.toBoolean()}
        val cUInt:(Any, String)->Any = {_, v->v.toUInt()}
        val cUShort:(Any, String)->Any = {_, v->v.toUShort()}
        val cULong:(Any, String)->Any = {_, v->v.toULong()}
        val cString:(Any, String)->Any = {_, v->v}

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
            val enums:Array<*> = (field as EnumField<*>).enums
            val index:Int = v.toInt()
            if(index < enums.size) enums[index]!! else ToNoEnum(enums, v).terminate()
        }
        it[EnumListField::class] = {field, v->
            val enums:Array<*> = (field as EnumListField<*>).enums
            val index:Int = v.toInt()
            if(index < enums.size) enums[index]!! else ToNoEnum(enums, v).terminate()
        }
        it[EnumMapField::class] = {field, v->
            val enums:Array<*> = (field as EnumListField<*>).enums
            val index:Int = v.toInt()
            if(index < enums.size) enums[index]!! else ToNoEnum(enums, v).terminate()
        }
    }
    private val stringifier:HashMap<KClass<*>, suspend FlowCollector<String>.(Any)->Unit> = HashMap<KClass<*>, suspend FlowCollector<String>.(Any)->Unit>(50).also{target->
        target[VOField::class] = {
            val vo:VO = it as VO
            val fields:HashMap<String, Field<*>> = vo.getFields() ?: ToVONoInitialized(vo, "no fields").terminate()
            val keys:List<String> = VO.keys(vo) ?: ToVONoInitialized(vo, "no keys").terminate()
            val tasks:HashMap<String, Task>? = vo.getTasks()
            emit("{")
            val size:Int = keys.size
            var i:Int = 0
            do{
                val key:String = keys[i]
                val include:((String, Any?) -> Boolean)? = tasks?.get(key)?.include
                val v:Any? = vo[key]
                if(v != null){
                    if(include == null || include(key, v)){
                        val field:Field<*> = fields[key] ?: ToVONoInitialized(vo, "key:$key, v:$v").terminate()
                        val converter:suspend FlowCollector<String>.(Any)->Unit = target[field::class] ?: ToNoConverter(vo, "field:${field::class.simpleName}, key:$key, v:$v").terminate()
                        if(i != 0) emit(",")
                        emit("\"$key\":")
                        converter(v)
                    }
                }else{
                    if(include?.let{f->f == Task.EXCLUDE || f == Task.OPTIONAL || f(key, v)} != false) ToNullField(vo, "key:$key, v:$v").terminate()
                }
            }while(++i < size)
            emit("}")
        }
        target[VOSumField::class] = target[VOField::class]!!

        val value:suspend FlowCollector<String>.(v:Any)->Unit = {emit("$it")}
        target[IntField::class] = value
        target[IntField::class] = value
        target[ShortField::class] = value
        target[LongField::class] = value
        target[UIntField::class] = value
        target[UShortField::class] = value
        target[ULongField::class] = value
        target[FloatField::class] = value
        target[DoubleField::class] = value
        target[BooleanField::class] = value
        target[StringField::class] = {emit("\"$it\"")}
        target[EnumField::class] = {emit("${(it as Enum<*>).ordinal}")}
        fun getList(block:suspend FlowCollector<String>.(Any)->Unit):suspend FlowCollector<String>.(v: Any)->Unit = {
            it as List<Any>
            emit("[")
            val size:Int = it.size
            var i:Int = 0
            do{
                if(i != 0) emit(",")
                block(it[i++])
            }while(i < size)
            emit("]")
        }
        target[VOListField::class] = getList{stringify(VOField::class, it)}
        target[VOSumListField::class] = target[VOListField::class]!!
        target[EnumListField::class] = getList{stringify(EnumField::class, it)}
        val list:suspend FlowCollector<String>.(v: Any)->Unit = getList{stringify(it::class, it)}
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
        fun getMap(block:suspend FlowCollector<String>.(Any)->Unit):suspend FlowCollector<String>.(v: Any)->Unit = {
            it as Map<String, Any>
            val keys:Array<String> = it.keys.toTypedArray()
            emit("{")
            val size:Int = keys.size
            var i:Int = 0
            do{
                if(i != 0) emit(",")
                val key:String = keys[i++]
                emit("\"$key\":")
                block(it[key]!!)
            }while(i < size)
            emit("}")
        }
        target[VOMapField::class] = getMap{stringify(VOField::class, it)}
        target[VOSumMapField::class] = target[VOMapField::class]!!
        target[EnumMapField::class] = getMap{stringify(EnumField::class, it)}
        val map:suspend FlowCollector<String>.(v:Any)->Unit = getMap{stringify(it::class, it)}
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
    internal fun parseValue(type:Any, field:Any, v:String):Any = (parsers[type] ?: throw Throwable("invalid parser type $type"))(field, v)
    private suspend inline fun FlowCollector<String>.stringify(type:KClass<*>, v:Any){
        (stringifier[type] ?: throw Throwable("invalid stringify type $type"))(v)
    }
    fun setParser(type:Any, parser:(Any, String)->Any){parsers[type] = parser}
    fun setStringifier(type:KClass<*>, stringifier:suspend FlowCollector<String>.(Any)->Unit){
        this.stringifier[type] = stringifier}
    fun <V:VO> from(vo:V, value:Flow<String>):Flow<V> = flow{
        val emitter:FlowCollector<V> = this
        val parser:Parser<V> = Parser(vo)
        value.collect {
            var str:String? = it
            while(str != null) str = parser(str)?.let{(s, vo)->
                emitter.emit(vo)
                s
            }
        }
    }
    fun to(vo:VO):Flow<String> = flow{stringify(VOField::class, vo)}
}