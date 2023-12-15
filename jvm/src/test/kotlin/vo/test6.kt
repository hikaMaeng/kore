package vo

import kore.vo.VO
import kore.vo.field.list.intList
import kore.vo.field.list.stringList
import kore.vo.field.value.boolean
import kore.vo.field.value.int
import kore.vo.field.value.string
import kore.vo.field.voList
import kore.vosn.fromVOSN
import kore.vosn.toVOSN
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class Test6{
    class Ent: VO(){
        var a by string
        var b by boolean
        var b1 by string
        var b2 by int
        var b3 by string
        var b4 by intList
        var b5 by voList(::Ent2)
        var b6 by voList(::Ent3)
        var c by boolean
        //        var c1 by voMap(::Ent3)
        var d by int
    }
    class Ent2: VO(){
        var ent2 by string
        var list by stringList
    }
    class Ent3: VO() {
        var r by int
        var title by string
    }
    class Ent4: VO() {
        var r by int
        var data by string
    }
    fun createEnt() = Ent().also { e1 ->
        e1.a = ""
        e1.b = true
//        e1.b1 = null
        e1.b2 = 1
        e1.b3 = "bbb"
        e1.b4 = mutableListOf(
            41, 42
        )
        e1.b5 = mutableListOf(
            Ent2().also{
                it.ent2 = "b5_1"
                it.list = mutableListOf("b5_11", "b5_12")
            }
        )
        e1.b6 = mutableListOf(
            Ent3().also{
                it.r = 1
                it.title = "111"
            },
            Ent3().also{
                it.r = 2
                it.title = "222"
            }
        )
        e1.c = false
//        e1.c1 = mutableMapOf("r" to 3, "title" to "333")
        e1.d = 99999
    }
    @Test
    fun create(){
        val ent2 = createEnt()

        testLog("a", ent2.a, "")
        testLog("b", ent2.b, true)
        testLog("c", ent2.c, false)
        testLog("d", ent2.d, 99999)

        val s = ent2.toVOSN()

        val ent3 = Ent().fromVOSN(s()!!)

        val s2 =ent3()!!.toVOSN()

        assertEquals(s,s2)
    }
}
