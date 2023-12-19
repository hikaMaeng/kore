@file:Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")

package kore.vo.field

import kore.vo.VO
import kore.vo.VOSum
import kore.vo.task.Task
import kore.vo.task.Task.Default
import kotlin.reflect.KClass

class VOSumField<V: VO>(val cls:KClass<out VO>, val sum: VOSum<V>): Field<V>{
    override val typeName:String = "Sum"
    class T<V:VO>: Task(){
        fun default(block:()->V){
            _default = Default { _, _ -> block() }
        }
    }
    companion object{
        @PublishedApi internal val fields:HashMap<KClass<out VO>, VOSumField<out VO>> = hashMapOf()
        inline operator fun <V: VO> get(cls:KClass<out V>, sum: VOSum<V>): VOSumField<V> {
            return fields.getOrPut(cls){VOSumField(cls, sum)} as VOSumField<V>
        }
    }
}
inline fun <reified V:VO> VO.sum(sum:VOSum<V>): Prop<V> = delegate(VOSumField[V::class, sum])
inline fun <reified V:VO> VO.sum(sum:VOSum<V>, block: VOSumField.T<V>.()->Unit): Prop<V> = delegate(VOSumField[V::class, sum], block){ VOSumField.T() }
class VOSumListField<V: VO>(val cls:KClass<out VO>, val sum: VOSum<V>): Field<MutableList<V>>{
    override val typeName:String = "SumList"
    class T<V:VO>: Task(){
        fun default(block:()->MutableList<V>){
            _default = Default{ _, _->
                val v = block()
                ArrayList<V>(v.size).also{it.addAll(v)}
            }
        }
    }
    companion object{
        @PublishedApi internal val fields:HashMap<KClass<out VO>, VOSumListField<out VO>> = hashMapOf()
        inline operator fun <V: VO> get(cls:KClass<out V>, sum: VOSum<V>): VOSumListField<V> {
            return fields.getOrPut(cls){VOSumListField(cls, sum)} as VOSumListField<V>
        }
    }
}
inline fun <reified V:VO> VO.sumList(sum:VOSum<V>): Prop<MutableList<V>> = delegate(VOSumListField[V::class, sum])
inline fun <reified V:VO> VO.sumListDefault(sum:VOSum<V>, noinline block:()->MutableList<V>): Prop<MutableList<V>>
        = delegate(VOSumListField[V::class, sum]){ VOSumListField.T<V>().also{it.default(block)}}
inline fun <reified V:VO> VO.sumList(sum:VOSum<V>, block: VOSumListField.T<V>.()->Unit): Prop<MutableList<V>>
        = delegate(VOSumListField[V::class, sum], block){ VOSumListField.T() }
class VOSumMapField<V: VO>(val cls:KClass<out VO>, val sum: VOSum<V>): Field<MutableMap<String, V>>{
    override val typeName:String = "SumMap"
    class T<V:VO>: Task(){
        fun default(block:()->MutableMap<String, V>){
            _default = Default{_,_->
                val v = block()
                HashMap<String, V>(v.size).also{it.putAll(v)}
            }
        }
    }
    companion object{
        @PublishedApi internal val fields:HashMap<KClass<out VO>, VOSumMapField<out VO>> = hashMapOf()
        inline operator fun <V: VO> get(cls:KClass<out V>, sum: VOSum<V>): VOSumMapField<V> {
            return fields.getOrPut(cls){VOSumMapField(cls, sum)} as VOSumMapField<V>
        }
    }
}
inline fun <reified V:VO> VO.sumMap(sum:VOSum<V>): Prop<MutableMap<String, V>> = delegate(VOSumMapField[V::class, sum])
inline fun <reified V:VO> VO.sumMapDefault(sum:VOSum<V>, noinline block:()->MutableMap<String, V>): Prop<MutableMap<String, V>>
= delegate(VOSumMapField[V::class, sum]){ VOSumMapField.T<V>().also{it.default(block)}}
inline fun <reified V:VO> VO.sumMap(sum:VOSum<V>, block: VOSumMapField.T<V>.()->Unit): Prop<MutableMap<String, V>>
= delegate(VOSumMapField[V::class, sum], block){ VOSumMapField.T() }