@file:Suppress("NOTHING_TO_INLINE")

package kore.vo.field.map

import kore.vo.VO
import kore.vo.field.Field
import kore.vo.field.Prop
import kore.vo.task.Task
import kore.vo.task.Task.Default

object LongMapField: Field<MutableMap<String, Long>> {
    override val typeName:String = "LongMap"
    class T: Task(){
        fun default(v:MutableMap<String, Long>){
            _default = Default{_,_->HashMap<String, Long>(v.size).also{it.putAll(v)}}
        }
        fun default(vararg v:Pair<String, Long>){
            _default = Default{_,_->HashMap<String, Long>(v.size).also{it.putAll(v)}}
        }
    }
}
inline val VO.longMap:Prop<MutableMap<String, Long>> get() = delegate(LongMapField)
inline fun VO.longMap(v:MutableMap<String, Long>):Prop<MutableMap<String, Long>>
        = delegate(LongMapField){LongMapField.T().also{it.default(v)}}
inline fun VO.longMap(vararg v:Pair<String, Long>):Prop<MutableMap<String, Long>>
        = delegate(LongMapField){LongMapField.T().also{it.default(*v)}}
inline fun VO.longMap(block: LongMapField.T.()->Unit):Prop<MutableMap<String, Long>> = delegate(LongMapField, block){LongMapField.T()}