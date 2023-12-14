package vo

import kore.vo.VO
import kore.vo.field.list.intList
import kore.vo.field.map.stringMap
import kore.vo.field.value.boolean
import kore.vo.field.value.int
import kore.vo.field.value.string
import kore.vo.field.voList
import kore.vosn.fromVOSN
import kore.vosn.toVOSN
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class Test3{
    class Ent: VO(){
        var a by int
        var b by string
        var c by boolean
        var d by stringMap
        var e by intList
        var f by voList(::Ent2)
    }
    class Ent2: VO(){
        var e by intList
    }
    fun createEnt() = Ent().also { e1 ->
        e1.a = 1
        e1.b = "2"
        e1.c = true
        e1.d = mutableMapOf("d1" to "d1", "d2" to "d2")
        e1.e = mutableListOf(10, 20, 30)
        e1.f = mutableListOf(
            Ent2().also { e2 ->
                e2.e = mutableListOf(100, 101, 102)
            },
            Ent2().also { e2 ->
                e2.e = mutableListOf(200, 201, 202)
            }
        )
    }

    @Test
    fun create(){
        val t = createEnt()
        testLog("t.a", t.a, 1)
        testLog("t.b", t.b, "2")
        testLog("t.c", t.c, true)
        testLog("t.d[d1]", t.d["d1"], "d1")
        testLog("t.d[d2]", t.d["d2"], "d2")
        testLog("t.e[0]", t.e[0], 10)
        testLog("t.e[1]", t.e[1], 20)
        testLog("t.e[2]", t.e[2], 30)
        testLog("t.f[0].e[0]", t.f[0].e[0], 100)
        testLog("t.f[0].e[1]", t.f[0].e[1], 101)
        testLog("t.f[0].e[2]", t.f[0].e[2], 102)
        testLog("t.f[1].e[0]", t.f[1].e[0], 200)
        testLog("t.f[1].e[1]", t.f[1].e[1], 201)
        testLog("t.f[1].e[2]", t.f[1].e[2], 202)
        val s = t.toVOSN()
        val t2 = Ent().fromVOSN(s()!!)
        val s2 = t2()!!.toVOSN()
        assertEquals(s,s2,"원래 인코딩과 디코드 후 인코딩한 인코딩이 다름")
    }

}
