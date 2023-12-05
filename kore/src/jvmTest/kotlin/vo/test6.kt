//package vo
//
//import ein2b.core.entity.eEntity
//import ein2b.core.entity.encoder.serializeEin
//import ein2b.core.log.log
//import kotlin.test.Test
//import kotlin.test.assertEquals
//import kotlin.test.fail
//
//class Test6{
//    class Ent: eEntity(){
//        var a by string
//        var b by boolean
//        var c by boolean
//        var d by int
//    }
//    @Test
//    fun create(){
//        val json = """
//{
//    "a": "",
//    "b": true,
//    "b1": null,
//    "b2": 1,
//    "b3": "bbb",
//    "b4": [
//        41,
//        42
//    ],
//    "b5": [
//        "b5_1",
//        "b5_2",
//        [
//            "b5_11",
//            "b5_12"
//        ]
//    ],
//    "b6": [
//        {
//            "r": 1,
//            "title": "111"
//        },
//        {
//            "r": 2,
//            "title": "222"
//        }
//    ],
//    "c": false,
//    "c1": {
//        "r": 3,
//        "title": "333"
//    },
//    "c2": {
//        "r":[4, 5],
//        "data":{"r": 6, "title":"666"}
//    },
//    "c3": {
//        "r":[4, 5],
//        "data":{
//            "r": 6,
//            "data":[88, 99]
//        }
//    },
//    "d": 99999
//}""".trimIndent()
//
//        val ent2 = eEntity.parse(Ent(), json){
//            println("eEntity.parse error:${it.id}:${it.message}")
//        } ?: fail("Json Parse Error")
//
//        testLog("a", ent2.a, "")
//        testLog("b", ent2.b, true)
//        testLog("c", ent2.c, false)
//        testLog("d", ent2.d, 99999)
//
//        val s = ent2.serializeEin{
//                log("error:${it.id}:${it.message}")
//            } ?: fail("Ein SerializeEin Error")
//
//        val ent3 = eEntity.parseEin(Ent(), s){
//            log("======report=======")
//            log(it.id ?: "")
//            log(it.message ?: "")
//        } ?: fail("Ein Decoding Error")
//
//        val s2 = ent3.serializeEin{
//            log("error:${it.id}:${it.message}")
//        } ?: fail("Ein SerializeEin Error2")
//
//        assertEquals(s,s2)
//    }
//}