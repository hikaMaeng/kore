@file:Suppress("NOTHING_TO_INLINE")

package kore.vosn

import kore.error.E
import kore.vo.VO
import kore.vo.converter.ToVONoInitialized
import kore.vosn.VOSN.Cursor
import kore.vosn.VOSN.Cursor.DecodeNoListTeminator
import kore.vosn.VOSN.STRINGLIST_EMPTY_C
import kore.vosn.VOSN.decodeString
import kore.vo.field.*
import kore.vo.field.list.*
import kore.vo.field.map.*
import kore.vo.field.value.*
import kore.wrap.W
import kore.wrap.Wrap
import kotlin.reflect.KClass

internal object From{
    /**
     * 디코더에서 사용하는 빈 객체
     * get()으로 값을 가져올 수 없음
     */
    private val vo:Map.Entry<String, Field<*>> = object:Map.Entry<String, Field<*>>{
        override val key:String get() = throw Throwable("DecodeEntry")
        override val value: Field<*> get() = throw Throwable("DecodeEntry")
    }
    class FromNoDecoder(val field:Field<*>, val cursor:Int, val encoded:String): E(field, cursor, encoded)
    class FromInvalidValue(val field:Field<*>, val target:String, val cursor:Int): E(field, target, cursor)
    class FromInvalidListValue(val field:Field<*>, val target:String, val index:Int): E(field, target, index)
    class FromInvalidMapValue(val field:Field<*>, val target:String, val key:String): E(field, target, key)
    private inline fun from(cursor:Cursor, field: Field<*>):Wrap<Any>{
        return decoders[field::class]?.invoke(cursor, field) ?: W(FromNoDecoder(field, cursor.v, cursor.encoded))
    }
    internal fun <V:VO> vo(cursor:Cursor, vo:V):Wrap<V>{
        val fields:HashMap<String, Field<*>> = vo.getFields() ?: return W(ToVONoInitialized(vo))
        (VO.keys(vo) ?: return W(ToVONoInitialized(vo))).forEach{ key->
            val field = fields[key] ?: return W(ToVONoInitialized(vo))
            when{
                cursor.isEnd->{}
                cursor.curr == VOSN.OPTIONAL_NULL_C -> cursor.v++
                else-> from(cursor, field).flatMap{
                    try{
                        vo[key] = it
                        W(it)
                    }catch(e:Throwable){
                        W(e)
                    }
                }
            }
            cursor.v++
        }
        return W(vo)
    }
    private inline fun<VALUE:Any> value(cursor:Cursor, field: Field<*>, block:String.()->VALUE?):Wrap<VALUE>{
        val target: String = cursor.nextValue
        return block(target)?.let{ W(it) } ?: W(FromInvalidValue(field, target, cursor.v))
    }
    private inline fun <VALUE:Any> valueList(cursor: Cursor, field: Field<*>, crossinline block:String.()->Wrap<VALUE>):Wrap<List<VALUE>>{
        return cursor.nextValueList.flatMap{list->
            list.flatMapList(block)
        }
    }
    private inline fun <VALUE:Any> valueMap(cursor:Cursor, field: Field<*>, crossinline block:(String, String)->Wrap<VALUE>):Wrap<HashMap<String, VALUE>>{
        return stringList(cursor).flatMap{
            it.flatMapListToMap(block)
        }
    }
    private inline fun stringValue(cursor: Cursor):Wrap<String>{
        val encoded = cursor.encoded
        val pin = cursor.v
        var at = pin
        do {
            at = encoded.indexOf('|', at)
            if (at == -1) { /** encoded 맨 끝까지 문자열인 경우 */
                cursor.v = encoded.length
                return W(decodeString(encoded.substring(pin, cursor.v)))
            } else if(at == pin) return W("") /** 길이 0인 문자열  || 인 상황 */
            else if (encoded[at-1] == '\\') at++ /** \| 스킵하기 */
            else {
                cursor.v = at
                return W(decodeString(encoded.substring(pin, at)))
            }
        } while(true)
    }
    private val regStringSplit = """(?<!\\)\|""".toRegex()
    private inline fun stringList(cursor: Cursor):Wrap<List<String>>{
        val list:ArrayList<String> = arrayListOf()
        if(cursor.curr == STRINGLIST_EMPTY_C) { /** !로 마크된 빈 리스트 */
            cursor.v++
            return W(list)
        }
        val encoded = cursor.encoded
        val pin = cursor.v
        var at = pin
        do{
            at = encoded.indexOf('@', at)
            if(at == -1) return W(DecodeNoListTeminator(encoded.substring(pin)))
            else if(encoded[at - 1] == '\\') at++
            else break
        }while(true)
        cursor.v = at + 1
        return W(encoded.substring(pin, at).splitToSequence(regStringSplit).map{decodeString(it)}.toList())
    }
    internal val decoders:HashMap<KClass<*>,(Cursor, Field<*>)->Wrap<Any>> = hashMapOf(
        IntField::class to { c, f-> value(c, f){toIntOrNull()} },
        ShortField::class to { c, f-> value(c, f){toShortOrNull()} },
        LongField::class to { c, f-> value(c, f){toLongOrNull()} },
        UIntField::class to { c, f-> value(c, f){toUIntOrNull()} },
        UShortField::class to { c, f-> value(c, f){toUShortOrNull()} },
        ULongField::class to { c, f-> value(c, f){toULongOrNull()} },
        FloatField::class to { c, f-> value(c, f){toFloatOrNull()} },
        DoubleField::class to { c, f-> value(c, f){toDoubleOrNull()} },
        BooleanField::class to { c, f-> value(c, f){toBooleanStrictOrNull()} },
        IntListField::class to { c, f-> valueList(c, f){toIntOrNull()?.let{W(it)} ?: W(FromInvalidListValue(f, this, c.v))} },
        ShortListField::class to { c, f-> valueList(c, f){toUShortOrNull()?.let{W(it)} ?: W(FromInvalidListValue(f, this, c.v))} },
        LongListField::class to { c, f-> valueList(c, f){toLongOrNull()?.let{W(it)} ?: W(FromInvalidListValue(f, this, c.v))} },
        UIntListField::class to { c, f-> valueList(c, f){toUIntOrNull()?.let{W(it)} ?: W(FromInvalidListValue(f, this, c.v))} },
        UShortListField::class to { c, f-> valueList(c, f){toUShortOrNull()?.let{W(it)} ?: W(FromInvalidListValue(f, this, c.v))} },
        ULongListField::class to { c, f-> valueList(c, f){toULongOrNull()?.let{W(it)} ?: W(FromInvalidListValue(f, this, c.v))} },
        FloatListField::class to { c, f-> valueList(c, f){toFloatOrNull()?.let{W(it)} ?: W(FromInvalidListValue(f, this, c.v))} },
        DoubleListField::class to { c, f-> valueList(c, f){toDoubleOrNull()?.let{W(it)} ?: W(FromInvalidListValue(f, this, c.v))} },
        BooleanListField::class to { c, f->
            valueList(c, f){toBooleanStrictOrNull()?.let{W(it)} ?: W(
                FromInvalidListValue(f, this, c.v)
            )}
        },
        IntMapField::class to { c, f-> valueMap(c, f){ k, v->v.toIntOrNull()?.let{W(it)} ?: W(FromInvalidMapValue(f, k, v))} },
        ShortMapField::class to { c, f-> valueMap(c, f){ k, v->v.toUShortOrNull()?.let{W(it)} ?: W(FromInvalidMapValue(f, k, v))} },
        LongMapField::class to { c, f-> valueMap(c, f){ k, v->v.toLongOrNull()?.let{W(it)} ?: W(FromInvalidMapValue(f, k, v))} },
        UIntMapField::class to { c, f-> valueMap(c, f){ k, v->v.toUIntOrNull()?.let{W(it)} ?: W(FromInvalidMapValue(f, k, v))} },
        UShortMapField::class to { c, f-> valueMap(c, f){ k, v->v.toUShortOrNull()?.let{W(it)} ?: W(FromInvalidMapValue(f, k, v))} },
        ULongMapField::class to { c, f-> valueMap(c, f){ k, v->v.toULongOrNull()?.let{W(it)} ?: W(FromInvalidMapValue(f, k, v))} },
        FloatMapField::class to { c, f-> valueMap(c, f){ k, v->v.toFloatOrNull()?.let{W(it)} ?: W(FromInvalidMapValue(f, k, v))} },
        DoubleMapField::class to { c, f-> valueMap(c, f){ k, v->v.toDoubleOrNull()?.let{W(it)} ?: W(FromInvalidMapValue(f, k, v))} },
        BooleanMapField::class to { c, f->
            valueMap(c, f){ k, v->v.toBooleanStrictOrNull()?.let{W(it)} ?: W(
                FromInvalidMapValue(f, k, v)
            )}
        },
//        UtcField::class to { _, serial, cursor, _-> KoreConverter.decodeStringValue(serial, cursor).let{ eUtc.of(it) } },
        StringField::class to {c, _-> stringValue(c) },
        StringListField::class to {c, _-> stringList(c) },
        StringMapField::class to {c, _->
            stringList(c).map{
                var key:String? = null
                it.fold(hashMapOf<String, String>()) {acc, item->
                    if(key == null) key = item
                    else{
                        acc[key!!] = item
                        key = null
                    }
                    acc
                }
            }
        },
        EnumField::class to { c, f->
            value(c, f){toIntOrNull()}.map{(f as EnumField<*>).enums[it]}
        },
        EnumListField::class to {c, f->
            val enums = (f as EnumListField<*>).enums
            valueList(c, f){toIntOrNull()?.let{W(it)} ?: W(FromInvalidListValue(f, this, c.v))}.flatMap{
                it.flatMapList {item->if(enums.size > item) W(enums[item]) else W(FromInvalidListValue(f, "$item", c.v))}
            }
        },
        EnumMapField::class to {c, f->
            val enums = (f as EnumMapField<*>).enums
            stringList(c).flatMap{
                it.flatMapListToMap {k, v->
                    v.toIntOrNull()?.let{index->
                        if(enums.size > index) W(enums[index]) else W(FromInvalidMapValue(f, k, v))
                    } ?: W(FromInvalidMapValue(f, k, v))
                }
            }
        },
        VOField::class to { c, f-> vo(c, (f as VOField<*>).factory()) },
        VOListField::class to {c, f->
            val result: ArrayList<Any> = arrayListOf()
            val factory: () -> VO = (f as VOListField<*>).factory
            c.loopItems {
                vo(c, factory()).effect{result.add(it)}
            } ?: W(result)
        },
        VOMapField::class to {c, f->
            val result:HashMap<String, VO> = hashMapOf()
            val factory: () -> VO = (f as VOMapField<*>).factory
            c.loopItems {
                stringValue(c).flatMap{ key->
                    c.v++
                    vo(c, factory()).effect{result[key] = it}
                }
            } ?: W(result)
        },
        VOSumField::class to {c, f->
            value(c, f){toIntOrNull()}.flatMap{ index->
                val factory:()->VO = (f as VOSumField<*>).sum.factories[index]
                c.v++
                vo(c, factory())
            }
        },
        VOSumListField::class to {c, f->
            val result:ArrayList<Any> = arrayListOf()
            val factories:Array<out ()->VO> = (f as VOSumListField<*>).sum.factories
            c.loopItems {
                value(c, f){toIntOrNull()}.map{
                    factories[it]
                }.flatMap{ factory->
                    c.v++
                    vo(c, factory()).effect{
                        result.add(it)
                    }
                }
            }?.let{W(it)} ?: W(result)
        },
        VOSumMapField::class to {c, f->
            val result:HashMap<String, VO> = hashMapOf()
            val factories = (f as VOSumMapField<*>).sum.factories
            c.loopItems {
                stringValue(c).flatMap{ key->
                    c.v++
                    value(c, f){toIntOrNull()}.map{factories[it]()}.flatMap{
                        c.v++
                        vo(c, it).effect{
                            result[key] = it
                        }
                    }
                }
            } ?: W(result)
        },
    )
}