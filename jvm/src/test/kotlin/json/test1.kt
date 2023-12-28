package json

import kore.json.JSON
import kore.vo.VO
import kore.vo.field.list.*
import kore.vo.field.map.*
import kore.vo.field.value.*
import kore.vo.field.vo
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class test1 {
    class Test1:VO(){
        var a by int
        var b by short
        var c by long
        var d by float
        var e by double
        var f by boolean
        var g by uint
        var h by ushort
        var i by ulong
        var j by string
    }
    @Test
    fun test1(){
        runBlocking{
            val vo = Test1().also {
                it.a = 1
                it.b = 123.toShort()
                it.c = 121444L
                it.d = 1.2f
                it.e = 1.3
                it.f = true
                it.g = 1u
                it.h = 2u
                it.i = 3u
                it.j = "abc"
            }
            val str = JSON.to(vo).fold(""){acc, c->
                acc + c
            }
            assertEquals(str, """{"a":1,"b":123,"c":121444,"d":1.2,"e":1.3,"f":true,"g":1,"h":2,"i":3,"j":"abc"}""")
        }
    }
    class Test2:VO(){
        var a by int
        var b by short
        var c by long
        var d by float
        var e by double
        var f by boolean
        var g by uint
        var h by ushort
        var i by ulong
        var j by string
        var al  by intList
        var bl  by shortList
        var cl  by longList
        var dl  by floatList
        var el  by doubleList
        var fl  by booleanList
        var gl  by uintList
        var hl  by ushortList
        var il  by ulongList
        var jl  by stringList
        var am  by intMap
        var bm  by shortMap
        var cm  by longMap
        var dm  by floatMap
        var em  by doubleMap
        var fm  by booleanMap
        var gm  by uintMap
        var hm  by ushortMap
        var im  by ulongMap
        var jm  by stringMap
    }
    @Test
    fun test2() {
        runBlocking {
            var isContinue = true
            val vo = JSON.from(Test2(), flow {
                emit("""{"a"""")
                emit(""":1, """)
                emit(""""b":12""")
                emit("""3, "c":121444, """)
                emit(""""al":[1,2,3], """)
                emit(""""am":{ "a": 1,"b" : 2, "c":3}, """)
                emit(""""bl":[1,2,3], "i":3,""")
                emit(""""dm":{ "a": 1.1,"b" : 2.1, "c":3.1}, """)
                emit(""""cl":[1,2,3], """)
                emit(""""jm":{ "a": "aew","bew" : "bwe", "c":"cds"}, """)
                emit(""""dl":[1.1,2.2,3.3], "f":true, """)
                emit(""""el":[1.1,2.1,3.1], """)
                emit(""""fl":[true,false, true], """)
                emit(""""gl":[1,2,3], """)
                emit(""""fm":{ "a": true,"b" : false, "c":true}, """)
                emit(""""hl":[1,2,3], "e":1.3, """)
                emit(""""cm":{ "a": 1,"b" : 2, "c":3}, """)
                emit(""""il":[1,2,3], """)
                emit(""""bm":{ "a": 1,"b" : 2, "c":3}, """)
                emit(""""em":{ "a": 1.3,"b" : 2.3, "c":3.3}, """)
                emit(""""im":{ "a": 1,"b" : 2, "c":3}, """)
                emit(""""jl":["a","b","c"], """)
                emit(""""gm":{ "a": 1,"b" : 2, "c":3}, """)
                emit(""""d":1.2, "g":1, "h":2, """)
                emit(""""hm":{ "a": 1,"b" : 2, "c":3}, """)
                emit(""""j":"abc"}""")
            }).takeWhile {isContinue}.last()
            assertEquals(1, vo.a)
            assertEquals(123.toShort(), vo.b)
            assertEquals(121444L, vo.c)
            assertEquals(1.2f, vo.d)
            assertEquals(1.3, vo.e)
            assertEquals(true, vo.f)
            assertEquals(1u, vo.g)
            assertEquals(2u, vo.h)
            assertEquals(3u, vo.i)
            assertEquals("abc", vo.j)
            assertEquals(listOf(1, 2, 3), vo.al)
            assertEquals(listOf(1.toShort(), 2.toShort(), 3.toShort()), vo.bl)
            assertEquals(listOf(1L, 2L, 3L), vo.cl)
            assertEquals(listOf(1.1f, 2.2f, 3.3f), vo.dl)
            assertEquals(listOf(1.1, 2.1, 3.1), vo.el)
            assertEquals(listOf(true, false, true), vo.fl)
            assertEquals(listOf(1u, 2u, 3u), vo.gl)
            assertEquals(listOf(1.toUShort(), 2.toUShort(), 3.toUShort()), vo.hl)
            assertEquals(listOf(1.toULong(), 2.toULong(), 3.toULong()), vo.il)
            assertEquals(listOf("a", "b", "c"), vo.jl)
            assertEquals(mapOf("a" to 1, "b" to 2, "c" to 3), vo.am)
            assertEquals(mapOf("a" to 1.toShort(), "b" to 2.toShort(), "c" to 3.toShort()), vo.bm)
            assertEquals(mapOf("a" to 1L, "b" to 2L, "c" to 3L), vo.cm)
            assertEquals(mapOf("a" to 1.1f, "b" to 2.1f, "c" to 3.1f), vo.dm)
            assertEquals(mapOf("a" to 1.3, "b" to 2.3, "c" to 3.3), vo.em)
            assertEquals(mapOf("a" to true, "b" to false, "c" to true), vo.fm)
            assertEquals(mapOf("a" to 1u, "b" to 2u, "c" to 3u), vo.gm)
            assertEquals(mapOf("a" to 1.toUShort(), "b" to 2.toUShort(), "c" to 3.toUShort()), vo.hm)
            assertEquals(mapOf("a" to 1.toULong(), "b" to 2.toULong(), "c" to 3.toULong()), vo.im)
            assertEquals(mapOf("a" to "aew", "bew" to "bwe", "c" to "cds"), vo.jm)
        }
    }

    class Test3:VO(){
        class Sub:VO(){
            var a by int
            var b by string
        }
        var a by int
        var b by vo(::Sub)
        var c by string
    }
    @Test
    fun test3(){
        runBlocking{
            val vo = Test3().also {
                it.a = 1
                it.b = Test3.Sub().also {
                    it.a = 2
                    it.b = "abc"
                }
                it.c = "def"
            }
            val str = JSON.to(vo).fold(""){acc, c->
                acc + c
            }
            assertEquals(str, """{"a":1,"b":{"a":2,"b":"abc"},"c":"def"}""")
            val parsed = JSON.from(Test3(), flow{emit(str)}).last()
            assertEquals(vo.a, parsed.a)
            assertEquals(vo.b.a, parsed.b.a)
            assertEquals(vo.b.b, parsed.b.b)
            assertEquals(vo.c, parsed.c)
        }
    }
}