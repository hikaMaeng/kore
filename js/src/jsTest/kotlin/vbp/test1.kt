@file:Suppress("OPT_IN_USAGE")

package vbp

import kore.vbp.VBP
import kore.vo.VO
import kore.vo.VOSum
import kore.vo.field.*
import kore.vo.field.list.intList
import kore.vo.field.list.stringList
import kore.vo.field.map.intMap
import kore.vo.field.map.stringMap
import kore.vo.field.value.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.promise
import kotlinx.io.*
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
        assertEquals(1, 1)
        GlobalScope.promise{
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
            val arr = VBP.to(vo).fold(byteArrayOf()){acc, c->
                acc + c
            }
            println("---${arr.joinToString { "$it" }}---")
            val buffer = Buffer().also {
                it.writeByte(0)
                it.writeInt(1)
                it.writeByte(1)
                it.writeShort(123)
                it.writeByte(2)
                it.writeLong(121444)
                it.writeByte(3)
                it.writeFloat(1.2f)
                it.writeByte(4)
                it.writeDouble(1.3)
                it.writeByte(5)
                it.writeByte(1)
                it.writeByte(6)
                it.writeUInt(1u)
                it.writeByte(7)
                it.writeUShort(2u)
                it.writeByte(8)
                it.writeULong(3u)
                it.writeByte(9)
                it.writeString("abc")
                it.writeByte(0)
                it.writeByte(-1)
            }.readByteArray()
            assertEquals(arr.joinToString{"$it"}, buffer.joinToString{"$it"})
            val v = VBP.from(Test1(), flow{emit(arr)}).last()
            assertEquals(v.a, 1)
            assertEquals(v.b, 123.toShort())
            assertEquals(v.c, 121444L)
            assertEquals(v.d, 1.2f)
            assertEquals(v.e, 1.3)
            assertEquals(v.f, true)
            assertEquals(v.g, 1u)
            assertEquals(v.h, 2u)
            assertEquals(v.i, 3u)
            assertEquals(v.j, "abc")
        }
    }
    class Test2:VO(){
        var a by int
        var b by short{
            exclude()
        }
        var c by long{
            optinal()
        }
        var d by float
        var e by string{
            exclude()
        }
    }
    @Test
    fun test2(){
        GlobalScope.promise{
            var vo = Test2().also {
                it.a = 1
                it.b = 123.toShort()
                it.d = 1.2f
            }
            run{
                val arr = VBP.to(vo).fold(byteArrayOf()){acc, c->
                    acc + c
                }
                val buffer = Buffer().also {
                    it.writeByte(0)
                    it.writeInt(1)
                    it.writeByte(3)
                    it.writeFloat(1.2f)
                    it.writeByte(-1)
                }.readByteArray()
                //exclude에 값이 있는데 제거되는지
                assertEquals(arr.joinToString{"$it"}, buffer.joinToString{"$it"})
            }
            run{
                vo = Test2().also {
                    it.a = 1
                    it.d = 1.2f
                }
                val arr = VBP.to(vo).fold(byteArrayOf()){acc, c->
                    acc + c
                }
                val buffer = Buffer().also {
                    it.writeByte(0)
                    it.writeInt(1)
                    it.writeByte(3)
                    it.writeFloat(1.2f)
                    it.writeByte(-1)
                }.readByteArray()
                //exclude에 값이 없는데 제거되는지
                assertEquals(arr.joinToString{"$it"}, buffer.joinToString{"$it"})
            }
            run {

                vo = Test2().also {
                    it.a = 1
                    it.c = 15L
                    it.d = 1.2f
                }
                val arr = VBP.to(vo).fold(byteArrayOf()){acc, c->
                    acc + c
                }
                val buffer = Buffer().also {
                    it.writeByte(0)
                    it.writeInt(1)
                    it.writeByte(2)
                    it.writeLong(15L)
                    it.writeByte(3)
                    it.writeFloat(1.2f)
                    it.writeByte(-1)
                }.readByteArray()
                //옵셔널에 값을 넣은 경우
                assertEquals(arr.joinToString{"$it"}, buffer.joinToString{"$it"})
            }
        }
    }
    class Test3:VO(){
        var a by intList
        var b by stringList
        var c by intMap
        var d by stringMap
    }
    @Test
    fun test3(){
        GlobalScope.promise{
            val vo = Test3().also {
                it.a = mutableListOf(1, 2, 3)
                it.b = mutableListOf("a", "b", "c")
                it.c = mutableMapOf("a" to 2, "b" to 3)
                it.d = mutableMapOf("a" to "b", "c" to "d")
            }
            assertEquals(vo.a, mutableListOf(1, 2, 3))
            assertEquals(vo.b, mutableListOf("a", "b", "c"))
            assertEquals(vo.c, mutableMapOf("a" to 2, "b" to 3))
            assertEquals(vo.d, mutableMapOf("a" to "b", "c" to "d"))
            val arr = VBP.to(vo).fold(byteArrayOf()){acc, c->
                acc + c
            }
            val buffer = Buffer().also {
                it.writeByte(0)
                it.writeInt(3)
                it.writeInt(1)
                it.writeInt(2)
                it.writeInt(3)
                it.writeByte(1)
                it.writeInt(3)
                it.writeString("a")
                it.writeByte(0)
                it.writeString("b")
                it.writeByte(0)
                it.writeString("c")
                it.writeByte(0)
                it.writeByte(2)
                it.writeInt(2)
                it.writeString("a")
                it.writeByte(0)
                it.writeInt(2)
                it.writeString("b")
                it.writeByte(0)
                it.writeInt(3)
                it.writeByte(3)
                it.writeInt(2)
                it.writeString("a")
                it.writeByte(0)
                it.writeString("b")
                it.writeByte(0)
                it.writeString("c")
                it.writeByte(0)
                it.writeString("d")
                it.writeByte(0)
                it.writeByte(-1)
            }.readByteArray()
            assertEquals(arr.joinToString{"$it"}, buffer.joinToString{"$it"})
            val v = VBP.from(Test3(), flow{emit(arr)}).last()
            assertEquals(v.a, mutableListOf(1, 2, 3))
            assertEquals(v.b, mutableListOf("a", "b", "c"))
            assertEquals(v.c, mutableMapOf("a" to 2, "b" to 3))
            assertEquals(v.d, mutableMapOf("a" to "b", "c" to "d"))
        }
    }
    class Test4:VO(){
        class Sub:VO(){
            var a by int
            var b by string
        }

        var a by intList
        var b by vo(::Sub)
        var c by intMap
    }
    @Test
    fun test4() {
        GlobalScope.promise {
            val vo = Test4().also {
                it.a = mutableListOf(1, 2, 3)
                it.b = Test4.Sub().also {sub->
                    sub.a = 1
                    sub.b = "abc"
                }
                it.c = mutableMapOf("a" to 2, "b" to 3)
            }
            assertEquals(vo.a, mutableListOf(1, 2, 3))
            assertEquals(vo.b.a, 1)
            assertEquals(vo.b.b, "abc")
            assertEquals(vo.c, mutableMapOf("a" to 2, "b" to 3))
            val arr = VBP.to(vo).fold(byteArrayOf()){acc, c->
                acc + c
            }
            val buffer = Buffer().also {
                it.writeByte(0)
                it.writeInt(3)
                it.writeInt(1)
                it.writeInt(2)
                it.writeInt(3)
                it.writeByte(1)
                it.writeByte(0)
                it.writeInt(1)
                it.writeByte(1)
                it.writeString("abc")
                it.writeByte(0)
                it.writeByte(-1)
                it.writeByte(2)
                it.writeInt(2)
                it.writeString("a")
                it.writeByte(0)
                it.writeInt(2)
                it.writeString("b")
                it.writeByte(0)
                it.writeInt(3)
                it.writeByte(-1)
            }.readByteArray()
            assertEquals(arr.joinToString{"$it"}, buffer.joinToString{"$it"})
            val v = VBP.from(Test4(), flow{emit(arr)}).last()
            assertEquals(v.a, mutableListOf(1, 2, 3))
            assertEquals(v.b.a, 1)
            assertEquals(v.b.b, "abc")
            assertEquals(v.c, mutableMapOf("a" to 2, "b" to 3))
        }
    }
    class Test5:VO(){
        class Sub:VO(){
            var a by int
            var b by string
        }

        var a by voList(::Sub)
        var b by vo(::Sub)
        var c by voMap(::Sub)
    }
    @Test
    fun test5() {
        GlobalScope.promise {
            val vo = Test5().also {
                it.a = mutableListOf(
                    Test5.Sub().also {sub->
                        sub.a = 1
                        sub.b = "a"
                    },
                    Test5.Sub().also {sub->
                        sub.a = 2
                        sub.b = "b"
                    }
                )
                it.b = Test5.Sub().also {sub->
                    sub.a = 3
                    sub.b = "c"
                }
                it.c = mutableMapOf(
                    "a" to Test5.Sub().also {sub->
                        sub.a = 4
                        sub.b = "d"
                    },
                    "b" to Test5.Sub().also {sub->
                        sub.a = 5
                        sub.b = "e"
                    }
                )
            }
            assertEquals(vo.a[0].a, 1)
            assertEquals(vo.a[0].b, "a")
            assertEquals(vo.a[1].a, 2)
            assertEquals(vo.a[1].b, "b")
            assertEquals(vo.b.a, 3)
            assertEquals(vo.b.b, "c")
            assertEquals(vo.c["a"]?.a, 4)
            assertEquals(vo.c["a"]?.b, "d")
            assertEquals(vo.c["b"]?.a, 5)
            assertEquals(vo.c["b"]?.b, "e")

            val arr = VBP.to(vo).fold(byteArrayOf()){acc, c->
                acc + c
            }
            println("ARR ${arr.joinToString { "$it" }}")
            val v = VBP.from(Test5(), flow{emit(arr)}).last()
            assertEquals(v.a[0].a, 1)
            assertEquals(v.a[0].b, "a")
            assertEquals(v.a[1].a, 2)
            assertEquals(v.a[1].b, "b")
            assertEquals(v.b.a, 3)
            assertEquals(v.b.b, "c")
            assertEquals(v.c["a"]?.a, 4)
            assertEquals(v.c["a"]?.b, "d")
            assertEquals(v.c["b"]?.a, 5)
            assertEquals(v.c["b"]?.b, "e")
        }
    }
    class Test6:VO(){
        sealed class Sum:VO(){
            companion object:VOSum<Sum>(::A, ::B)
            class A:Sum(){
                var c by boolean
            }
            class B:Sum(){
                var d by long
            }
            var a by int
            var b by string
        }
        var a by sum(Sum)
        var b by int
        var c by sumList(Sum)
        var d by sumMap(Sum)
    }
    @Test
    fun test6() {
        GlobalScope.promise {
            val vo = Test6().also {
                it.a = Test6.Sum.A().also {sub->
                    sub.a = 1
                    sub.b = "a"
                    sub.c = true
                }
                it.b = 2
                it.c = mutableListOf(
                    Test6.Sum.A().also {sub->
                        sub.a = 3
                        sub.b = "b"
                        sub.c = false
                    },
                    Test6.Sum.B().also {sub->
                        sub.a = 4
                        sub.b = "c"
                        sub.d = 5
                    }
                )
                it.d = mutableMapOf(
                    "a" to Test6.Sum.A().also {sub->
                        sub.a = 6
                        sub.b = "d"
                        sub.c = true
                    },
                    "b" to Test6.Sum.B().also {sub->
                        sub.a = 7
                        sub.b = "e"
                        sub.d = 8
                    }
                )
            }
            assertEquals(vo.a.a, 1)
            assertEquals(vo.a.b, "a")
            assertEquals((vo.a as Test6.Sum.A).c, true)
            assertEquals(vo.b, 2)
            assertEquals(vo.c[0].a, 3)
            assertEquals(vo.c[0].b, "b")
            assertEquals((vo.c[0] as Test6.Sum.A).c, false)
            assertEquals(vo.c[1].a, 4)
            assertEquals(vo.c[1].b, "c")
            assertEquals((vo.c[1] as Test6.Sum.B).d, 5)
            assertEquals(vo.d["a"]?.a, 6)
            assertEquals(vo.d["a"]?.b, "d")
            assertEquals((vo.d["a"] as Test6.Sum.A).c, true)
            assertEquals(vo.d["b"]?.a, 7)
            assertEquals(vo.d["b"]?.b, "e")
            assertEquals((vo.d["b"] as Test6.Sum.B).d, 8)

            val arr = VBP.to(vo).fold(byteArrayOf()){acc, c->
                acc + c
            }
            println("ARR ${arr.joinToString { "$it" }}")
            val v = VBP.from(Test6(), flow{emit(arr)}).last()
            assertEquals(v.a.a, 1)
            assertEquals(v.a.b, "a")
            assertEquals((v.a as Test6.Sum.A).c, true)
            assertEquals(v.b, 2)
            assertEquals(v.c[0].a, 3)
            assertEquals(v.c[0].b, "b")
            assertEquals((v.c[0] as Test6.Sum.A).c, false)
            assertEquals(v.c[1].a, 4)
            assertEquals(v.c[1].b, "c")
            assertEquals((v.c[1] as Test6.Sum.B).d, 5)
            assertEquals(v.d["a"]?.a, 6)
            assertEquals(v.d["a"]?.b, "d")
            assertEquals((v.d["a"] as Test6.Sum.A).c, true)
            assertEquals(v.d["b"]?.a, 7)
            assertEquals(v.d["b"]?.b, "e")
            assertEquals((v.d["b"] as Test6.Sum.B).d, 8)
        }
    }
    class Test7:VO(){
        enum class E{A,B,C}
        var a by enum<E>()
        var c by enumList<E>()
        var d by enumMap<E>()
    }
    @Test
    fun test7() {
        GlobalScope.promise {
            val vo = Test7().also {
                it.a = Test7.E.A
                it.c = mutableListOf(Test7.E.B, Test7.E.C)
                it.d = mutableMapOf("a" to Test7.E.A, "b" to Test7.E.B)
            }
            assertEquals(vo.a, Test7.E.A)
            assertEquals(vo.c[0], Test7.E.B)
            assertEquals(vo.c[1], Test7.E.C)
            assertEquals(vo.d["a"], Test7.E.A)
            assertEquals(vo.d["b"], Test7.E.B)
            val arr = VBP.to(vo).fold(byteArrayOf()){acc, c->
                acc + c
            }
            println("ARR ${arr.joinToString { "$it" }}")
            val v = VBP.from(Test7(), flow{emit(arr)}).last()
            assertEquals(v.a, Test7.E.A)
            assertEquals(v.c[0], Test7.E.B)
            assertEquals(v.c[1], Test7.E.C)
            assertEquals(v.d["a"], Test7.E.A)
            assertEquals(v.d["b"], Test7.E.B)

        }
    }
}