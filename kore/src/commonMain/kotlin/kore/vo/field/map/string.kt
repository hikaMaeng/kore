@file:Suppress("NOTHING_TO_INLINE")

package kore.vo.field.map

import kore.vo.VO
import kore.vo.field.Field
import kore.vo.field.Prop
import kore.vo.task.Task
import kore.vo.task.Task.Default

object StringMapField: Field<MutableMap<String, String>> {
    class T: Task(){
        fun default(v:MutableMap<String, String>){
            _default = Default{_,_->HashMap<String, String>(v.size).also{it.putAll(v)}}
        }
        fun default(vararg v:Pair<String, String>){
            _default = Default{_,_->HashMap<String, String>(v.size).also{it.putAll(v)}}
        }
    }
}
inline val VO.stringMap:Prop<MutableMap<String, String>> get() = delegate(StringMapField)
inline fun VO.stringMap(v:MutableMap<String, String>):Prop<MutableMap<String, String>>
        = delegate(StringMapField){StringMapField.T().also{it.default(v)}}
inline fun VO.stringMap(vararg v:Pair<String, String>):Prop<MutableMap<String, String>>
        = delegate(StringMapField){StringMapField.T().also{it.default(*v)}}
inline fun VO.stringMap(block: StringMapField.T.()->Unit):Prop<MutableMap<String, String>> = delegate(StringMapField, block){StringMapField.T()}