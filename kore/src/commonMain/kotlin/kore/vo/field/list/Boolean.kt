@file:Suppress("NOTHING_TO_INLINE")

package kore.vo.field.list

import kore.vo.VO
import kore.vo.field.Field
import kore.vo.field.Prop
import kore.vo.field.list.BooleanListField.T
import kore.vo.task.Task

object BooleanListField:Field<MutableList<Boolean>>{
    override fun defaultFactory():MutableList<Boolean> = arrayListOf()
    class T:Task(){
        fun default(v:MutableList<Boolean>){
            _default = Task.Default{_,_->ArrayList<Boolean>(v.size).also{it.addAll(v)}}
        }
        fun default(vararg v:Boolean){
            _default = Task.Default{_,_->ArrayList<Boolean>(v.size).also{a->v.forEach{a.add(it)}}}
        }
    }
}
inline val VO.booleanList:Prop<MutableList<Boolean>> get() = delegate(BooleanListField)
inline fun VO.booleanList(v:MutableList<Boolean>):Prop<MutableList<Boolean>>
= delegate(BooleanListField){ T().also{it.default(v)}}
inline fun VO.booleanList(vararg v:Boolean):Prop<MutableList<Boolean>>
= delegate(BooleanListField){ T().also{it.default(*v)}}
inline fun VO.booleanList(block: T.()->Unit):Prop<MutableList<Boolean>> = delegate(BooleanListField, block){ T() }