@file:Suppress("NOTHING_TO_INLINE")

package kore.vo.field.map

import kore.vo.VO
import kore.vo.field.Field
import kore.vo.field.Prop
import kore.vo.task.Task
import kore.vo.task.Task.Default

object UIntMapField:Field<MutableMap<String, UInt>>{
    override fun defaultFactory():MutableMap<String, UInt> = hashMapOf()
    class T:Task(){
        fun default(v:MutableMap<String, UInt>){
            _default = Default{_,_->HashMap<String, UInt>(v.size).also{it.putAll(v)}}
        }
        fun default(vararg v:Pair<String, UInt>){
            _default = Default{_,_->HashMap<String, UInt>(v.size).also{it.putAll(v)}}
        }
    }
}
inline val VO.uintMap:Prop<MutableMap<String, UInt>> get() = delegate(UIntMapField)
inline fun VO.uintMap(v:MutableMap<String, UInt>):Prop<MutableMap<String, UInt>>
        = delegate(UIntMapField){UIntMapField.T().also{it.default(v)}}
inline fun VO.uintMap(vararg v:Pair<String, UInt>):Prop<MutableMap<String, UInt>>
        = delegate(UIntMapField){UIntMapField.T().also{it.default(*v)}}
inline fun VO.uintMap(block: UIntMapField.T.()->Unit):Prop<MutableMap<String, UInt>> = delegate(UIntMapField, block){UIntMapField.T()}