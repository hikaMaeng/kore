package json

import kore.json.JSON
import kore.vo.VO
import kore.vo.VOSum
import kore.vo.field.*
import kore.vo.field.list.*
import kore.vo.field.map.*
import kore.vo.field.value.*
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
    class Test4:VO(){
        class Sub:VO(){
            var a by int
            var b by string
        }
        var a by int
        var b by voList(::Sub)
        var c by voMap(::Sub)
    }
    @Test
    fun test4(){
        runBlocking{
            val vo = Test4().also {
                it.a = 1
                it.b = arrayListOf(Test4.Sub().also {
                    it.a = 2
                    it.b = "abc"
                }, Test4.Sub().also {
                    it.a = 3
                    it.b = "def"
                })
                it.c = hashMapOf("a" to Test4.Sub().also {
                    it.a = 4
                    it.b = "ghi"
                }, "b" to Test4.Sub().also {
                    it.a = 5
                    it.b = "jkl"
                })
            }
            val str = JSON.to(vo).fold(""){acc, c->
                acc + c
            }
            assertEquals(str, """{"a":1,"b":[{"a":2,"b":"abc"},{"a":3,"b":"def"}],"c":{"a":{"a":4,"b":"ghi"},"b":{"a":5,"b":"jkl"}}}""")
            //val str = """{"a":1,"b":[{"a":2,"b":"abc"},{"a":3,"b":"def"}],"c":{"a":{"a":4,"b":"ghi"},"b":{"a":5,"b":"jkl"}}}"""
            val parsed = JSON.from(Test4(), flow{emit(str)}).last()
            assertEquals(vo.a, parsed.a)
            assertEquals(vo.b[0].a, parsed.b[0].a)
            assertEquals(vo.b[0].b, parsed.b[0].b)
            assertEquals(vo.b[1].a, parsed.b[1].a)
            assertEquals(vo.b[1].b, parsed.b[1].b)
            assertEquals(vo.c["a"]?.a, parsed.c["a"]?.a)
            assertEquals(vo.c["a"]?.b, parsed.c["a"]?.b)
            assertEquals(vo.c["b"]?.a, parsed.c["b"]?.a)
            assertEquals(vo.c["b"]?.b, parsed.c["b"]?.b)
        }
    }
    class Test5:VO(){
        enum class E{A, B, C}
        var a by enum<E>()
        var b by enumList<E>()
        var c by enumMap<E>()
    }
    @Test
    fun test5(){
        runBlocking{
            val vo = Test5().also {
                it.a = Test5.E.B
                it.b = arrayListOf(Test5.E.A, Test5.E.C)
                it.c = hashMapOf("a" to Test5.E.A, "b" to Test5.E.C)
            }
            val str = JSON.to(vo).fold(""){acc, c->
                acc + c
            }
            assertEquals(str, """{"a":1,"b":[0,2],"c":{"a":0,"b":2}}""")
            val parsed = JSON.from(Test5(), flow{emit(str)}).last()
            assertEquals(vo.a, parsed.a)
            assertEquals(vo.b[0], parsed.b[0])
            assertEquals(vo.b[1], parsed.b[1])
            assertEquals(vo.c["a"], parsed.c["a"])
            assertEquals(vo.c["b"], parsed.c["b"])

        }
    }
    class Test6:VO(){
        sealed class Sum:VO(){
            companion object:VOSum<Sum>(::A, ::B)
            class A:Sum(){
                var a by int
                var b by string
            }
            class B:Sum(){
                var c by int
                var d by string
            }
            var e by boolean
            var f by double
        }
        var a by sum(Sum)
        var b by sumList(Sum)
        var c by sumMap(Sum)
    }
    @Test
    fun test6(){
        runBlocking {
            val vo = Test6().also {
                it.a = Test6.Sum.A().also {
                    it.a = 1
                    it.b = "abc"
                    it.e = true
                    it.f = 1.2
                }
                it.b = arrayListOf(Test6.Sum.A().also {
                    it.a = 2
                    it.b = "def"
                    it.e = true
                    it.f = 1.2
                }, Test6.Sum.B().also {
                    it.c = 3
                    it.d = "ghi"
                    it.e = false
                    it.f = 1.3
                })
                it.c = hashMapOf("a" to Test6.Sum.A().also {
                    it.a = 4
                    it.b = "jkl"
                    it.e = true
                    it.f = 1.4
                }, "b" to Test6.Sum.B().also {
                    it.c = 5
                    it.d = "mno"
                    it.e = false
                    it.f = 1.5
                })
            }
            val str = JSON.to(vo).fold(""){acc, c->
                acc + c
            }
//            assertEquals(str, """{"a":{"e":true,"f":1.2,"a":1,"b":"abc"},"b":[{"e":true,"f":1.2,"a":2,"b":"def"},{"e":false,"f":1.3,"c":3,"d":"ghi"}],"c":{"a":{"e":true,"f":1.4,"a":4,"b":"jkl"},"b":{"e":false,"f":1.5,"c":5,"d":"mno"}}}""")
            println(str)
            val parsed = JSON.from(Test6(), flow{emit(str)}).last()
            println("--------------")
            assertEquals(vo.a::class, parsed.a::class)
            assertEquals((vo.a as Test6.Sum.A).a, (parsed.a as Test6.Sum.A).a)
            assertEquals((vo.a as Test6.Sum.A).b, (parsed.a as Test6.Sum.A).b)
            assertEquals(vo.b[0]::class, parsed.b[0]::class)
            println("aaaa2")
            assertEquals((vo.b[0] as Test6.Sum.A).a, (parsed.b[0] as Test6.Sum.A).a)
            assertEquals((vo.b[0] as Test6.Sum.A).b, (parsed.b[0] as Test6.Sum.A).b)
            assertEquals((vo.b[0] as Test6.Sum.A).e, (parsed.b[0] as Test6.Sum.A).e)
            assertEquals((vo.b[0] as Test6.Sum.A).f, (parsed.b[0] as Test6.Sum.A).f)
            assertEquals(vo.b[1]::class, parsed.b[1]::class)
            assertEquals((vo.b[1] as Test6.Sum.B).c, (parsed.b[1] as Test6.Sum.B).c)
            assertEquals((vo.b[1] as Test6.Sum.B).d, (parsed.b[1] as Test6.Sum.B).d)
            assertEquals((vo.b[1] as Test6.Sum.B).e, (parsed.b[1] as Test6.Sum.B).e)
            assertEquals((vo.b[1] as Test6.Sum.B).f, (parsed.b[1] as Test6.Sum.B).f)
            assertEquals(vo.c["a"]!!::class, parsed.c["a"]!!::class)
            assertEquals((vo.c["a"]!! as Test6.Sum.A).a, (parsed.c["a"]!! as Test6.Sum.A).a)
            assertEquals((vo.c["a"]!! as Test6.Sum.A).b, (parsed.c["a"]!! as Test6.Sum.A).b)
            assertEquals((vo.c["a"]!! as Test6.Sum.A).e, (parsed.c["a"]!! as Test6.Sum.A).e)
            assertEquals((vo.c["a"]!! as Test6.Sum.A).f, (parsed.c["a"]!! as Test6.Sum.A).f)
            assertEquals(vo.c["b"]!!::class, parsed.c["b"]!!::class)
            assertEquals((vo.c["b"]!! as Test6.Sum.B).c, (parsed.c["b"]!! as Test6.Sum.B).c)
            assertEquals((vo.c["b"]!! as Test6.Sum.B).d, (parsed.c["b"]!! as Test6.Sum.B).d)
            assertEquals((vo.c["b"]!! as Test6.Sum.B).e, (parsed.c["b"]!! as Test6.Sum.B).e)
            assertEquals((vo.c["b"]!! as Test6.Sum.B).f, (parsed.c["b"]!! as Test6.Sum.B).f)
        }
    }
}