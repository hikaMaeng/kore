@file:Suppress("NOTHING_TO_INLINE")

package kore.vo.field.map

import kore.vo.VO
import kore.vo.field.Field
import kore.vo.field.Prop
import kore.vo.task.Task
import kore.vo.task.Task.Default

object ShortMapField: Field<MutableMap<String, Short>> {
    class T: Task(){
        fun default(v:MutableMap<String, Short>){
            _default = Default{_,_->HashMap<String, Short>(v.size).also{it.putAll(v)}}
        }
        fun default(vararg v:Pair<String, Short>){
            _default = Default{_,_->HashMap<String, Short>(v.size).also{it.putAll(v)}}
        }
    }
}
inline val VO.shortMap:Prop<MutableMap<String, Short>> get() = delegate(ShortMapField)
inline fun VO.shortMap(v:MutableMap<String, Short>):Prop<MutableMap<String, Short>>
        = delegate(ShortMapField){ShortMapField.T().also{it.default(v)}}
inline fun VO.shortMap(vararg v:Pair<String, Short>):Prop<MutableMap<String, Short>>
        = delegate(ShortMapField){ShortMapField.T().also{it.default(*v)}}
inline fun VO.shortMap(block: ShortMapField.T.()->Unit):Prop<MutableMap<String, Short>> = delegate(ShortMapField, block){ShortMapField.T()}