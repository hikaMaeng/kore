package vo

import kore.vo.VO
import kore.vo.field.list.intList
import kore.vo.field.map.stringMap
import kore.vo.field.value.boolean
import kore.vo.field.value.int
import kore.vo.field.value.string
import kore.vo.field.voList
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
    @Test
    fun create(){
//    val t = eEntity.parse(Ent(), """
//    {
//    "a" : 1,
//  "b" : "2",
//  "c" : true,
//  "d": {
//    "d1": "d1",
//    "d2": "d2"
//  } ,
//  "e": [
//    10,
//    20,
//    30
//  ],
//  "f":[
//    {
//        "e" : [
//            100,
//            101,
//            102
//        ]
//    },
//    {"e":[200,201,202]}
//  ]
//    }""".trimIndent())?.also{ a->
//            log("======================================")
//            testLog("a.a", a.a, 1)
//            testLog("a.b", a.b, "2")
//            testLog("a.c", a.c, true)
//            testLog("a.d[d1]", a.d["d1"], "d1")
//            testLog("a.d[d2]", a.d["d2"], "d2")
//            testLog("a.e[0]", a.e[0], 10)
//            testLog("a.e[1]", a.e[1], 20)
//            testLog("a.e[2]", a.e[2], 30)
//            testLog("a.f[0].e[0]", a.f[0].e[0], 100)
//            testLog("a.f[0].e[1]", a.f[0].e[1], 101)
//            testLog("a.f[0].e[2]", a.f[0].e[2], 102)
//            testLog("a.f[1].e[0]", a.f[1].e[0], 200)
//            testLog("a.f[1].e[1]", a.f[1].e[1], 201)
//            testLog("a.f[1].e[2]", a.f[1].e[2], 202)
//        } ?: fail("Cannot parse json")
//        val s = t.stringifyEin()
//        val t2 = eEntity.parseEin(Ent(),s){
//            log("======report=======")
//            log(it.id ?: "")
//            log(it.message ?: "")
//        } ?: fail("Parse Error")
//        val s2 = t2.stringifyEin()
//        assertEquals(s,s2,"원래 인코딩과 디코드 후 인코딩한 인코딩이 다름")

    }
}