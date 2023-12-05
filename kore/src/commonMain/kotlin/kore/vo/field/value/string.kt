@file:Suppress("NOTHING_TO_INLINE")

package kore.vo.field.value

import kore.vo.VO
import kore.vo.field.Field
import kore.vo.field.Prop
import kore.vo.task.Task

object StringField: Field<String> {
    class T: Task(){
        fun default(v:String){
            _default = v
        }
    }
}
inline val VO.string:Prop<String> get() = delegate(StringField)
inline fun VO.string(v:String):Prop<String> = delegate(StringField){ StringField.T().also{it.default(v)}}
inline fun VO.string(block: StringField.T.()->Unit):Prop<String> = delegate(StringField, block){ StringField.T()}