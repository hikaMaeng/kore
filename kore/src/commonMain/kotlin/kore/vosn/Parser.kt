@file:Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE", "FunctionName")

package kore.vosn

import kore.vo.VO
import kore.vo.field.*

internal class Parser<V:VO>(val vo:V){
    private data class Stack(val type:Any, val target:Any, var key:String = "")
    private var curr:Stack = Stack(vo, vo)
    private val stack:ArrayList<Stack> = arrayListOf(curr)

    private var state:Int = 100
    private var next:Int = 0
    private var s:String = ""
    private var c:Int = 0
    private var buffer:StringBuilder = StringBuilder(1000)
    private var flushed:String = ""
    private inline fun err(msg:String):Nothing = throw Throwable(msg + " $state, $c, ${s[c]}, $s")

    operator fun invoke(input:String):Pair<String, V>? = if(input.isEmpty()) null else{
        s = input
        c = 0
        run()?.let{it to vo}
    }
    private inline fun run():String?{
        while(c < s.length){
            when(state){
                0->spaceRead()
                10->if(s[c++] == '"') state = 11 else err("invalid string start")
                11->readWhile(true){it != '"'}
                20->readWhile(false){it in "0123456789-."}
                30->wordRead("true")
                40->wordRead("false")
                50->wordRead("null")
                100->skipSpace(101) /** start point */
                101->if(s[c++] == '{') skipSpace(102) else err("invalid object")
                102->stringRead(103) /** key */
                103->{
                    curr.key = flushed
                    skipSpace(104)
                }
                104->if(s[c++] == ':') skipSpace(105) else err("invalid colon next key, ${curr.key}--")
                105->when(s[c]){ /** value or push stack */
                    '"'-> stringRead(106)
                    '0','1','2','3','4','5','6','7','8','9','-','.'->numRead(106)
                    't'->trueRead(106)
                    'f'->falseRead(106)
                    'n'->nullRead(106)
                    '[','{'->{
                        val (type, target, key) = curr
                        when(target){
                            is VO->target.getFields()?.get(key)?.let{field->
                                stack.add(Stack(
                                    field,
                                    when(field){
                                        is VOSumField<*>->hashMapOf<String, String>()
                                        else->target[key] ?: field.defaultFactory()
                                    }
                                ).also{curr = it})
                            } ?: err("invalid VO field")
                            is MutableList<*>->when(type){
                                is VOListField<*>->type.factory().let{vo->stack.add(Stack(vo, vo).also{curr = it})}
                                is VOSumListField<*>->stack.add(Stack(type, hashMapOf<String, String>()).also{curr = it})
                                else->err("invalid stack open []")
                            }
                            is MutableMap<*, *>->when(type){
                                is VOMapField<*>->type.factory().let{vo->stack.add(Stack(vo, vo).also {curr = it})}
                                is VOSumMapField<*>->stack.add(Stack(type, hashMapOf<String, String>()).also {curr = it})
                                else->err("invalid stack open {}")
                            }
                            else->err("invalid stack open")
                        }
                        c++
                        nextItem()
                    }
                    else->err("invalid value")
                }
                106->{ /** assign */
                    val (type, target, key) = curr
                    when(target){
                         is VO->{
                             target.getFields()?.get(key)?.let{f->
                                 target[key] = VSON.parseValue(f::class, f, flushed)
                                 if(stack.size == 1){
                                     state = 107
                                     return s.substring(c)
                                 }
                             } ?: err("invalid VO field")
                        }
                        is MutableMap<*, *>->(target as MutableMap<String, Any>)[key] = when(type){
                            is VOSumField<*>, is VOSumListField<*>, is VOSumMapField<*>->flushed
                            else->VSON.parseValue(type::class, type, flushed)
                        }
                        is MutableList<*>->(target as? MutableList<Any>)?.add(VSON.parseValue(type::class, type, flushed))
                        else->err("invalid value")
                    }
                    skipSpace(108)
                }
                107->skipSpace(108)
                108->when(s[c++]){ /** next or pop stack */
                    ','->nextItem()
                    '}', ']'->{
                        if(stack.size == 1){
                            state = 1000
                            return null
                        }
                        val prev:Stack = curr
                        stack.removeLast()
                        curr = stack.last()
                        val (type, target, key) = curr
                        when(target){
                            is VO->{
                                target[key] = when(prev.type){
                                    is VOSumField<*>->mapToSum(prev.target, prev.type.sum.factories) ?: err("invalid sum")
                                    else->prev.target
                                }
                            }
                            is MutableList<*>->when(type){
                                is VOSumListField<*>->(target as MutableList<Any>).add(mapToSum(prev.target, type.sum.factories) ?: err("invalid sum"))
                                else->(target as? MutableList<Any>)?.add(prev.target)
                            }
                            is MutableMap<*, *>->(target as MutableMap<String, Any>)[key] = when(type){
                                is VOSumMapField<*>->mapToSum(prev.target, type.sum.factories) ?: err("invalid sum")
                                else->prev.target
                            }
                            else->err("invalid value")
                        }
                        if(target == vo){
                            state = 109
                            return s.substring(c)
                        }else skipSpace(108)
                    }
                }
                109->skipSpace(108)
                1000->{/** terminal */}
            }
        }
        return null
    }
    private inline fun nextItem() = skipSpace(if(curr.target is MutableList<*>) 105 else 102)
    private inline fun skipSpace(to:Int) = prepare(to, 0, false)
    private inline fun stringRead(to:Int) = prepare(to, 10, true)
    private inline fun numRead(to:Int) = prepare(to, 20, true)
    private inline fun trueRead(to:Int) = prepare(to, 30, true)
    private inline fun falseRead(to:Int) = prepare(to, 40, true)
    private inline fun nullRead(to:Int) = prepare(to, 50, true)

    private inline fun prepare(to:Int, s:Int, useBuffer:Boolean){
        next = to
        state = s
        if(useBuffer) buffer.clear()
    }
    private inline fun readWhile(isDropLast:Boolean, block:(Char)->Boolean){
        do{
            val it = s[c++]
            if(block(it)) buffer.append(it) else {
                flushed = buffer.toString()
                state = next
                c -= if(isDropLast) 0 else 1
                break
            }
        }while(c < s.length)
    }
    private inline fun wordRead(word:String){
        do{
            if(buffer.length == word.length){
                flushed = buffer.toString()
                state = next
                break
            }else{
                val it = s[c++]
                if(word[buffer.length] == it) buffer.append(it)
                else err("invalid $word")
            }
        }while(c < s.length)
    }
    private inline fun spaceRead(){
        do{
            val it = s[c]
            if(" \t\n\r".indexOf(it) != -1) c++ else{
                state = next
                break
            }
        }while(c < s.length)
    }
    private inline fun mapToSum(map:Any, factories:Array<out ()->VO>):Any?{
        map as Map<String, String>
        val mapKeys:Set<String> = map.keys
        factories.any{
            val vo:VO = it()
            val keys:MutableSet<String> = vo.values.keys
            if(keys == mapKeys){
                val fields:Map<String, Field<*>> = vo.getFields()!!
                keys.forEach {key->
                    val t = fields[key]!!
                    vo[key] = VSON.parseValue(t::class, t, map[key]!!)
                }
                return vo
            }else false
        }
        return null
    }
}