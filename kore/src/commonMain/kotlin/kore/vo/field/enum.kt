@file:Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")

package kore.vo.field

import kore.vo.VO
import kore.vo.task.Task
import kore.vo.task.Task.Default
import kotlin.reflect.KClass

class EnumField<ENUM: Enum<ENUM>>(val enums:Array<ENUM>): Field<ENUM>{
    override fun defaultFactory():ENUM = enums[0]
    class T<ENUM: Enum<ENUM>>:Task(){
        fun default(v:ENUM){
            _default = v
        }
    }
    companion object{
        @PublishedApi internal val fields:HashMap<KClass<out Enum<*>>, EnumField<out Enum<*>>> = hashMapOf()
        inline operator fun <reified ENUM: Enum<ENUM>> invoke(): EnumField<ENUM>{
            return fields.getOrPut(ENUM::class){ EnumField(enumValues<ENUM>()) } as EnumField<ENUM>
        }
    }
}
inline fun <reified ENUM:Enum<ENUM>> VO.enum(): Prop<ENUM> = delegate(EnumField())
inline fun <reified ENUM:Enum<ENUM>> VO.enum(v:ENUM): Prop<ENUM> = delegate(EnumField()){ EnumField.T<ENUM>().also{it.default(v)}}
inline fun <reified ENUM:Enum<ENUM>> VO.enum(block: EnumField.T<ENUM>.()->Unit): Prop<ENUM> = delegate(EnumField(), block){ EnumField.T() }
class EnumListField<ENUM: Enum<ENUM>>(val enums:Array<ENUM>):Field<MutableList<ENUM>>{
    override fun defaultFactory():MutableList<ENUM> = arrayListOf()
    class T<ENUM: Enum<ENUM>>: Task(){
        fun default(v:MutableList<ENUM>){
            _default = Default{ _, _->ArrayList<ENUM>(v.size).also{it.addAll(v)}}
        }
        fun default(vararg v:ENUM){
            _default = Default{ _, _->ArrayList<ENUM>(v.size).also{ a->v.forEach{a.add(it)}}}
        }
    }
    companion object{
        @PublishedApi internal val fields:HashMap<KClass<out Enum<*>>, EnumListField<out Enum<*>>> = hashMapOf()
        inline operator fun <reified ENUM: Enum<ENUM>> invoke(): EnumListField<ENUM>{
            return fields.getOrPut(ENUM::class){ EnumListField(enumValues<ENUM>()) } as EnumListField<ENUM>
        }
    }
}
inline fun <reified ENUM:Enum<ENUM>> VO.enumList(): Prop<MutableList<ENUM>> = delegate(EnumListField())
inline fun <reified ENUM:Enum<ENUM>> VO.enumList(v:MutableList<ENUM>): Prop<MutableList<ENUM>>
= delegate(EnumListField()){ EnumListField.T<ENUM>().also{it.default(v)}}
inline fun <reified ENUM:Enum<ENUM>> VO.enumList(vararg v:ENUM): Prop<MutableList<ENUM>>
= delegate(EnumListField()){ EnumListField.T<ENUM>().also{it.default(*v)}}
inline fun <reified ENUM:Enum<ENUM>> VO.enumList(block: EnumListField.T<ENUM>.()->Unit): Prop<MutableList<ENUM>>
= delegate(EnumListField(), block){ EnumListField.T() }
class EnumMapField<ENUM: Enum<ENUM>>(val enums:Array<ENUM>): Field<MutableMap<String, ENUM>>{
    override fun defaultFactory():MutableMap<String, ENUM> = hashMapOf()
    class T<ENUM: Enum<ENUM>>: Task(){
        fun default(v:MutableMap<String, ENUM>){
            _default = Default{_,_->HashMap<String, ENUM>(v.size).also{it.putAll(v)}}
        }
        fun default(vararg v:Pair<String, ENUM>){
            _default = Default{_,_->HashMap<String, ENUM>(v.size).also{it.putAll(v)}}
        }
    }
    companion object{
        @PublishedApi internal val fields:HashMap<KClass<out Enum<*>>, EnumMapField<out Enum<*>>> = hashMapOf()
        inline operator fun <reified ENUM: Enum<ENUM>> invoke(): EnumMapField<ENUM>{
            return fields.getOrPut(ENUM::class){ EnumMapField(enumValues<ENUM>()) } as EnumMapField<ENUM>
        }
    }
}
inline fun <reified ENUM:Enum<ENUM>> VO.enumMap(): Prop<MutableMap<String, ENUM>> = delegate(EnumMapField())
inline fun <reified ENUM:Enum<ENUM>> VO.enumMap(v:MutableMap<String, ENUM>): Prop<MutableMap<String, ENUM>>
= delegate(EnumMapField()){ EnumMapField.T<ENUM>().also{it.default(v)}}
inline fun <reified ENUM:Enum<ENUM>> VO.enumMap(vararg v:Pair<String, ENUM>): Prop<MutableMap<String, ENUM>>
= delegate(EnumMapField()){ EnumMapField.T<ENUM>().also{it.default(*v)}}
inline fun <reified ENUM:Enum<ENUM>> VO.enumMap(block: EnumMapField.T<ENUM>.()->Unit): Prop<MutableMap<String, ENUM>>
= delegate(EnumMapField(), block){ EnumMapField.T() }

///**
// * Enum이 일련번호와 밀접한 관계가 있다면 이것을 구현한다.
// * eQuery에서 param 엔티티에 이것을 구현한 Enum Field가 있다면 rowid로 대체해 주며
// * 반대로 반환값도 rowid를 Enum으로 매칭해준다.
// */
//interface EnumRowid<T>{
//    val rowid:T
//    fun toDbString() = "$rowid"
//}