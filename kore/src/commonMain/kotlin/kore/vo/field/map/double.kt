@file:Suppress("NOTHING_TO_INLINE")

package kore.vo.field.map

import kore.vo.VO
import kore.vo.field.MapFields
import kore.vo.field.Prop
import kore.vo.task.Task
import kore.vo.task.Task.Default

object DoubleMapField:MapFields<Double> {
    override fun defaultFactory():MutableMap<String, Double> = hashMapOf()
    class T:Task(){
        fun default(v:MutableMap<String, Double>){
            _default = Default{_,_->HashMap<String, Double>(v.size).also{it.putAll(v)}}
        }
        fun default(vararg v:Pair<String, Double>){
            _default = Default{_,_->HashMap<String, Double>(v.size).also{it.putAll(v)}}
        }
    }
}
inline val VO.doubleMap:Prop<MutableMap<String, Double>> get() = delegate(DoubleMapField)
inline fun VO.doubleMap(v:MutableMap<String, Double>):Prop<MutableMap<String, Double>>
        = delegate(DoubleMapField){DoubleMapField.T().also{it.default(v)}}
inline fun VO.doubleMap(vararg v:Pair<String, Double>):Prop<MutableMap<String, Double>>
        = delegate(DoubleMapField){DoubleMapField.T().also{it.default(*v)}}
inline fun VO.doubleMap(block: DoubleMapField.T.()->Unit):Prop<MutableMap<String, Double>> = delegate(DoubleMapField, block){DoubleMapField.T()}