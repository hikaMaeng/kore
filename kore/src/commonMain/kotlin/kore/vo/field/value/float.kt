@file:Suppress("NOTHING_TO_INLINE")

package kore.vo.field.value

import kore.vo.VO
import kore.vo.field.Field
import kore.vo.field.Prop
import kore.vo.task.Task

object FloatField: Field<Float> {
    class T: Task(){
        fun default(v:Float){
            _default = v
        }
    }
}
inline val VO.float:Prop<Float> get() = delegate(FloatField)
inline fun VO.float(v:Float):Prop<Float> = delegate(FloatField){ FloatField.T().also{it.default(v)}}
inline fun VO.float(block: FloatField.T.()->Unit):Prop<Float> = delegate(FloatField, block){ FloatField.T()}