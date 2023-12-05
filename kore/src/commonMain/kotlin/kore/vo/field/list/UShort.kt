@file:Suppress("NOTHING_TO_INLINE")

package kore.vo.field.list

import kore.vo.VO
import kore.vo.field.Field
import kore.vo.field.Prop
import kore.vo.task.Task

object UShortListField: Field<MutableList<UShort>> {
    class T: Task(){
        fun default(v:MutableList<UShort>){
            _default = Task.Default{_,_->ArrayList<UShort>(v.size).also{it.addAll(v)}}
        }
        @OptIn(ExperimentalUnsignedTypes::class)
        fun default(vararg v:UShort){
            _default = Task.Default{_,_->ArrayList<UShort>(v.size).also{a->v.forEach{a.add(it)}}}
        }
    }
}
inline val VO.ushortList:Prop<MutableList<UShort>> get() = delegate(UShortListField)
inline fun VO.ushortList(v:MutableList<UShort>):Prop<MutableList<UShort>>
= delegate(UShortListField){UShortListField.T().also{it.default(v)}}
@OptIn(ExperimentalUnsignedTypes::class)
inline fun VO.ushortList(vararg v:UShort): Prop<MutableList<UShort>>
= delegate(UShortListField){UShortListField.T().also{it.default(*v)}}
inline fun VO.ushortList(block: UShortListField.T.()->Unit):Prop<MutableList<UShort>> = delegate(UShortListField, block){UShortListField.T()}