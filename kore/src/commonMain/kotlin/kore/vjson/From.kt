//package kore.vjson
//
//import kore.vo.VO
//import kore.wrap.Wrap
//import kotlin.reflect.KClass
//
//object From {
//    fun <V:VO> vo(vo:V, value:String, cursor:VJson.Cursor):Wrap<V>{
//
//    }
//    private val decoders:HashMap<KClass<*>,(Field<*>, String, VJson.Cursor, Report)->Any?> = hashMapOf(
//        IntField::class to { f, s, c, r-> VJson.decodeValue(s, c, r, String::toIntOrNull, f)},
//        ShortField::class to { f, s, c, r-> VJson.decodeValue(s, c, r, String::toShortOrNull, f)},
//        LongField::class to { f, s, c, r-> VJson.decodeValue(s, c, r, String::toLongOrNull, f)},
//        UIntField::class to { f, s, c, r-> VJson.decodeValue(s, c, r, String::toUIntOrNull, f)},
//        UShortField::class to { f, s, c, r-> VJson.decodeValue(s, c, r, String::toUShortOrNull, f)},
//        ULongField::class to { f, s, c, r-> VJson.decodeValue(s, c, r, String::toULongOrNull, f)},
//        FloatField::class to { f, s, c, r-> VJson.decodeValue(s, c, r, String::toFloatOrNull, f)},
//        DoubleField::class to { f, s, c, r-> VJson.decodeValue(s, c, r, String::toDoubleOrNull, f)},
//        BooleanField::class to { f, s, c, r-> VJson.decodeValue(s, c, r, String::toBooleanStrictOrNull, f)},
//        StringField::class to { _, s, c, r-> VJson.decodeStringValue(s, c, r)},
//        UtcField::class to fun(_:Field<*>, serial:String, cursor:VJson.Cursor, report:Report):Any?{
//            return VJson.decodeStringValue(serial, cursor, report)?.let{
//                eUtc.of(it) ?: report(
//                    eEntity.ERROR.decode_error,
//                    "invalid type eUtc,cursor:${cursor.v - 1},serial:$serial"
//                )
//            } ?: report(
//                eEntity.ERROR.decode_error,
//                "invalid eUtc,cursor:${cursor.v - 1},serial:$serial"
//            )
//        },
//        EnumField::class to fun(field:Field<*>, serial:String, cursor:VJson.Cursor, report:Report):Any?{
//            val value = VJson.decodeStringValue(serial, cursor, report) ?: return null
//            return (field as EnumField<*>).enums.find{it.name == value } ?: report(eEntity.ERROR.decode_error,"invalid enum,cursor:${cursor.v-1},serial:$serial")
//        },
//        IntListField::class to { _, s, c, r-> VJson.decodeList(s, c, r, String::toIntOrNull)},
//        ShortListField::class to { _, s, c, r-> VJson.decodeList(s, c, r, String::toShortOrNull)},
//        LongListField::class to { _, s, c, r-> VJson.decodeList(s, c, r, String::toLongOrNull)},
//        UIntListField::class to { _, s, c, r-> VJson.decodeList(s, c, r, String::toUIntOrNull)},
//        UShortListField::class to { _, s, c, r-> VJson.decodeList(s, c, r, String::toUShortOrNull)},
//        ULongListField::class to { _, s, c, r-> VJson.decodeList(s, c, r, String::toULongOrNull)},
//        FloatListField::class to { _, s, c, r-> VJson.decodeList(s, c, r, String::toFloatOrNull)},
//        DoubleListField::class to { _, s, c, r-> VJson.decodeList(s, c, r, String::toDoubleOrNull)},
//        BooleanListField::class to { _, s, c, r-> VJson.decodeList(s, c, r, String::toBooleanStrictOrNull)},
//        StringListField::class to { _, s, c, r-> VJson.decodeListPart(s, c, r)},
//        EnumListField::class to fun(field:Field<*>, serial:String, cursor:VJson.Cursor, report:Report):Any?{
//            val enums = (field as EnumListField<*>).enums
//            val list = arrayListOf<Any>()
//            VJson.openList(serial, cursor)
//            if(VJson.skipSep(']', serial, cursor)) VJson.skipComma(serial, cursor)
//            else{
//                do{
//                    val value = VJson.decodeStringValue(serial, cursor, report)
//                    list += enums.find{ it.name == value } ?:return report(eEntity.ERROR.decode_error, "invalid enum,cursor:${cursor.v - 1},serial:$serial")
//                    if(VJson.skipSep(']', serial, cursor)) break
//                    VJson.skipComma(serial, cursor)
//                }while(true)
//            }
//            return list
//        },
//        IntMapField::class to { f, s, c, r-> VJson.decodeMap(s, c, r, String::toIntOrNull, f)},
//        ShortMapField::class to { f, s, c, r-> VJson.decodeMap(s, c, r, String::toShortOrNull, f)},
//        LongMapField::class to { f, s, c, r-> VJson.decodeMap(s, c, r, String::toLongOrNull, f)},
//        UIntMapField::class to { f, s, c, r-> VJson.decodeMap(s, c, r, String::toUIntOrNull, f)},
//        UShortMapField::class to { f, s, c, r-> VJson.decodeMap(s, c, r, String::toUShortOrNull, f)},
//        ULongMapField::class to { f, s, c, r-> VJson.decodeMap(s, c, r, String::toULongOrNull, f)},
//        FloatMapField::class to { f, s, c, r-> VJson.decodeMap(s, c, r, String::toFloatOrNull, f)},
//        DoubleMapField::class to { f, s, c, r-> VJson.decodeMap(s, c, r, String::toDoubleOrNull, f)},
//        BooleanMapField::class to { f, s, c, r-> VJson.decodeMap(s, c, r, String::toBooleanStrictOrNull, f)},
//        StringMapField::class to fun(_, serial:String, cursor:VJson.Cursor, report:Report):Any?{
//            var key:String
//            var value:String
//            val result = hashMapOf<String,String>()
//            VJson.openObject(serial, cursor)
//            if(VJson.skipSep('}', serial, cursor)) VJson.skipComma(serial, cursor)
//            else{
//                while(VJson.skipNotSep('}', serial, cursor)){
//                    key = VJson.decodeStringValue(serial, cursor, report) ?: return null
//                    cursor.v++
//                    value = VJson.decodeStringValue(serial, cursor, report) ?: return null
//                    result[key] = value
//                    if(VJson.skipSep('}', serial, cursor)) break
//                    VJson.skipComma(serial, cursor)
//                }
//            }
//            return result
//        },
//        EnumMapField::class to fun(field:Field<*>, serial:String, cursor:VJson.Cursor, report:Report):Any?{
//            var key:String
//            var value:String
//            val result = hashMapOf<String,Any>()
//            VJson.openObject(serial, cursor)
//            if(VJson.skipSep('}', serial, cursor)) VJson.skipComma(serial, cursor)
//            else{
//                while(VJson.skipNotSep('}', serial, cursor)){
//                    key = VJson.decodeStringValue(serial, cursor, report) ?: return null
//                    cursor.v++
//                    value = VJson.decodeStringValue(serial, cursor, report) ?: return null
//                    result[key] = (field as EnumMapField<*>).enums.find{it.name == value } ?: return report(eEntity.ERROR.decode_error,"invalid enum,cursor:${cursor.v-1},serial:$serial")
//                    if(VJson.skipSep('}', serial, cursor)) break
//                    VJson.skipComma(serial, cursor)
//                }
//            }
//            return result
//        },
//        EntityField::class to { field, serial, cursor, report->
//            VJson.decodeEntity(serial, cursor, (field as EntityField<*>).factory(), report)
//        },
//        SlowEntityField::class to { field, serial, cursor, report->
//            VJson.decodeEntity(serial, cursor, (field as EntityField<*>).factory(), report)
//        },
//        EntityListField::class to fun(field:Field<*>, serial:String, cursor:VJson.Cursor, report:Report):Any{
//            val result = arrayListOf<Any>()
//            VJson.openList(serial, cursor)
//            if(VJson.skipSep(']', serial, cursor)) VJson.skipComma(serial, cursor)
//            else{
//                do{
//                    if(VJson.decodeEntity(serial, cursor, (field as EntityListField<*>).factory(), report)?.let{ result += it } == null) return report
//                    if(VJson.skipSep(']', serial, cursor)) break
//                    VJson.skipComma(serial, cursor)
//                }while(true)
//            }
//            return result
//        },
//        SlowEntityListField::class to fun(field:Field<*>, serial:String, cursor:VJson.Cursor, report:Report):Any{
//            val result = arrayListOf<Any>()
//            VJson.openList(serial, cursor)
//            if(VJson.skipSep(']', serial, cursor)) VJson.skipComma(serial, cursor)
//            else{
//                do{
//                    if(VJson.decodeEntity(serial, cursor, (field as EntityListField<*>).factory(), report)?.let{ result += it } == null) return report
//                    if(VJson.skipSep(']', serial, cursor)) break
//                    VJson.skipComma(serial, cursor)
//                }while(true)
//            }
//            return result
//        },
//        SlowEntityMapField::class to { field, serial, cursor, report->
//            val factory = (field as EntityMapField<*>).factory
//            var key:String?
//            var value:eEntity?
//            val result:HashMap<String, eEntity> = hashMapOf()
//            VJson.openObject(serial, cursor)
//            if(VJson.skipSep('}', serial, cursor)) VJson.skipComma(serial, cursor)
//            else{
//                while(VJson.skipNotSep('}', serial, cursor)){
//                    key = VJson.decodeStringValue(serial, cursor, report)
//                    cursor.v++
//                    value = VJson.decodeEntity(serial, cursor, factory(), report)
//                    result[key!!] = value!!
//                    if(VJson.skipSep('}', serial, cursor)) break
//                    VJson.skipComma(serial, cursor)
//                }
//            }
//            result
//        },
//        EntityMapField::class to { field, serial, cursor, report->
//            val factory = (field as EntityMapField<*>).factory
//            var key:String?
//            var value:eEntity?
//            val result:HashMap<String, eEntity> = hashMapOf()
//            VJson.openObject(serial, cursor)
//            if(VJson.skipSep('}', serial, cursor)) VJson.skipComma(serial, cursor)
//            else{
//                while(VJson.skipNotSep('}', serial, cursor)){
//                    key = VJson.decodeStringValue(serial, cursor, report)
//                    cursor.v++
//                    value = VJson.decodeEntity(serial, cursor, factory(), report)
//                    result[key!!] = value!!
//                    if(VJson.skipSep('}', serial, cursor)) break
//                    VJson.skipComma(serial, cursor)
//                }
//            }
//            result
//        },
//        UnionField::class to { field, serial, cursor, report->
//            VJson.decodeUnionEntity(serial, cursor, (field as UnionField<*>).union, report)
//        },
//        UnionListField::class to fun(field:Field<*>, serial:String, cursor:VJson.Cursor, report:Report):Any?{
//            val result = arrayListOf<Any>()
//            VJson.openList(serial, cursor)
//            if(VJson.skipSep(']', serial, cursor)) VJson.skipComma(serial, cursor)
//            else{
//                do{
//                    result += VJson.decodeUnionEntity(serial, cursor, (field as UnionListField<*>).union, report) ?: return null
//                    if(VJson.skipSep(']', serial, cursor)) break
//                    VJson.skipComma(serial, cursor)
//                }while(true)
//            }
//            return result
//        },
//        UnionMapField::class to fun(field:Field<*>, serial:String, cursor:VJson.Cursor, report:Report):Any?{
//            var key:String
//            var value:eEntity
//            val result:HashMap<String, eEntity> = hashMapOf()
//            VJson.openObject(serial, cursor)
//            if(VJson.skipSep('}', serial, cursor)) VJson.skipComma(serial, cursor)
//            else{
//                while(VJson.skipNotSep('}', serial, cursor)){
//                    key = VJson.decodeStringValue(serial, cursor, report) ?:return null
//                    cursor.v++
//                    value = VJson.decodeUnionEntity(serial, cursor, (field as UnionMapField<*>).union, report) ?:return null
//                    result[key] = value
//                    if(VJson.skipSep('}', serial, cursor)) break
//                    VJson.skipComma(serial, cursor)
//                }
//            }
//            return result
//        }
//    )
//    fun setEncoder(type:KClass<*>, block:(name:String, v:Any, field:Field<*>, report:Report)->String?){
//        encoders[type] = block
//    }
//    fun setDecoder(type:KClass<*>, block:(field:Field<*>, serial:String, cursor:VJson.Cursor, report:Report)->Any?){
//        decoders[type] = block
//    }
//    /**
//     * decodeStringValue 를 외부에 제공해주기 위한 함수
//     */
//    fun getDecodeStringValue(serial:String, cursor:VJson.Cursor, report:Report):String? =
//        VJson.decodeStringValue(serial, cursor, report)
//    private val SEP = " \t\n\r,]}".toCharArray()
//    //private val SEP = ",]}".toCharArray()
//    private inline fun<T> decodeValue(serial:String, cursor:VJson.Cursor, report:Report, block:String.()->T?, field:Field<*>? = null):T?{
//        VJson.skipSpace(serial, cursor)
//        val pin = cursor.v
//        cursor.v = serial.indexOfAny(SEP,cursor.v++)
//        if(cursor.v == -1) return report(eEntity.ERROR.decode_error,"invalid json form.field:$field,pin:${pin},serial:$serial")
//        val chunk = serial.substring(pin,cursor.v)
//        return chunk.block() ?:return report(eEntity.ERROR.decode_error,"invalid type.field:$field,chunk:*$chunk*,pin:${pin},cursor:${cursor.v},serial:$serial")
//    }
//    private inline fun decodeStringValue(serial:String, cursor:VJson.Cursor, report:Report):String?{
//        if(VJson.skipNotSep('"', serial, cursor)) return report(eEntity.ERROR.decode_error, "invalid string form. cursor:${cursor.v},serial:$serial")
//        cursor.v++
//        val pin = cursor.v
//        do{
//            cursor.v = serial.indexOf('"', cursor.v++)
//            if(cursor.v == -1){
//                return report(eEntity.ERROR.decode_error,"invalid string form. pin:${pin},serial:$serial")
//            }else{
//                if(serial[cursor.v - 1] == '\\') cursor.v++
//                else break
//            }
//        }while(true)
//        return VJson.decodeString(serial.substring(pin, cursor.v++))
//    }
//    private inline fun <T> decodeList(serial:String, cursor:VJson.Cursor, report:Report, block:String.()->T?):Any?{
//        VJson.openList(serial, cursor)
//        val pin = cursor.v
//        if(VJson.skipSep(']', serial, cursor)){
//            VJson.skipComma(serial, cursor)
//            return listOf<T>()
//        }else{
//            do{
//                cursor.v = serial.indexOf(']', cursor.v++)
//                if(cursor.v == -1){
//                    return report(eEntity.ERROR.decode_error, "invalid list form. pin:${pin},cursor:${cursor.v},serial:$serial")
//                }else{
//                    if(serial[cursor.v - 1] == '\\') cursor.v++
//                    else break
//                }
//            }while(true)
//        }
//        return serial.substring(pin,cursor.v++).trim().split(',').mapIndexed{ index,it ->
//            it.trim().block() ?:return report(eEntity.ERROR.decode_error,"invalid type. $it,index:$index")
//        }
//    }
//    private inline fun decodeListPart(serial:String, cursor:VJson.Cursor, report:Report):List<String>?{
//        val list = arrayListOf<String>()
//        VJson.openList(serial, cursor)
//        if(VJson.skipSep(']', serial, cursor)) VJson.skipComma(serial, cursor)
//        else{
//            do{
//                list += decodeStringValue(serial,cursor,report) ?:return null
//                if(VJson.skipSep(']', serial, cursor)) break
//                VJson.skipComma(serial, cursor)
//            }while(true)
//        }
//        return list
//    }
//    private inline fun <T> decodeMap(serial:String, cursor:VJson.Cursor, report:Report, block:String.()->T?, field:Field<*>):Any?{
//        var key:String
//        var value:T
//        val result = hashMapOf<String,T>()
//        VJson.openObject(serial, cursor)
//        if(VJson.skipSep('}', serial, cursor)) VJson.skipComma(serial, cursor)
//        else{
//            while(VJson.skipNotSep('}', serial, cursor)){
//                key = decodeStringValue(serial,cursor,report) ?:return null
//                cursor.v++
//                value = decodeValue(serial,cursor,report,block,field) ?:return null
//                result[key] = value
//                if(VJson.skipSep('}', serial, cursor)) break
//                VJson.skipComma(serial, cursor)
//            }
//        }
//        return result
//    }
//    private inline fun <ENTITY:eEntity> decodeEntity(serial:String, cursor:VJson.Cursor, entity:ENTITY, report:Report):ENTITY?{
//        val type:KClass<out eEntity> = entity::class
//        //val fields:HashMap<String, Field<*>> = Field[type] ?: return entity//return report(eEntity.ERROR.decode_error,"no fields:${type.simpleName}")
//        val fields:HashMap<String, Field<*>> = entity.fields
//        val convert:ArrayList<Map.Entry<String, Field<*>>> = ArrayList<Map.Entry<String, Field<*>>>(fields.size).also{ list-> repeat(fields.size){ list.add(
//            VJson.entry
//        ) } }
//        fields.forEach{
//            convert[Indexer.get(type,it.key)] = it
//        }
//        VJson.openObject(serial, cursor)
//        while(VJson.skipNotSep('}', serial, cursor)){
//            val key = VJson.key(serial, cursor, report) ?: return entity
//            if(Indexer.getOrNull(type,key) == null){
//                try{
//                    VJson.passValue(key, serial, cursor, report)
//                }catch(e:Error){
//                    return report(e.id, e.message, *e.result)
//                }
//            }else{
//                val field = convert[Indexer.get(type,key)]
//                val v = decoders[field.value::class]?.invoke(field.value,serial,cursor,report) ?:return report(eEntity.ERROR.decode_error,"no value:${type.simpleName}:${key}")
//                try{
//                    entity.setRawValue(field.key, v)
//                }catch(e:Error){
//                    return report(e.id, e.message, *e.result)
//                }
//            }
//            //종료 처리
//            if(VJson.skipSep('}', serial, cursor)) break
//            VJson.skipComma(serial, cursor)
//        }
//        return entity
//    }
//    private inline fun <ENTITY:eEntity,T:Union<ENTITY>> decodeUnionEntity(serial:String, cursor:VJson.Cursor, union:T, report:Report):ENTITY?{
//        VJson.openObject(serial, cursor)
//
//        var isM42Json = false
//        var entity:ENTITY? = null
//        val pin = cursor.v
//        val unionKey = VJson.key(serial, cursor, report) ?:return null
//        var unionIndex:Int? = null
//        if(unionKey == unionIndexKey){
//            isM42Json = true
//            unionIndex = decoders[IntField::class]?.invoke(IntField,serial,cursor,report)?.let{ "$it".toIntOrNull() }
//            if(unionIndex == null) isM42Json = false
//            else VJson.skipComma(serial, cursor)
//        }
//
//        if(isM42Json){
//            var isError = false
//            entity = union.factories[unionIndex!!]()
//            val type:KClass<out eEntity> = entity::class
//            val value:MutableMap<String,Any?> = entity._values ?:let{
//                VJson.skipSep('}', serial, cursor)
//                return entity
//            }// ?:throw Error(eEntity.ERROR.decode_error,"no value:${type}:$entity")
//            //val fields:HashMap<String, Field<*>> = Field[type] ?: throw Error(eEntity.ERROR.decode_error,"no fields $entity")
//            val fields:HashMap<String, Field<*>> = entity.fields
//            val convert:ArrayList<Map.Entry<String, Field<*>>> = ArrayList<Map.Entry<String, Field<*>>>(fields.size).also{ list->repeat(fields.size){list.add(
//                VJson.entry
//            )}}
//            fields.forEach{
//                if(Indexer.getOrNull(type,it.key) == null){
//                    isError = true
//                    return@forEach
//                }
//                convert[Indexer.get(type,it.key)] = it
//            }
//            if(!isError){
//                //openObject(serial,cursor)
//                while(VJson.skipNotSep('}', serial, cursor)){
//                    val key = VJson.key(serial, cursor, report)?.split('|')?.last() ?: return null
//                    val field = convert[Indexer.get(type,key)]
//                    value[field.key] = decoders[field.value::class]?.invoke(field.value,serial,cursor,report)
//                    if(VJson.skipSep('}', serial, cursor)) break
//                    VJson.skipComma(serial, cursor)
//                }
//                return entity
//            }
//        }
//
//        union.factories.forEach{ factory ->
//            val factoryEntity = factory()
//            cursor.v = pin
//            var isError = false
//            val type:KClass<out eEntity> = factoryEntity::class
//            val value:MutableMap<String,Any?> = factoryEntity._values ?:return@forEach
//            //val fields:HashMap<String, Field<*>> = Field[type] ?:return@forEach
//            val fields:HashMap<String, Field<*>> = factoryEntity.fields
//            val convert:ArrayList<Map.Entry<String, Field<*>>> = ArrayList<Map.Entry<String, Field<*>>>(fields.size).also{ list->repeat(fields.size){ list.add(
//                VJson.entry
//            ) }}
//            fields.forEach{
//                if(Indexer.getOrNull(type,it.key) == null){
//                    isError = true
//                    @Suppress("LABEL_NAME_CLASH")
//                    return@forEach
//                }
//                convert[Indexer.get(type,it.key)] = it
//            }
//            if(isError) return@forEach
//
//            while(VJson.skipNotSep('}', serial, cursor)){
//                val key = VJson.key(serial, cursor, report) ?: return@forEach
//                val idx = Indexer.getOrNull(type,key)
//                if(idx == null){
//                    isError = true
//                    break
//                }
//                val field = convert[idx]
//                value[field.key] = decoders[field.value::class]?.invoke(field.value,serial,cursor,report)
//                if(VJson.skipSep('}', serial, cursor)) break
//                VJson.skipComma(serial, cursor)
//            }
//            if(isError) return@forEach
//            return factoryEntity
//        }
//
//        return entity?:throw Error(eEntity.ERROR.decode_error,"Union Decode Error")
//    }
//}