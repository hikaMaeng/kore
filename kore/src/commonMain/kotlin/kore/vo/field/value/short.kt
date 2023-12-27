@file:Suppress("NOTHING_TO_INLINE")

package kore.vo.field.value

import kore.vo.VO
import kore.vo.field.Field
import kore.vo.field.Prop
import kore.vo.task.Task

object ShortField:Field<Short>{
    override fun defaultFactory():Short = 0
    class T:Task(){
        fun default(v:Short){
            _default = v
        }
    }
}
inline val VO.short:Prop<Short> get() = delegate(ShortField)
inline fun VO.short(v:Short):Prop<Short> = delegate(ShortField){ ShortField.T().also{it.default(v)}}
inline fun VO.short(block: ShortField.T.()->Unit):Prop<Short> = delegate(ShortField, block){ ShortField.T()}