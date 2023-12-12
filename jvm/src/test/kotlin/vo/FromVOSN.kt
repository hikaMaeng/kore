package vo

import kore.vo.VO
import kore.vo.VOSum
import kore.vosn.fromVOSN
import kore.vosn.toVOSN
import kore.vo.field.list.*
import kore.vo.field.value.*
import kore.vo.field.*
import kore.vo.field.map.*
import kotlin.test.Test
import kotlin.test.assertEquals

class FromVOSN {
    class Test1: VO(){
        var a by string
        var b by int
    }
    @Test
    fun test1(){
        val t1 = Test1().also {
            it.a = "hika"
            it.b = 3
        }
        assertEquals(t1.toVOSN()(), "hika|3|")
        val t2 = Test1().fromVOSN("hika|3|")
        assertEquals(t2()?.a, "hika")
        assertEquals(t2()?.b, 3)
    }
    class Test2:VO(){
        var a by string
        var b by int
        var c by vo(FromVOSN::Test1)
    }
    @Test
    fun test2(){
        val t1 = Test2().also {
            it.a = "hika1"
            it.b = 3
            it.c = Test1().also {
                it.a = "hika2"
                it.b = 4
            }
        }
        assertEquals(t1.toVOSN()(), "hika1|3|hika2|4||")
        val t2 = Test2().fromVOSN("hika1|3|hika2|4||")
        assertEquals(t2()?.a, "hika1")
        assertEquals(t2()?.b, 3)
        assertEquals(t2()?.c?.a, "hika2")
        assertEquals(t2()?.c?.b, 4)
    }
    class Test3:VO(){
        var a by string
        var b by stringList
        var c by int
        var d by intList
        var e by vo(FromVOSN::Test1)
        var f by voList(FromVOSN::Test1)
    }
    @Test
    fun test3(){
        val t1 = Test3().also {
            it.a = "hika1"
            it.b = arrayListOf("a", "b", "c")
            it.c = 3
            it.d = arrayListOf(1,2,3)
            it.e = Test1().also {
                it.a = "hika2"
                it.b = 4
            }
            it.f = arrayListOf(
                Test1().also {
                    it.a = "hika10"
                    it.b = 10
                },
                Test1().also {
                    it.a = "hika11"
                    it.b = 11
                }
            )
        }
        assertEquals(t1.toVOSN()(), "hika1|a|b|c@|3|1|2|3@|hika2|4||hika10|10||hika11|11|@|")
        val t2 = Test3().fromVOSN("hika1|a|b|c@|3|1|2|3@|hika2|4||hika10|10||hika11|11|@|")
        assertEquals(t2()?.a, "hika1")
        assertEquals(t2()?.b, arrayListOf("a", "b", "c"))
        assertEquals(t2()?.c, 3)
        assertEquals(t2()?.d, arrayListOf(1,2,3))
        assertEquals(t2()?.e?.a, "hika2")
        assertEquals(t2()?.e?.b, 4)
        assertEquals(t2()?.f?.get(0)?.a, "hika10")
        assertEquals(t2()?.f?.get(0)?.b, 10)
        assertEquals(t2()?.f?.get(1)?.a, "hika11")
        assertEquals(t2()?.f?.get(1)?.b, 11)
    }
    class Test4:VO(){
        var a by string
        var b by stringMap
        var c by int
        var d by intMap
        var e by vo(FromVOSN::Test1)
        var f by voMap(FromVOSN::Test1)
    }
    @Test
    fun test4(){
        val t1 = Test4().also {
            it.a = "hika1"
            it.b = hashMapOf("a" to "aa", "b" to "bb", "c" to "cc")
            it.c = 3
            it.d = hashMapOf("a" to 1, "b" to 2, "c" to 3)
            it.e = Test1().also {
                it.a = "hika2"
                it.b = 4
            }
            it.f = hashMapOf(
                "a" to Test1().also {
                    it.a = "hika10"
                    it.b = 10
                },
                "b" to Test1().also {
                    it.a = "hika11"
                    it.b = 11
                }
            )
        }
        assertEquals(t1.toVOSN()(), "hika1|a|aa|b|bb|c|cc@|3|a|1|b|2|c|3@|hika2|4||a|hika10|10||b|hika11|11|@|")
        val t2 = Test4().fromVOSN("hika1|a|aa|b|bb|c|cc@|3|a|1|b|2|c|3@|hika2|4||a|hika10|10||b|hika11|11|@|")
        assertEquals(t2()?.a, "hika1")
        assertEquals(t2()?.b, hashMapOf("a" to "aa", "b" to "bb", "c" to "cc"))
        assertEquals(t2()?.c, 3)
        assertEquals(t2()?.d, hashMapOf("a" to 1, "b" to 2, "c" to 3))
        assertEquals(t2()?.e?.a, "hika2")
        assertEquals(t2()?.e?.b, 4)
        assertEquals(t2()?.f?.get("a")?.a, "hika10")
        assertEquals(t2()?.f?.get("a")?.b, 10)
        assertEquals(t2()?.f?.get("b")?.a, "hika11")
        assertEquals(t2()?.f?.get("b")?.b, 11)
    }
    class Test5:VO(){
        enum class Enum{A,B,C}
        var a by enum<Enum>()
        var b by enumList<Enum>()
        var c by enumMap<Enum>()
    }
    @Test
    fun test5(){
        val t1 = Test5().also{
            it.a = Test5.Enum.A
            it.b = arrayListOf(Test5.Enum.B, Test5.Enum.C)
            it.c = hashMapOf("a" to Test5.Enum.A, "b" to Test5.Enum.B)
        }
        assertEquals(t1.toVOSN()(), "0|1|2@|a|0|b|1@|")
        val t2 = Test5().fromVOSN("0|1|2@|a|0|b|1@|")
        assertEquals(t2()?.a, Test5.Enum.A)
        assertEquals(t2()?.b, arrayListOf(Test5.Enum.B, Test5.Enum.C))
        assertEquals(t2()?.c, hashMapOf("a" to Test5.Enum.A, "b" to Test5.Enum.B))
    }
    class Test6:VO(){
        sealed class TestUnion:VO(){
            companion object: VOSum<TestUnion>({ A() }, { B() })
            class A: TestUnion(){
                var c by vo(FromVOSN::Test1)
            }
            class B: TestUnion(){
                var d by intList
            }
            var a by string
            var b by int
        }
        var a by sum(TestUnion)
        var b by sumList(TestUnion)
        var c by sumMap(TestUnion)
    }
    @Test
    fun test6(){
        val t1 = Test6().also {
            it.a = Test6.TestUnion.A().also {
                it.a = "a1"
                it.b = 1
                it.c = Test1().also {
                    it.a = "c1"
                    it.b = 2
                }
            }
            it.b = arrayListOf(
                Test6.TestUnion.A().also {
                    it.a = "b1"
                    it.b = 3
                    it.c = Test1().also {
                        it.a = "b2"
                        it.b = 4
                    }
                },
                Test6.TestUnion.B().also {
                    it.a = "b2"
                    it.b = 5
                    it.d = arrayListOf(1,2,3)
                }
            )
            it.c = hashMapOf(
                "a" to Test6.TestUnion.A().also {
                    it.a = "c1"
                    it.b = 6
                    it.c = Test1().also {
                        it.a = "c2"
                        it.b = 7
                    }
                },
                "b" to Test6.TestUnion.B().also {
                    it.a = "c2"
                    it.b = 8
                    it.d = arrayListOf(1,2,3)
                }
            )
        }
        assertEquals(t1.toVOSN()(), "0|a1|1|c1|2|||0|b1|3|b2|4|||1|b2|5|1|2|3@|@|a|0|c1|6|c2|7|||b|1|c2|8|1|2|3@|@|")
        val t2 = Test6().fromVOSN("0|a1|1|c1|2|||0|b1|3|b2|4|||1|b2|5|1|2|3@|@|a|0|c1|6|c2|7|||b|1|c2|8|1|2|3@|@|")
        assertEquals(t2()?.a?.a, "a1")
        assertEquals(t2()?.a?.b, 1)
        assertEquals((t2()?.a as? Test6.TestUnion.A)?.c?.a, "c1")
        assertEquals((t2()?.a as? Test6.TestUnion.A)?.c?.b, 2)
        assertEquals((t2()?.b?.get(0) as? Test6.TestUnion.A)?.a, "b1")
        assertEquals((t2()?.b?.get(0) as? Test6.TestUnion.A)?.b, 3)
        assertEquals((t2()?.b?.get(0) as? Test6.TestUnion.A)?.c?.a, "b2")
        assertEquals((t2()?.b?.get(0) as? Test6.TestUnion.A)?.c?.b, 4)
        assertEquals((t2()?.b?.get(1) as? Test6.TestUnion.B)?.a, "b2")
        assertEquals((t2()?.b?.get(1) as? Test6.TestUnion.B)?.b, 5)
        assertEquals((t2()?.b?.get(1) as? Test6.TestUnion.B)?.d, arrayListOf(1,2,3))
    }
}