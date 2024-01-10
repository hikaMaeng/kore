@file:Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE", "FunctionName")

package kore.vbp

import kore.vo.VO
import kore.vo.converter.ToVONoInitialized
import kore.vo.field
import kore.vo.field.*
import kore.vo.task.Task
import kore.vosn.VSON
import kotlin.collections.EmptyMap.keys

internal class Parser<V:VO>(val vo:V){
    companion object{
        val EMPTY:ByteArray = byteArrayOf()
    }
    private data class Stack(val type:Any, val target:Any, var key:String = "", var index:Int = 0U)
    private var curr:Stack = Stack(vo, vo)
    private var currArray:ByteArray = byteArrayOf()
    private val stack:ArrayList<Stack> = arrayListOf(curr)

    private var state:Int = 100
    private var next:Int = 0
    private var s:ByteArray = byteArrayOf()
    private var buffer:StringBuilder = StringBuilder(1000)
    private var flushed:String = ""
    private inline fun err(msg:String):Nothing = throw Throwable(msg + " $state, $c, ${s[c]}, $s")

    operator fun invoke(input:ByteArray):Pair<ByteArray, V>? = if(input.isEmpty()) null else{
        if(input.isNotEmpty()) s += input
        run()?.let{it to vo}
    }
    private inline fun run():ByteArray?{
        while(s.isNotEmpty()){
            when(state){
                100->{
                    val (type, target, key) = curr
                    when(target){
                        is VO->{
                            val v:Int = s[0].toInt()
                            s = s.copyOfRange(1, s.size)
                            if(v == 255) state = 2000 else {
                                curr.index = v
                                state = 101
                            }
                        }
                        is MutableMap<*, *>->(target as MutableMap<String, Any>)[key] = when(type){
                            is VOSumField<*>, is VOSumListField<*>, is VOSumMapField<*>->flushed
                            else->VSON.parseValue(type::class, type, flushed)
                        }
                        is MutableList<*>->(target as? MutableList<Any>)?.add(VSON.parseValue(type::class, type, flushed))
                        else->err("invalid value")
                    }
                }
                101->{
                    val vo:VO = curr.target as VO
                    val (key:String, field:Field<*>) = vo.field(curr.index)
                    when(field){
                        is ListFields<*>->state = 1010
                        is MapFields<*>->state = 1020
                        is VOField<*>->state = 1030
                        is VOListField<*>->state = 1040
                        is VOMapField<*>->state = 1050
                        is VOSumField<*>->state = 1060
                        is VOSumListField<*>->state = 1070
                        is VOSumMapField<*>->state = 1080
                        is EnumListField<*>->state = 1090
                        is EnumMapField<*>->state = 1100
                        else->VBP.parseValue(field::class, field, s)?.let{(v, arr)->
                            vo[key] = when(field){
                                is EnumField<*>->field.enums[v as Int]
                                else->v
                            }
                            s = arr
                            state = 100
                        } ?: return null
                    }
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