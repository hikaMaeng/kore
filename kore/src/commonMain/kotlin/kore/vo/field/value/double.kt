@file:Suppress("NOTHING_TO_INLINE")

package kore.vo.field.value

import kore.vo.VO
import kore.vo.field.Field
import kore.vo.field.Prop
import kore.vo.task.Task

object DoubleField: Field<Double> {
    override val typeName:String = "Double"
//    fun toJSONString():String = "Double"
//    fun fromJSONString():Double = "Double"
    class T: Task(){
        fun default(v:Double){
            _default = v
        }
    }
}
//    fun toJSONString():String = "Double"
//    fun fromJSONString():Double = "Double"
inline val VO.double:Prop<Double> get() = delegate(DoubleField)
inline fun VO.double(v:Double):Prop<Double> = delegate(DoubleField){ DoubleField.T().also{it.default(v)}}
inline fun VO.double(block: DoubleField.T.()->Unit):Prop<Double> = delegate(DoubleField, block){ DoubleField.T()}