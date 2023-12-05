//package vo
//
//import ein2b.core.entity.eEntity
//import ein2b.core.log.log
//import kotlin.test.Test
//import kotlin.test.assertEquals
//import kotlin.test.assertTrue
//import kotlin.test.fail
//
//class EntityWithEmptyStringList {
//    class Ent1: eEntity(){
//        var a by stringList
//        var b by stringList
//    }
//
//    fun createEnt(vararg s:String) = Ent1().also{t->
//        t.a = mutableListOf(*s)
//        t.b = mutableListOf(*s)
//    }
//
//    @Test
//    fun test() {
//        run {
//            val e1 = createEnt("","")
//            val s1 = e1.stringifyEin()
//            val e2 = eEntity.parseEin(Ent1(),s1) {
//                log("======report=======")
//                log(it.id ?: "")
//                log(it.message ?: "")
//            } ?: fail("Parse Error")
//            val s2 = e1.stringifyEin()
//
//            assertEquals(s1,s2,"원래 인코딩과 디코딩 후 인코딩한게 다름")
//        }
//        run {
//            val e1 = createEnt("1","","2")
//            val s1 = e1.stringifyEin()
//            val e2 = eEntity.parseEin(Ent1(),s1) {
//                log("======report=======")
//                log(it.id ?: "")
//                log(it.message ?: "")
//            } ?: fail("Parse Error")
//            val s2 = e1.stringifyEin()
//
//            assertEquals(s1,s2,"원래 인코딩과 디코딩 후 인코딩한게 다름")
//        }
//        run {
//            val e1 = createEnt("","1","2")
//            val s1 = e1.stringifyEin()
//            val e2 = eEntity.parseEin(Ent1(),s1) {
//                log("======report=======")
//                log(it.id ?: "")
//                log(it.message ?: "")
//            } ?: fail("Parse Error")
//            val s2 = e1.stringifyEin()
//
//            assertEquals(s1,s2,"원래 인코딩과 디코딩 후 인코딩한게 다름")
//        }
//    }
//}