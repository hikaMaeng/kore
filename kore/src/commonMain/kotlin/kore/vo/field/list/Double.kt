@file:Suppress("NOTHING_TO_INLINE")

package kore.vo.field.list

import kore.vo.VO
import kore.vo.field.ListFields
import kore.vo.field.Prop
import kore.vo.field.list.DoubleListField.T
import kore.vo.task.Task

object DoubleListField:ListFields<Double> {
    override fun defaultFactory():MutableList<Double> = arrayListOf()
    class T:Task(){
        fun default(v:MutableList<Double>){
            _default = Task.Default{_,_->ArrayList<Double>(v.size).also{it.addAll(v)}}
        }
        fun default(vararg v:Double){
            _default = Task.Default{_,_->ArrayList<Double>(v.size).also{a->v.forEach{a.add(it)}}}
        }
    }
}
inline val VO.doubleList:Prop<MutableList<Double>> get() = delegate(DoubleListField)
inline fun VO.doubleList(v:MutableList<Double>):Prop<MutableList<Double>>
= delegate(DoubleListField){ T().also{it.default(v)}}
inline fun VO.doubleList(vararg v:Double):Prop<MutableList<Double>>
= delegate(DoubleListField){ T().also{it.default(*v)}}
inline fun VO.doubleList(block: T.()->Unit):Prop<MutableList<Double>> = delegate(DoubleListField, block){ T() }