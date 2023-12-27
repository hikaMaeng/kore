@file:Suppress("NOTHING_TO_INLINE")

package kore.vo.field.value

import kore.vo.VO
import kore.vo.field.Field
import kore.vo.field.Prop
import kore.vo.task.Task

object BooleanField:Field<Boolean>{
    override fun defaultFactory():Boolean = true
    class T:Task(){
        fun default(v:Boolean){
            _default = v
        }
    }
}
inline val VO.boolean:Prop<Boolean> get() = delegate(BooleanField)
inline fun VO.boolean(v:Boolean):Prop<Boolean> = delegate(BooleanField){ BooleanField.T().also{it.default(v)}}
inline fun VO.boolean(block: BooleanField.T.()->Unit):Prop<Boolean> = delegate(BooleanField, block){ BooleanField.T()}