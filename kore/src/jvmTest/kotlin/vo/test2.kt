package vo

import kore.vo.VO
import kore.vo.field.list.intList
import kore.vo.field.map.booleanMap
import kore.vo.field.map.intMap
import kore.vo.field.map.stringMap
import kotlin.test.Test

class Test2{
    class Ent: VO(){
        var a by intMap
        var b by stringMap
        var c by booleanMap
        var d by intList
    }

    fun createEnt() = Ent().also { a->
        a.a = hashMapOf("a" to 0, "b" to 1)
        a.b = hashMapOf("a" to "a", "b" to "b")
        a.c = hashMapOf("a" to true, "b" to false)
        a.d = arrayListOf(1, 2, 3)
    }
    @Test
    fun create(){
        val a = createEnt()

        testLog("a.a[a]", a.a["a"], 0)
        testLog("a.a[b]", a.a["b"], 1)
        testLog("a.b[a]", a.b["a"], "a")
        testLog("a.b[b]", a.b["b"], "b")
        testLog("a.c[a]", a.c["a"], true)
        testLog("a.c[b]", a.c["b"], false)
        testLog("a.d[0]", a.d[0], 1)
        testLog("a.d[1]", a.d[1], 2)
        testLog("a.d[2]", a.d[2], 3)
    }
}