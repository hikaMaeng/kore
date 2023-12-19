@file:Suppress("NOTHING_TO_INLINE")

package kore.vo.field.value

import kore.vo.VO
import kore.vo.field.Field
import kore.vo.field.Prop
import kore.vo.task.Task

object LongField: Field<Long> {
    override val typeName:String = "Long"
    class T: Task(){
        fun default(v:Long){
            _default = v
        }
    }
}
inline val VO.long:Prop<Long> get() = delegate(LongField)
inline fun VO.long(v:Long):Prop<Long> = delegate(LongField){ LongField.T().also{it.default(v)}}
inline fun VO.long(block: LongField.T.()->Unit):Prop<Long> = delegate(LongField, block){ LongField.T()}