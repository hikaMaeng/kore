@file:Suppress("NOTHING_TO_INLINE", "FunctionName", "UNCHECKED_CAST")

package kore.vjson

import kore.vo.VO
import kore.vo.converter.ToVONoInitialized
import kore.vo.field.Field
import kore.vo.field.VOField
import kore.vo.field.list.*
import kore.vo.field.map.*
import kore.vo.field.value.*
import kore.vo.task.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlin.reflect.KClass

object JSON{
    private val parsers:HashMap<Any, (String)->Any> = HashMap<Any, (String)->Any>(100).also{
        val cInt:(String)->Any = {it.toInt()}
        val cShort:(String)->Any = {it.toShort()}
        val cLong:(String)->Any = {it.toLong()}
        val cFloat:(String)->Any = {it.toFloat()}
        val cDouble:(String)->Any = {it.toDouble()}
        val cBoolean:(String)->Any = {it.toBoolean()}
        val cUInt:(String)->Any = {it.toUInt()}
        val cUShort:(String)->Any = {it.toUShort()}
        val cULong:(String)->Any = {it.toULong()}
        val cString:(String)->Any = {it}

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

    }
    private val emitters:HashMap<KClass<*>, suspend FlowCollector<String>.(Any)->Unit> = HashMap<KClass<*>, suspend FlowCollector<String>.(Any)->Unit>(50).also{ map->
        map[VOField::class] = emitVO
        val emitValue:suspend FlowCollector<String>.(v:Any)->Unit = ToEmitter(0).f
        arrayOf<KClass<*>>(IntField::class, IntField::class, ShortField::class, LongField::class, UIntField::class, UShortField::class, ULongField::class, FloatField::class, DoubleField::class, BooleanField::class).forEach {
            map[it] = emitValue
        }
        map[StringField::class] = ToEmitter(1).f
        val emitList:suspend FlowCollector<String>.(v: Any)->Unit = ToEmitter(2).f
        arrayOf<KClass<*>>(StringListField::class, IntListField::class, ShortListField::class, LongListField::class, UIntListField::class, UShortListField::class, ULongListField::class, FloatListField::class, DoubleListField::class, BooleanListField::class).forEach {
            map[it] = emitList
        }
        val emitMap:suspend FlowCollector<String>.(v:Any)->Unit = ToEmitter(3).f
        arrayOf<KClass<*>>(StringMapField::class, IntMapField::class, ShortMapField::class, LongMapField::class, UIntMapField::class, UShortMapField::class, ULongMapField::class, FloatMapField::class, DoubleMapField::class, BooleanListField::class).forEach {
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
    fun setParser(type:Any, parser:(String)->Any){parsers[type] = parser}
    fun parseValue(type:Any, v:String):Any = (parsers[type] ?: throw Throwable("invalid parser type $type"))(v)
    fun setStringify(type:KClass<*>, stringify:suspend FlowCollector<String>.(Any)->Unit){emitters[type] = stringify}
    fun getStringify(type:KClass<*>):suspend FlowCollector<String>.(Any)->Unit = emitters[type] ?: throw Throwable("invalid stringify type $type")
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
    fun to(vo:VO):Flow<String> = flow{emitVO(vo)}
}
