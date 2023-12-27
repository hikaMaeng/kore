@file:Suppress("NOTHING_TO_INLINE")

package kore.vo.field.map

import kore.vo.VO
import kore.vo.field.Field
import kore.vo.field.Prop
import kore.vo.task.Task
import kore.vo.task.Task.Default

object ULongMapField:Field<MutableMap<String, ULong>>{
    override fun defaultFactory():MutableMap<String, ULong> = hashMapOf()
    class T:Task(){
        fun default(v:MutableMap<String, ULong>){
            _default = Default{_,_->HashMap<String, ULong>(v.size).also{it.putAll(v)}}
        }
        fun default(vararg v:Pair<String, ULong>){
            _default = Default{_,_->HashMap<String, ULong>(v.size).also{it.putAll(v)}}
        }
    }
}
inline val VO.ulongMap:Prop<MutableMap<String, ULong>> get() = delegate(ULongMapField)
inline fun VO.ulongMap(v:MutableMap<String, ULong>):Prop<MutableMap<String, ULong>>
        = delegate(ULongMapField){ULongMapField.T().also{it.default(v)}}
inline fun VO.ulongMap(vararg v:Pair<String, ULong>):Prop<MutableMap<String, ULong>>
        = delegate(ULongMapField){ULongMapField.T().also{it.default(*v)}}
inline fun VO.ulongMap(block: ULongMapField.T.()->Unit):Prop<MutableMap<String, ULong>> = delegate(ULongMapField, block){ULongMapField.T()}