@file:Suppress("NOTHING_TO_INLINE")

package kore.vo.field.value

import kore.vo.VO
import kore.vo.field.Field
import kore.vo.field.Prop
import kore.vo.task.Task

object IntField: Field<Int> {
    class T:Task(){
        fun default(v:Int){
            _default = v
        }
    }
}
inline val VO.int:Prop<Int> get() = delegate(IntField)
inline fun VO.int(v:Int):Prop<Int> = delegate(IntField){IntField.T().also{it.default(v)}}
inline fun VO.int(block:IntField.T.()->Unit):Prop<Int> = delegate(IntField, block){ IntField.T() }