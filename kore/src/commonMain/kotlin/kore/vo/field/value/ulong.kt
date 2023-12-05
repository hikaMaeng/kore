@file:Suppress("NOTHING_TO_INLINE")

package kore.vo.field.value

import kore.vo.VO
import kore.vo.field.Field
import kore.vo.field.Prop
import kore.vo.task.Task

object ULongField: Field<ULong> {
    class T: Task(){
        fun default(v:ULong){
            _default = v
        }
    }
}
inline val VO.ulong:Prop<ULong> get() = delegate(ULongField)
inline fun VO.ulong(v:ULong):Prop<ULong> = delegate(ULongField){ ULongField.T().also{it.default(v)}}
inline fun VO.ulong(block: ULongField.T.()->Unit):Prop<ULong> = delegate(ULongField, block){ ULongField.T()}