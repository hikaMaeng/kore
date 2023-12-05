@file:Suppress("NOTHING_TO_INLINE")

package kore.vo.field.value

import kore.vo.VO
import kore.vo.field.Field
import kore.vo.field.Prop
import kore.vo.task.Task

object UShortField: Field<UShort> {
    class T: Task(){
        fun default(v:UShort){
            _default = v
        }
    }
}
inline val VO.ushort:Prop<UShort> get() = delegate(UShortField)
inline fun VO.ushort(v:UShort):Prop<UShort> = delegate(UShortField){ UShortField.T().also{it.default(v)}}
inline fun VO.ushort(block: UShortField.T.()->Unit):Prop<UShort> = delegate(UShortField, block){ UShortField.T()}