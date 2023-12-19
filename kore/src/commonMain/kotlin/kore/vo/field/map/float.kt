@file:Suppress("NOTHING_TO_INLINE")

package kore.vo.field.map

import kore.vo.VO
import kore.vo.field.Field
import kore.vo.field.Prop
import kore.vo.task.Task
import kore.vo.task.Task.Default

object FloatMapField: Field<MutableMap<String, Float>> {
    override val typeName:String = "FloatMap"
    class T: Task(){
        fun default(v:MutableMap<String, Float>){
            _default = Default{_,_->HashMap<String, Float>(v.size).also{it.putAll(v)}}
        }
        fun default(vararg v:Pair<String, Float>){
            _default = Default{_,_->HashMap<String, Float>(v.size).also{it.putAll(v)}}
        }
    }
}
inline val VO.floatMap:Prop<MutableMap<String, Float>> get() = delegate(FloatMapField)
inline fun VO.floatMap(v:MutableMap<String, Float>):Prop<MutableMap<String, Float>>
        = delegate(FloatMapField){FloatMapField.T().also{it.default(v)}}
inline fun VO.floatMap(vararg v:Pair<String, Float>):Prop<MutableMap<String, Float>>
        = delegate(FloatMapField){FloatMapField.T().also{it.default(*v)}}
inline fun VO.floatMap(block: FloatMapField.T.()->Unit):Prop<MutableMap<String, Float>> = delegate(FloatMapField, block){FloatMapField.T()}