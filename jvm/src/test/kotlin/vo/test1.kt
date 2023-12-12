package vo

import kore.vo.VO
import kore.vo.field.value.*
import kore.vosn.toVOSN
import kotlin.test.Test
import kotlin.test.assertEquals

class Test1{
    class Ent: VO(){
        var a by int
        var b by uint
        var c by short
        var d by ushort
        var e by long
        var f by ulong
        var g by float
        var h by double
        var i by boolean
        var j by string
        var k by string
    }

    fun createEnt() = Ent().also{t->
        t.a = 1
        t.b = 2u
        t.c = 3
        t.d = 4u
        t.e = 5L
        t.f = 6UL
        t.g = 7.5f
        t.h = 8.6
        t.i = false
        t.j = "abc"
        t.k = "kkk"
    }

    @Test
    fun create(){
        val t = createEnt()
        testLog("t.a", t.a, 1)
        testLog("t.b", t.b, 2u)
        testLog("t.c", t.c, 3 .toShort())
        testLog("t.d", t.d, 4u .toUShort())
        testLog("t.e", t.e, 5L)
        testLog("t.f", t.f, 6UL)
        testLog("t.g", t.g, 7.5f)
        testLog("t.h", t.h, 8.6)
        testLog("t.i", t.i, false)
        testLog("t.j", t.j, "abc")

        println("stringify:${t.toVOSN()}")
    }

}
fun testLog(msg:String, v1:Any?, v2:Any?){
    val s = "$msg: $v1 == $v2"
    println(s)
    assertEquals(v1, v2, s)
}