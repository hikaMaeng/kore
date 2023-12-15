@file:Suppress("NOTHING_TO_INLINE")

package kore.vjson

import kore.vo.VO
import kore.vo.converter.ToVONoInitialized
import kore.vo.field.Field
import kore.vo.task.Task
import kore.wrap.W
import kore.wrap.Wrap
import kotlin.reflect.KClass

class DataCotainer{
    var name = ""
    var items = mutableListOf<String>()
}
internal object To{
    private inline fun encode(type:KClass<*>, v:Any, field: Field<*>):Wrap<String> = encoders[type]?.invoke(v, field) ?: W("$v")
    internal fun <V:VO> vo(vo:V):Wrap<String>{
        val fields:HashMap<String, Field<*>> = vo.getFields() ?: return Wrap(ToVONoInitialized(vo))
        val tasks: HashMap<String, Task> = vo.getTasks() ?: return Wrap(ToVONoInitialized(vo))
        val keys:List<String> = VO.keys(vo) ?: return Wrap(ToVONoInitialized(vo))
        var result:String = ""
        var i:Int = 0
        val j:Int = keys.size
        while(i < j){
            val key = keys[i++]
            val include: ((String, Any?) -> Boolean)? = tasks[key]?.include
            vo[key]?.let{v->
                if(include?.invoke(key, v) == true) fields[key]?.let{field->
                    result += """"$key":${encode(field::class, v, field).getOrFailEffect{ return Wrap(it) }}${if(i < j) "," else ""}"""
                }
            }

        }
        return Wrap(result)
    }
    internal val encoders:HashMap<KClass<*>,(Any, Field<*>)-> Wrap<String>> = hashMapOf(
    )
//    private inline fun encodeObject(ent:Any, report:Report):List<String>?{
//        val entity:eEntity = ent as eEntity
//        val values:MutableMap<String,Any?> = entity._values ?:return arrayListOf()
//
//        val type:KClass<out eEntity> = entity::class
//        //val fields:HashMap<String, Field<*>> = Field[type] ?:return report(eEntity.ERROR.encode_error,"no fields $entity")
//        val fields:HashMap<String, Field<*>> = entity.fields
//        if (fields.size != values.size) return report(eEntity.ERROR.encode_error,"exist not initialized fields $entity")
//        val result:ArrayList<String> = ArrayList<String>(fields.size).also{ list-> repeat(fields.size){ list.add("") } }
//
//        values.forEach{(k,v)->
//            val field:Field<*> = fields[k] ?:return report(eEntity.ERROR.encode_error,"no field ${type.simpleName}.${k}")
//            //if(k == "data") log("values::${k}::${field::class}:$v")
//
//            //1단계 키에 해당되는 store의 tasks를 가져와서
//            val include = Store.getInclude(entity,k)
//            val value = when{
//                //include === Field.isOptional 일때 -> _values 에 값이 있으면 포함 없으면 포함 안함
//                include === Field.isOptional-> v
//                //include?.invoke() == false -> 포함 안함
//                include?.invoke() == false-> null
//                //그 외 -> _values 가 있으면 이걸로, 없으면 default 로, 둘 다 없으면 에러
//                //else->v ?:Store.getDefault<Any>(entity,k)?.value ?:return report(eEntity.ERROR.encode_error,"not initialized encoding Entity ${type.simpleName}.${k}")
//                else-> {
//                    v ?: run {
//                        entity._defaultMap?.get(
//                            Indexer.get(entity::class, k)
//                        )?.value ?:return report(eEntity.ERROR.encode_error,"not initialized encoding Entity ${type.simpleName}.${k}")
//                    }
//                }
//            }
//            if(value != null) Indexer.getOrNull(type,k)?.also{
//                //if(k == "data") log("Indexer::${it}::${field::class.simpleName}:$v")
//                result[it] = encode(field::class,k,value,field,report)
//            }
//        }
//        return result.filter{ it.isNotBlank() }
//    }

//    private inline fun wrapString(str:String) = "\"" + str + "\""
//    fun encodeWrapString(str:String):String = wrapString(str)
//    private val encodeValue:(String, Any, Field<*>, Report)->String={ name, v, _, _-> wrapString(name) + ":" + v.toString() }
//    private val encodeStringValue:(String, Any, Field<*>, Report)->String={ name, v, _, _->
//        wrapString(name) + ":" + wrapString(VJson.encodeString(v))
//    }
//    private val encodeValueList:(String, Any, Field<*>, Report)->String={ name, v, _, _-> wrapString(name) + ":[" + (v as List<*>).joinToString(","){"$it"} + "]" }
//    private val encodeStringList:(String, Any, Field<*>, Report)->String={ name, v, _, _-> wrapString(name) +":[" + (v as List<*>).joinToString(","){ wrapString(
//        VJson.encodeString(it)
//    ) } + "]"}
//    private val encodeValueMap:(String, Any, Field<*>, Report)->String={ name, v, _, _->
//        var result = ""
//        (v as Map<String,*>).forEach{(k,it)->result += ""","${VJson.encodeString(k)}":${it}"""}
//        """"$name":{${if(result.isNotBlank()) result.substring(1) else ""}}"""
//    }
//    private val encodeStringMap:(String, Any, Field<*>, Report)->String={ name, v, _, _->
//        var result = ""
//        (v as Map<String,*>).forEach{(k,it)->result += ""","${VJson.encodeString(k)}":"${VJson.encodeString(it)}""""}
//        """"$name":{${if(result.isNotBlank()) result.substring(1) else ""}}"""
//    }
//    private val encoders:HashMap<KClass<*>,(String, Any, Field<*>, Report)->String?> = hashMapOf(
//        IntField::class to encodeValue,
//        ShortField::class to encodeValue,
//        LongField::class to encodeValue,
//        UIntField::class to encodeValue,
//        UShortField::class to encodeValue,
//        ULongField::class to encodeValue,
//        FloatField::class to encodeValue,
//        DoubleField::class to encodeValue,
//        BooleanField::class to encodeValue,
//        EnumField::class to encodeStringValue,
//        StringField::class to encodeStringValue,
//        UtcField::class to { name, v, _, r->
//            (v as? eUtc)?.let{
//                encodeWrapString(name) + ":" + encodeWrapString(it.toString())
//            }
//        },
//        IntListField::class to encodeValueList,
//        ShortListField::class to encodeValueList,
//        LongListField::class to encodeValueList,
//        UIntListField::class to encodeValueList,
//        UShortListField::class to encodeValueList,
//        ULongListField::class to encodeValueList,
//        FloatListField::class to encodeValueList,
//        DoubleListField::class to encodeValueList,
//        BooleanListField::class to encodeValueList,
//        EnumListField::class to encodeStringList,
//        StringListField::class to encodeStringList,
//        IntMapField::class to encodeValueMap,
//        ShortMapField::class to encodeValueMap,
//        LongMapField::class to encodeValueMap,
//        UIntMapField::class to encodeValueMap,
//        UShortMapField::class to encodeValueMap,
//        ULongMapField::class to encodeValueMap,
//        FloatMapField::class to encodeValueMap,
//        DoubleMapField::class to encodeValueMap,
//        BooleanMapField::class to encodeValueMap,
//        EnumMapField::class to encodeStringMap,
//        StringMapField::class to encodeStringMap,
//        SlowEntityField::class to { name, v, _, r->
//            val result = VJson.encodeEntity(v, r)
//            if(result != null) "\"" + name + "\":" + result else null },
//        EntityField::class to { name, v, _, r->
//            val result = VJson.encodeEntity(v, r)
//            if(result != null) "\"" + name + "\":" + result else null },
//        EntityListField::class to { name, v, _, r->
//            var result = ""
//            var isFirst = true
//            if ((v as List<*>).all{ e ->
//                    VJson.encodeEntity(e!!, r)?.let{
//                        result += if (isFirst){
//                            isFirst = false
//                            it
//                        } else ",$it"
//                    } != null
//                }) "\"" + name + "\":[" + result + "]" else null
//        },
//        SlowEntityListField::class to { name, v, _, r->
//            var result = ""
//            var isFirst = true
//            if ((v as List<*>).all{ e ->
//                    VJson.encodeEntity(e!!, r)?.let{
//                        result += if (isFirst){
//                            isFirst = false
//                            it
//                        } else ",$it"
//                    } != null
//                }) "\"" + name + "\":[" + result + "]" else null
//        },
//        EntityMapField::class to { name, v, _, r->
//            var result = ""
//            var isFirst = true
//            if ((v as Map<String, *>).all{ (k, v) ->
//                    VJson.encodeEntity(v!!, r)?.let{value ->
//                        result += if(isFirst){
//                            isFirst = false
//                            "\"" + VJson.encodeString(k) + "\":" + value
//                        } else ",\"" + VJson.encodeString(k) + "\":" + value
//                    } != null
//                }) "\"" + name + "\":{" + result + "}" else null
//        },
//        SlowEntityMapField::class to { name, v, _, r->
//            var result = ""
//            var isFirst = true
//            if ((v as Map<String, *>).all{ (k, v) ->
//                    VJson.encodeEntity(v!!, r)?.let{value ->
//                        result += if(isFirst){
//                            isFirst = false
//                            "\"" + VJson.encodeString(k) + "\":" + value
//                        } else ",\"" + VJson.encodeString(k) + "\":" + value
//                    } != null
//                }) "\"" + name + "\":{" + result + "}" else null
//        },
//        UnionField::class to { name, v, f, r->
//            val result = VJson.encodeUnionEntityM42(v, (f as UnionField<*>).union, r)
//            if(result != null) "\"" + name + "\":" + result else null},
//        UnionListField::class to { name, v, f, r->
//            val un:Union<eEntity> = (f as UnionListField<*>).union
//            var result = ""
//            var isFirst = true
//            if((v as List<*>).all{e ->
//                    VJson.encodeUnionEntityM42(e!!, un, r)?.let{
//                        result += if (isFirst){
//                            isFirst = false
//                            it
//                        } else ",$it"
//                    } != null
//                }) "\"" + name + "\":[" + result + "]" else null
//        },
//        UnionMapField::class to { name, v, f, r->
//            var result = ""
//            var isFirst = true
//            val un:Union<eEntity> = (f as UnionMapField<*>).union
//            if((v as Map<String,*>).all{ (k, v) ->
//                    VJson.encodeUnionEntityM42(v!!, un, r)?.let{value ->
//                        result += if(isFirst){
//                            isFirst = false
//                            "\"" + VJson.encodeString(k) + "\":" + value
//                        } else ",\"" + VJson.encodeString(k) + "\":" + value
//                    } != null
//                }) "\"" + name + "\":{" + result + "}" else null
//        }
//    )
//    /**
//     * encoders 등록 안되어 있을때 기본 정책은 "k":"v" 로 인코딩 함
//     */
//    private fun encode(type:KClass<*>, k:String, v:Any, field:Field<*>, report:Report):String{
//        return encoders[type]?.invoke(k,v,field,report) ?: encodeValue(k,v,field,report)
//    }
////    private inline fun decode(field:Field<*>, serial:String, cursor:Cursor, report:Report):Any?{
////        return decoders[field::class]?.invoke(field,serial,cursor,report)
////    }
//    /**
//     * 단일 객체 내부의 프로퍼티들을 인코딩하고 리스트로 반환하는 함수
//     * encodeEntity와 encodeUnionEntityM42에서 공통으로 사용하는 함수
//     */
//    private inline fun encodeObject(ent:Any, report:Report):List<String>?{
//        val entity:eEntity = ent as eEntity
//        val values:MutableMap<String,Any?> = entity._values ?:return arrayListOf()
//
//        val type:KClass<out eEntity> = entity::class
//        //val fields:HashMap<String, Field<*>> = Field[type] ?:return report(eEntity.ERROR.encode_error,"no fields $entity")
//        val fields:HashMap<String, Field<*>> = entity.fields
//        if (fields.size != values.size) return report(eEntity.ERROR.encode_error,"exist not initialized fields $entity")
//        val result:ArrayList<String> = ArrayList<String>(fields.size).also{ list-> repeat(fields.size){ list.add("") } }
//
//        values.forEach{(k,v)->
//            val field:Field<*> = fields[k] ?:return report(eEntity.ERROR.encode_error,"no field ${type.simpleName}.${k}")
//            //if(k == "data") log("values::${k}::${field::class}:$v")
//
//            //1단계 키에 해당되는 store의 tasks를 가져와서
//            val include = Store.getInclude(entity,k)
//            val value = when{
//                //include === Field.isOptional 일때 -> _values 에 값이 있으면 포함 없으면 포함 안함
//                include === Field.isOptional-> v
//                //include?.invoke() == false -> 포함 안함
//                include?.invoke() == false-> null
//                //그 외 -> _values 가 있으면 이걸로, 없으면 default 로, 둘 다 없으면 에러
//                //else->v ?:Store.getDefault<Any>(entity,k)?.value ?:return report(eEntity.ERROR.encode_error,"not initialized encoding Entity ${type.simpleName}.${k}")
//                else-> {
//                    v ?: run {
//                        entity._defaultMap?.get(
//                            Indexer.get(entity::class, k)
//                        )?.value ?:return report(eEntity.ERROR.encode_error,"not initialized encoding Entity ${type.simpleName}.${k}")
//                    }
//                }
//            }
//            if(value != null) Indexer.getOrNull(type,k)?.also{
//                //if(k == "data") log("Indexer::${it}::${field::class.simpleName}:$v")
//                result[it] = encode(field::class,k,value,field,report)
//            }
//        }
//        return result.filter{ it.isNotBlank() }
//    }
//    private inline fun encodeEntity(entity:Any, report:Report):String? = encodeObject(entity,report)?.joinToString(",")?.let{ """{${it}}""" }
//
//    private const val unionIndexKey:String = "@@@"
//    private inline fun encodeUnionEntityM42(entity:Any, union:Union<*>, report:Report):String?{
//        val unionType:KClass<out Any> = entity::class
//        val index:Int = union.type.indexOf(unionType)
//        if(index == -1) return report(eEntity.ERROR.encode_error,"invalid union subtype. unionType:${union.type},entity:${unionType.simpleName}")
//        val result = encodeObject(entity,report)
//        return if(result != null){
//            """{"$unionIndexKey":${index}${if(result.isNotEmpty()) "," else ""}${result.joinToString(","){"${it.substring(0,1)}${it.substring(1)}"}}}"""
//        }else null
//        //return if(result != null) "{${result.joinToString(","){"${it.substring(0,1)}${it.substring(1)}"}}}" else null
//    }
}