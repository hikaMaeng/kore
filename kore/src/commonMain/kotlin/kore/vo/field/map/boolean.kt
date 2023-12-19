@file:Suppress("NOTHING_TO_INLINE")

package kore.vo.field.map

import kore.vo.VO
import kore.vo.field.Field
import kore.vo.field.Prop
import kore.vo.task.Task
import kore.vo.task.Task.Default

object BooleanMapField: Field<MutableMap<String, Boolean>> {
    override val typeName:String = "BooleanMap"
    class T: Task(){
        fun default(v:MutableMap<String, Boolean>){
            _default = Default{_,_->HashMap<String, Boolean>(v.size).also{it.putAll(v)}}
        }
        fun default(vararg v:Pair<String, Boolean>){
            _default = Default{_,_->HashMap<String, Boolean>(v.size).also{it.putAll(v)}}
        }
    }
}
inline val VO.booleanMap:Prop<MutableMap<String, Boolean>> get() = delegate(BooleanMapField)
inline fun VO.booleanMap(v:MutableMap<String, Boolean>):Prop<MutableMap<String, Boolean>>
= delegate(BooleanMapField){BooleanMapField.T().also{it.default(v)}}
inline fun VO.booleanMap(vararg v:Pair<String, Boolean>):Prop<MutableMap<String, Boolean>>
= delegate(BooleanMapField){BooleanMapField.T().also{it.default(*v)}}
inline fun VO.booleanMap(block: BooleanMapField.T.()->Unit):Prop<MutableMap<String, Boolean>> = delegate(BooleanMapField, block){BooleanMapField.T()}