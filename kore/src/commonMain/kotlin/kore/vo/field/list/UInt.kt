@file:Suppress("NOTHING_TO_INLINE")

package kore.vo.field.list

import kore.vo.VO
import kore.vo.field.Field
import kore.vo.field.Prop
import kore.vo.field.list.UIntListField.T
import kore.vo.task.Task

object UIntListField:Field<MutableList<UInt>>{
    override fun defaultFactory():MutableList<UInt> = arrayListOf()
    class T:Task(){
        fun default(v:MutableList<UInt>){
            _default = Task.Default{_,_->ArrayList<UInt>(v.size).also{it.addAll(v)}}
        }
        @OptIn(ExperimentalUnsignedTypes::class)
        fun default(vararg v:UInt){
            _default = Task.Default{_,_->ArrayList<UInt>(v.size).also{a->v.forEach{a.add(it)}}}
        }
    }
}
inline val VO.uintList:Prop<MutableList<UInt>> get() = delegate(UIntListField)
inline fun VO.uintList(v:MutableList<UInt>):Prop<MutableList<UInt>>
= delegate(UIntListField){ T().also{it.default(v)}}
@OptIn(ExperimentalUnsignedTypes::class)
inline fun VO.uintList(vararg v:UInt):Prop<MutableList<UInt>>
= delegate(UIntListField){ T().also{it.default(*v)}}
inline fun VO.uintList(block: T.()->Unit):Prop<MutableList<UInt>> = delegate(UIntListField, block){ T() }