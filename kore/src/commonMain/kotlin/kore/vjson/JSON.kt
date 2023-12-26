@file:Suppress("NOTHING_TO_INLINE", "FunctionName", "UNCHECKED_CAST")

package kore.vjson

import kore.vo.VO
import kore.vo.field.list.*
import kore.vo.field.value.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow

object JSON{
    private val _converters:HashMap<Any, (String)->Any> = HashMap<Any, (String)->Any>(100).also{
        val cInt:(String)->Any = {it.toInt()}
        val cShort:(String)->Any = {it.toShort()}
        val cLong:(String)->Any = {it.toLong()}
        val cFloat:(String)->Any = {it.toFloat()}
        val cDouble:(String)->Any = {it.toDouble()}
        val cBoolean:(String)->Any = {it.toBoolean()}
        val cUInt:(String)->Any = {it.toUInt()}
        val cUShort:(String)->Any = {it.toUShort()}
        val cULong:(String)->Any = {it.toULong()}
        val cString:(String)->Any = {it}

        it[Int::class] = cInt
        it[Short::class] = cShort
        it[Long::class] = cLong
        it[Float::class] = cFloat
        it[Double::class] = cDouble
        it[Boolean::class] = cBoolean
        it[UInt::class] = cUInt
        it[UShort::class] = cUShort
        it[ULong::class] = cULong
        it[String::class] = cString

        it[IntField::class] = cInt
        it[ShortField::class] = cShort
        it[LongField::class] = cLong
        it[FloatField::class] = cFloat
        it[DoubleField::class] = cDouble
        it[BooleanField::class] = cBoolean
        it[UIntField::class] = cUInt
        it[UShortField::class] = cUShort
        it[ULongField::class] = cULong
        it[StringField::class] = cString

        it[IntListField::class] = cInt
        it[ShortListField::class] = cShort
        it[LongListField::class] = cLong
        it[FloatListField::class] = cFloat
        it[DoubleListField::class] = cDouble
        it[BooleanListField::class] = cBoolean
        it[UIntListField::class] = cUInt
        it[UShortListField::class] = cUShort
        it[ULongListField::class] = cULong
        it[StringListField::class] = cString

        it[VO::class] =  {it}
        it[MutableList::class] = {it:String->it}
        it[MutableMap::class] = {it:String->it}
    }
    operator fun set(type:Any, converter:(String)->Any){
        _converters[type] = converter
    }
    operator fun get(type:Any):(String)->Any = _converters[type] ?: throw Throwable("invalid type $type")
    fun <V:VO> from(vo:V, value:Flow<String>):Flow<V> = flow{
        val emitter:FlowCollector<V> = this
        val parser:Parser<V> = Parser(vo)
        value
            .collect {
                var str:String? = it
                while(str != null) str = parser(str)?.let{(s, vo)->
                    emitter.emit(vo)
                    s
                }
            }
    }
}
class Parser<V:VO>(val vo:V){
    data class Stack(val type:Any, val target:Any, var key:String = "")
    private var curr:Stack = Stack(vo, vo)
    private val stack:ArrayList<Stack> = arrayListOf(curr)
    private var input:String? = null
    var state:Int = 100
    var next:Int = 0
    var s:String = ""
    var c:Int = 0
    var buffer:StringBuilder = StringBuilder(1000)
    var flushed:String = ""
    inline fun err(msg:String):Nothing = throw Throwable(msg + " $state, $c, ${s[c]}, $s")

    private val sequence:Iterator<Pair<String, V>?> = sequence{
        while(true) yield(input?.let{inp->
            input = null
            run(inp.ifEmpty {return@let null})?.let{it to vo}
        })
    }.iterator()
    operator fun invoke(s:String):Pair<String, V>?{
        input = s
        return sequence.next()
    }
    private inline fun run(input:String):String?{
        s = input
        c = 0
        while(c < s.length){
            when(state){
                0->_skipSpace()
                10->if(s[c++] == '"') state = 11 else err("invalid string start")
                11->readWhile(true){it != '"'}
                20->readWhile(false){it in "0123456789-."}
                30->wordRead("true")
                40->wordRead("false")
                50->wordRead("null")
                100->skipSpace(101)
                101->if(s[c++] == '{') skipSpace(102) else err("invalid object")
                102->stringRead(103)
                103->{
                    curr.key = flushed
                    skipSpace(104)
                }
                104->if(s[c++] == ':') skipSpace(105) else err("invalid colon next key, ${curr.key}--")
                105->{ /** value or push stack */
                    val it:Char = s[c]
                    when{
                        it == '"'-> stringRead(106)
                        "0123456789-.".indexOf(it) != -1->numRead(106)
                        it == 't'->trueRead(106)
                        it == 'f'->falseRead(106)
                        it == 'n'->nullRead(106)
                        it == '['->{
                            (curr.target as? VO)?.let{vo->
                                stack.add(
                                    Stack(
                                        vo.getFields()?.get(curr.key)?.let{it::class} ?: err("invalid VO field"),
                                        try{
                                            vo[curr.key] ?: throw Throwable()
                                        }catch(e:Throwable){
                                            arrayListOf<Any>().also{vo[curr.key] = it}
                                        }
                                    ).also{curr = it}
                                )
                            }
                            c++
                            skipSpace(105)
                        }
//                        it == '{'->{
//                            targetStack.add(VOJson.Updater.Stack(vo[key]!!, vo, key))
//                            skipSpace(102, v, c + 1)
//
//                        }
                        else->err("invalid VO value")
                    }
                }
                106->{ /** assign */
                    when{
                        curr.type is VO->(curr.target as? VO)?.let{
                            it[curr.key] = JSON[vo.getFields()?.get(curr.key)?.let{
//                                println("---${curr.key} : ${it::class} -- $flushed, ${JSON[it::class]}, ${JSON[it::class](flushed)}")
                                it::class
                            } ?: err("invalid VO field")](flushed)
                            if(stack.size == 1){
                                state = 107
                                return s.substring(c)
                            }
                        }
                        curr.target is MutableList<*>->(curr.target as? MutableList<Any>)?.add(JSON[curr.type](flushed))
                        else->err("invalid VO value")
                    }
                    skipSpace(108)
                }
                107->skipSpace(108)
                108->when(s[c++]){ /** next or pop stack */
                    ','->skipSpace(if(curr.target is MutableList<*>) 105 else 102)
                    '}', ']'->{
                        if(stack.size == 1){
                            state = 1000
                            return null
                        }
                        val prev:Stack = curr
                        stack.removeLast()
                        curr = stack.last()
                        when{
                            curr.type is VO->(curr.target as? VO)?.let{
                                it[curr.key] = prev.target
                                skipSpace(108)
                            }
                        }
                    }
                }
                1000->{}
            }
        }
        return null
    }
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
    private inline fun skipSpace(to:Int) = prepare(to, 0, false)
    private inline fun stringRead(to:Int) = prepare(to, 10, true)
    private inline fun numRead(to:Int) = prepare(to, 20, true)
    private inline fun trueRead(to:Int) = prepare(to, 30, true)
    private inline fun falseRead(to:Int) = prepare(to, 40, true)
    private inline fun nullRead(to:Int) = prepare(to, 50, true)
    private inline fun _skipSpace(){
        do{
            val it = s[c]
            if(" \t\n\r".indexOf(it) != -1) c++ else{
                state = next
                break
            }
        }while(c < s.length)
    }
}