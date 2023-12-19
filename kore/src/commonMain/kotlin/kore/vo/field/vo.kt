@file:Suppress("NOTHING_TO_INLINE", "UNCHECKED_CAST")

package kore.vo.field

import kore.vo.VO
import kore.vo.task.Task
import kore.vo.task.Task.Default
import kotlin.reflect.KClass

class VOField<V: VO>(val cls: KClass<V>, val factory:()->V): Field<V>{
    override val typeName:String = "VO"
    class T<V:VO>: Task(){
        fun default(block:()->V){
            _default = Default { _, _ -> block() }
        }
    }
    companion object{
        @PublishedApi internal val fields:HashMap<KClass<out VO>, VOField<out VO>> = hashMapOf()
        inline operator fun <V: VO> get(cls:KClass<V>, noinline factory:()->V): VOField<V> = fields.getOrPut(cls){VOField(cls, factory)} as VOField<V>
    }
}
inline fun <reified V:VO> VO.vo(noinline factory:()->V): Prop<V> = delegate(VOField[V::class, factory])
inline fun <reified V:VO> VO.vo(noinline factory:()->V, block: VOField.T<V>.()->Unit): Prop<V> = delegate(VOField[V::class, factory], block){ VOField.T() }
class VOListField<V: VO>(val cls: KClass<V>, val factory:()->V): Field<MutableList<V>>{
    override val typeName:String = "VOList"
    class T<V:VO>: Task(){
        fun default(block:()->MutableList<V>){
            _default = Default{ _, _->
                val v = block()
                ArrayList<V>(v.size).also{it.addAll(v)}
            }
        }
    }
    companion object{
        @PublishedApi internal val fields:HashMap<KClass<out VO>, VOListField<out VO>> = hashMapOf()
        inline operator fun <reified V: VO> get(cls:KClass<V>, noinline factory:()->V): VOListField<V> {
            return fields.getOrPut(V::class){VOListField(cls, factory)} as VOListField<V>
        }
    }
}
inline fun <reified V:VO> VO.voList(noinline factory:()->V): Prop<MutableList<V>> = delegate(VOListField[V::class, factory])
inline fun <reified V:VO> VO.voListDefault(noinline factory:()->V, noinline block:()->MutableList<V>): Prop<MutableList<V>>
= delegate(VOListField[V::class, factory]){ VOListField.T<V>().also{it.default(block)}}
inline fun <reified V:VO> VO.voList(noinline factory:()->V, block: VOListField.T<V>.()->Unit): Prop<MutableList<V>>
= delegate(VOListField[V::class, factory], block){ VOListField.T() }
class VOMapField<V: VO>(val cls: KClass<V>, val factory:()->V): Field<MutableMap<String, V>>{
    override val typeName:String = "VOMap"
    class T<V:VO>: Task(){
        fun default(block:()->MutableMap<String, V>){
            _default = Default{_,_->
                val v = block()
                HashMap<String, V>(v.size).also{it.putAll(v)}
            }
        }
    }
    companion object{
        @PublishedApi internal val fields:HashMap<KClass<out VO>, VOMapField<out VO>> = hashMapOf()
        inline operator fun <V: VO> get(cls:KClass<V>, noinline factory:()->V): VOMapField<V> {
            return fields.getOrPut(cls){VOMapField(cls, factory)} as VOMapField<V>
        }
    }
}
inline fun <reified V:VO> VO.voMap(noinline factory:()->V): Prop<MutableMap<String, V>> = delegate(VOMapField[V::class, factory])
inline fun <reified V:VO> VO.voMapDefault(noinline factory:()->V, noinline block:()->MutableMap<String, V>): Prop<MutableMap<String, V>>
= delegate(VOMapField[V::class, factory]){ VOMapField.T<V>().also{it.default(block)}}
inline fun <reified V:VO> VO.voMap(noinline factory:()->V, block: VOMapField.T<V>.()->Unit): Prop<MutableMap<String, V>>
= delegate(VOMapField[V::class, factory], block){ VOMapField.T() }