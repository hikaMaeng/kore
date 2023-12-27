@file:Suppress("NOTHING_TO_INLINE")

package kore.vo.field.value

import kore.vo.VO
import kore.vo.field.Field
import kore.vo.field.Prop
import kore.vo.task.Task

object UIntField:Field<UInt>{
    override fun defaultFactory():UInt = 0u
    class T:Task(){
        fun default(v:UInt){
            _default = v
        }
    }
}
inline val VO.uint:Prop<UInt> get() = delegate(UIntField)
inline fun VO.uint(v:UInt):Prop<UInt> = delegate(UIntField){ UIntField.T().also{it.default(v)}}
inline fun VO.uint(block: UIntField.T.()->Unit):Prop<UInt> = delegate(UIntField, block){ UIntField.T()}