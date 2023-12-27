package vo

import kore.vo.VO
import kore.vo.VOSum
import kore.vo.field.*
import kore.vo.field.enum
import kore.vo.field.enumList
import kore.vo.field.enumMap
import kore.vo.field.list.doubleList
import kore.vo.field.map.stringMap
import kore.vo.field.value.boolean
import kore.vo.field.value.double
import kore.vo.field.value.int
import kore.vo.field.value.string
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class VOTest2 {
    enum class Enum1{
        A,B,C
    }
    class Test1:VO(){
        class Sub1:VO()
        sealed class Sum1:VO(){
            class V1:Sum1()
            class V2:Sum1()
            class V3:Sum1()
            companion object:VOSum<Sum1>(::V1, ::V2, ::V3)
        }
        var a1 by int{
            default(3)
        }
        var a2 by int
        var a3 by int(10)
        var b1 by doubleList
        var b2 by doubleList(1.0,2.0,3.0)
        var b3 by doubleList(arrayListOf(1.0,2.0,3.0))

        var c1 by stringMap
        var c2 by stringMap("a" to "1","b" to "2")
        var c3 by stringMap(hashMapOf("a" to "1", "b" to "2"))

        var d1 by enum<Enum1>()
        var d2 by enumList<Enum1>(Enum1.A, Enum1.B, Enum1.C)
        var d3 by enumList<Enum1>(arrayListOf(Enum1.A, Enum1.B, Enum1.C) )
        var d4 by enumMap<Enum1>("a" to Enum1.A, "b" to Enum1.B)
        var d5 by enumMap<Enum1>(hashMapOf("a" to Enum1.A, "b" to Enum1.B))

        var e1 by vo(::Sub1)
        var e2 by voListDefault(::Sub1){arrayListOf(Sub1(), Sub1())}
        var e3 by voMapDefault(::Sub1){hashMapOf("a" to Sub1(), "b" to Sub1())}
        var f1 by sum(Sum1)
        var f2 by sumListDefault(Sum1){arrayListOf(Sum1.V1(), Sum1.V2())}
        var f3 by sumMapDefault(Sum1){hashMapOf("a" to Sum1.V1(), "b" to Sum1.V2())}

    }
    @Test
    fun test1(){
        val vo1 = Test1().also {
            it.a1 = 5
            it.a2 = 7
        }
        val vo2 = Test1()
        assertEquals(vo1.a1, 5)
        assertEquals(vo1.a2, 7)
        assertEquals(vo1.a3, 10)
        assertFails { vo1.b1 }
        assertEquals(vo1.b2, arrayListOf(1.0,2.0,3.0))
        assertEquals(vo1.b3, arrayListOf(1.0,2.0,3.0))
        assertFails { vo1.c1 }
        assertEquals(vo1.c2, hashMapOf("a" to "1", "b" to "2"))
        assertEquals(vo1.c3, hashMapOf("a" to "1", "b" to "2"))
        assertFails { vo1.d1 }
        assertEquals(vo1.d2, arrayListOf(Enum1.A, Enum1.B, Enum1.C))
        assertEquals(vo1.d3, arrayListOf(Enum1.A, Enum1.B, Enum1.C))
        assertEquals(vo1.d4, hashMapOf("a" to Enum1.A, "b" to Enum1.B))
        assertEquals(vo1.d5, hashMapOf("a" to Enum1.A, "b" to Enum1.B))
        assertFails { vo1.e1 }
        assertEquals(vo1.e2.size, 2)
        assertEquals(vo1.e3.size, 2)
        assertFails { vo1.f1 }
        assertEquals(vo1.f2.size, 2)
        assertEquals(vo1.f3.size, 2)
        println(vo1)
        assertEquals(vo2.a1, 3)
        assertFails { vo2.a2 }
        assertEquals(vo2.a3, 10)
    }
    class Test2:VO(){
        var a by int
        var b by string
        var c by boolean
    }
    @Test
    fun test2(){
        println("------------------")
        println("${Test2::class.toString()},...${Test2()::class.toString()}")
        assertEquals(VO.keys(Test2::class), arrayListOf("a", "b", "c"))
    }
    sealed class Test3Sum:VO(){
        companion object:VOSum<Test3Sum>(::Sub1, ::Sub2, ::Sub3)
        var a by int(3)
        class Sub1:Test3Sum(){
            var b by string("sub1")
        }
        class Sub2:Test3Sum(){
            var c by double(10.5)
        }
        class Sub3:Test3Sum(){
            var d by boolean(true)
        }
    }
    class Test3:VO(){
        var a by sumList(Test3Sum)
    }
    @Test
    fun test3(){
        val vo1 = Test3()
        vo1.a = arrayListOf(
            Test3Sum.Sub1().also {
                it.a = 3
            },
            Test3Sum.Sub2().also {
                it.a = 10
            },
            Test3Sum.Sub3().also {
                it.a = 20
            }
        )

    }
}