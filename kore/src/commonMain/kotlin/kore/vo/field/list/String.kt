@file:Suppress("NOTHING_TO_INLINE")

package kore.vo.field.list

import kore.vo.VO
import kore.vo.field.Field
import kore.vo.field.Prop
import kore.vo.field.list.StringListField.T
import kore.vo.task.Task

object StringListField: Field<MutableList<String>> {
    override val typeName:String = "StringList"
    class T: Task(){
        fun default(v:MutableList<String>){
            _default = Task.Default{_,_->ArrayList<String>(v.size).also{it.addAll(v)}}
        }
        fun default(vararg v:String){
            _default = Task.Default{_,_->ArrayList<String>(v.size).also{a->v.forEach{a.add(it)}}}
        }
    }
}
inline val VO.stringList:Prop<MutableList<String>> get() = delegate(StringListField)
inline fun VO.stringList(v:MutableList<String>):Prop<MutableList<String>>
= delegate(StringListField){ T().also{it.default(v)}}
inline fun VO.stringList(vararg v:String):Prop<MutableList<String>>
= delegate(StringListField){ T().also{it.default(*v)}}
inline fun VO.stringList(block: T.()->Unit):Prop<MutableList<String>> = delegate(StringListField, block){ T() }