package vo

import kore.vo.VO
import kore.vo.VOSum
import kore.vosn.toVOSN
import kore.vo.field.*
import kore.vo.field.list.intList
import kore.vo.field.list.stringList
import kore.vo.field.map.intMap
import kore.vo.field.map.stringMap
import kore.vo.field.value.int
import kore.vo.field.value.string
import kotlin.test.Test
import kotlin.test.assertEquals
class ToVOSN {
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
    }
    class Test2:VO(){
        var a by string
        var b by int
        var c by vo(::Test1)//vo(TestKoreEncode1::Test1)
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
    }
    class Test3:VO(){
        var a by string
        var b by stringList
        var c by int
        var d by intList
        var e by vo(ToVOSN::Test1)
        var f by voList(ToVOSN::Test1)
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
    }
    class Test4:VO(){
        var a by string
        var b by stringMap
        var c by int
        var d by intMap
        var e by vo(ToVOSN::Test1)
        var f by voMap(ToVOSN::Test1)
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
    }
    class Test6:VO(){
        sealed class TestUnion:VO(){
            companion object: VOSum<TestUnion>(TestUnion::A, TestUnion::B)
            class A: TestUnion(){
                var c by vo(ToVOSN::Test1)
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
    }
}