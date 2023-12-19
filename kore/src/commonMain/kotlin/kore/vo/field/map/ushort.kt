@file:Suppress("NOTHING_TO_INLINE")

package kore.vo.field.map

import kore.vo.VO
import kore.vo.field.Field
import kore.vo.field.Prop
import kore.vo.task.Task
import kore.vo.task.Task.Default

object UShortMapField: Field<MutableMap<String, UShort>> {
    override val typeName:String = "UShortMap"
    class T: Task(){
        fun default(v:MutableMap<String, UShort>){
            _default = Default{_,_->HashMap<String, UShort>(v.size).also{it.putAll(v)}}
        }
        fun default(vararg v:Pair<String, UShort>){
            _default = Default{_,_->HashMap<String, UShort>(v.size).also{it.putAll(v)}}
        }
    }
}
inline val VO.ushortMap:Prop<MutableMap<String, UShort>> get() = delegate(UShortMapField)
inline fun VO.ushortMap(v:MutableMap<String, UShort>):Prop<MutableMap<String, UShort>>
        = delegate(UShortMapField){UShortMapField.T().also{it.default(v)}}
inline fun VO.ushortMap(vararg v:Pair<String, UShort>):Prop<MutableMap<String, UShort>>
        = delegate(UShortMapField){UShortMapField.T().also{it.default(*v)}}
inline fun VO.ushortMap(block: UShortMapField.T.()->Unit):Prop<MutableMap<String, UShort>> = delegate(UShortMapField, block){UShortMapField.T()}