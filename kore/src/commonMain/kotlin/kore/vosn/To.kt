@file:Suppress("NOTHING_TO_INLINE", "UNCHECKED_CAST")

package kore.vosn

import kore.vo.VO
import kore.vo.VOSum
import kore.vo.converter.ToEnum
import kore.vo.converter.ToInvalidSum
import kore.vo.converter.ToNoEnum
import kore.vo.converter.ToVONoInitialized
import kore.vo.field.*
import kore.vo.field.list.*
import kore.vo.field.map.*
import kore.vo.field.value.StringField
import kore.vo.task.Task
import kore.vo.task.Task.Companion._optinal
import kore.vosn.VOSN.OPTIONAL_NULL
import kore.vosn.VOSN.STRINGLIST_EMPTY
import kore.vosn.VOSN.encodeString
import kore.wrap.W
import kore.wrap.Wrap
import kore.wrap.wrap
import kotlin.reflect.KClass

internal object To{
    private inline fun to(type:KClass<*>, v:Any, field:Field<*>):Wrap<String> = encoders[type]?.invoke(v, field) ?: W("$v")
    fun vo(d:Any):Wrap<String>{
        val vo:VO = d as VO
        val fields:HashMap<String, Field<*>> = vo.getFields() ?: return ToVONoInitialized(vo, "getFields").wrap()
        val tasks:HashMap<String, Task>? = vo.getTasks()
        val keys:List<String> = VO.keys(vo) ?: return ToVONoInitialized(vo, "getKeys").wrap()
        val result:StringBuilder = StringBuilder()
        var i = 0
        while(i < keys.size){
            val key:String = keys[i++]
            val include:((String, Any?) -> Boolean)? = tasks?.get(key)?.include
            result.append(
                vo[key]?.let{v->
                    if(include?.invoke(key, v) != false) {
                        val field:Field<*> = fields[key] ?: return ToVONoInitialized(vo, "getField[$key]").wrap()
                        to(field::class, v, field).getOrFailEffect { return it.wrap() }
                    }else ""
                } ?: if(include == _optinal) OPTIONAL_NULL else ""
            ).append("|")
        }
        return W(result.toString())
    }
    private inline fun sum(it:Any, sum:VOSum<*>):Wrap<String>{
        val type:KClass<*> = it::class
        val index:Int = sum.type.indexOf(type)
        return if(index == -1) W(ToInvalidSum(sum, it))
            else vo(it).map{index.toString() + (if(it.isNotBlank()) "|$it" else "")}
    }
    private inline fun result(result:String) = (if(result.isNotBlank()) result.substring(1) else "") + "@"
    private val valueList:(Any, Field<*>)->Wrap<String> = { v, _-> W((v as List<*>).joinToString("|")+"@")}
    private val valueMap:(Any, Field<*>)->Wrap<String> = {v, _ ->
        var result = ""
        (v as Map<String,*>).forEach{(k, it)-> result += "|" + encodeString(k) + "|" + it.toString() }
        W(result(result))
    }
    internal val encoders:HashMap<KClass<*>,(Any, Field<*>)-> Wrap<String>> = hashMapOf(
        IntListField::class to valueList,
        ShortListField::class to valueList,
        LongListField::class to valueList,
        UIntListField::class to valueList,
        UShortListField::class to valueList,
        ULongListField::class to valueList,
        FloatListField::class to valueList,
        DoubleListField::class to valueList,
        BooleanListField::class to valueList,
        IntMapField::class to valueMap,
        ShortMapField::class to valueMap,
        LongMapField::class to valueMap,
        UIntMapField::class to valueMap,
        UShortMapField::class to valueMap,
        ULongMapField::class to valueMap,
        FloatMapField::class to valueMap,
        DoubleMapField::class to valueMap,
        BooleanMapField::class to valueMap,
        //UtcField::class to { v, _-> (v as? eUtc)?.let{ encodeString(it.toString()) } },
        StringField::class to { v, _-> W(encodeString(v)) },
        StringListField::class to { v, _->(v as List<*>).let{
            if (it.isEmpty()) W(STRINGLIST_EMPTY) else W(it.joinToString("|") {encodeString(it)} + "@")
        }},
        StringMapField::class to { v, _->
            var result = ""
            (v as Map<String,*>).forEach{(k,it)->result += "|" + encodeString(k) + "|" + encodeString(it)}
            W(result(result))
        },
        EnumField::class to { v, field->
            val enums:Array<*> = (field as EnumField<*>).enums
            val index:Int = enums.indexOf(v)
            if(index != -1) W("$index") else W(ToNoEnum(enums, v))
        },
        EnumListField::class to { v, field ->
            val enums: Array<*> = (field as EnumListField<*>).enums
            var result: String = ""
            var error:Any? = null
            if ((v as List<*>).all { e ->
                val index:Int = enums.indexOf(e)
                if(index == -1) {
                    error = e
                    false
                }else{
                    result += "|$index"
                    true
                }
            }) W(result(result)) else W(ToNoEnum(enums, error!!))
        },
        EnumMapField::class to { v, field->
            val enums:Array<*> = (field as EnumMapField<*>).enums
            var result:String = ""
            var error:Any? = null
            if ((v as Map<String,*>).all { (k,e)->
                val index: Int = enums.indexOf(e)
                if (index == -1) {
                    error = e
                    false
                } else {
                    result += "|" + encodeString(k) + "|" + index.toString()
                    true
                }
            }) W(result(result)) else W(ToEnum(enums, error!!))
        },
        VOField::class to { v, _-> vo(v) },
        VOListField::class to { v, _->
            var result = ""
            var error:Throwable? = null
            if((v as List<*>).all { e ->
                vo(e!!).isEffected{ result += "|$it" }?.let{error = it} == null
            }) W(result(result)) else W(error!!)
        },
        VOMapField::class to { v, _->
            var result = ""
            var error:Throwable? = null
            if((v as Map<String, *>).all { (k, it) ->
                vo(it!!).isEffected{ result += "|${encodeString(k)}|$it" }?.let{error = it} == null
            }) W(result(result)) else W(error!!)
        },
        VOSumField::class to { v, field-> sum(v, (field as VOSumField<*>).sum) },
        VOSumListField::class to { v, field->
            val un: VOSum<VO> = (field as VOSumListField<*>).sum
            var result = ""
            var error:Throwable? = null
            if((v as List<*>).all{ e ->
                sum(e!!,un).isEffected{ result += "|$it" }?.let{error = it} == null
            }) W(result(result)) else W(error!!)
        },
        VOSumMapField::class to { v, field->
            var result = ""
            val un: VOSum<VO> = (field as VOSumMapField<*>).sum
            var error:Throwable? = null
            if((v as Map<String, *>).all{ (k, it) ->
                sum(it!!, un).isEffected{ result += "|${encodeString(k)}|$it" }?.let{error = it} == null
            }) W(result(result)) else W(error!!)
        }
    )
}