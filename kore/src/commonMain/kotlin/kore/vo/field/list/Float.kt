@file:Suppress("NOTHING_TO_INLINE")

package kore.vo.field.list

import kore.vo.VO
import kore.vo.field.ListFields
import kore.vo.field.Prop
import kore.vo.field.list.FloatListField.T
import kore.vo.task.Task

object FloatListField:ListFields<Float>{
    override fun defaultFactory():MutableList<Float> = arrayListOf()
    class T:Task(){
        fun default(v:MutableList<Float>){
            _default = Task.Default{_,_->ArrayList<Float>(v.size).also{it.addAll(v)}}
        }
        fun default(vararg v:Float){
            _default = Task.Default{_,_->ArrayList<Float>(v.size).also{a->v.forEach{a.add(it)}}}
        }
    }
}
inline val VO.floatList:Prop<MutableList<Float>> get() = delegate(FloatListField)
inline fun VO.floatList(v:MutableList<Float>):Prop<MutableList<Float>>
= delegate(FloatListField){ T().also{it.default(v)}}
inline fun VO.floatList(vararg v:Float):Prop<MutableList<Float>>
= delegate(FloatListField){ T().also{it.default(*v)}}
inline fun VO.floatList(block: T.()->Unit):Prop<MutableList<Float>> = delegate(FloatListField, block){ T() }