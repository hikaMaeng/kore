//package vo
//
//import ein2b.core.entity.eEntity
//import ein2b.core.log.log
//import kotlin.test.Test
//import kotlin.test.assertEquals
//import kotlin.test.assertTrue
//import kotlin.test.fail
//
//class EntityWithEmptyString {
//    class Ent: eEntity(){
//        var a by string
//        var b by string
//        var c by string
//        var d by string
//    }
//
//    fun createEnt() = Ent().also{t->
//        t.a = ""
//        t.b = "non empty"
//        t.c = "non empty||"
//        t.d = "empty||"
//    }
//
//    class Ent2: eEntity() {
//        var a by string
//    }
//
//    class Ent3: eEntity(){
//        var a by string
//        var b by boolean
//    }
//
//    @Test
//    fun entityWithAString() {
//        run {
//            val e1 = Ent2().also { it.a = "" }
//            val s1 = e1.stringifyEin()
//            val e2 = eEntity.parseEin(Ent2(),s1) {
//                log("======report=======")
//                log(it.id ?: "")
//                log(it.message ?: "")
//            } ?: fail("Parse Error")
//            val s2 = e1.stringifyEin()
//
//            assertEquals(s1,s2,"원래 인코딩과 디코딩 후 인코딩한게 다름")
//        }
//        run {
//            val e1 = Ent2().also { it.a = "||||||||||" }
//            val s1 = e1.stringifyEin()
//            val e2 = eEntity.parseEin(Ent2(),s1) {
//                log("======report=======")
//                log(it.id ?: "")
//                log(it.message ?: "")
//            } ?: fail("Parse Error")
//            val s2 = e1.stringifyEin()
//
//            assertEquals(s1,s2,"원래 인코딩과 디코딩 후 인코딩한게 다름")
//        }
//        run {
//            val e1 = Ent2().also { it.a = """|| || | | \|\|""" }
//            val s1 = e1.stringifyEin()
//            val e2 = eEntity.parseEin(Ent2(),s1) {
//                log("======report=======")
//                log(it.id ?: "")
//                log(it.message ?: "")
//            } ?: fail("Parse Error")
//            val s2 = e1.stringifyEin()
//
//            assertEquals(s1,s2,"원래 인코딩과 디코딩 후 인코딩한게 다름")
//        }
//
//
//        run {
//            val e1 = Ent3().also { it.a = ""; it.b = true }
//            val s1 = e1.stringifyEin()
//            val e2 = eEntity.parseEin(Ent3(),s1) {
//                log("======report=======")
//                log(it.id ?: "")
//                log(it.message ?: "")
//            } ?: fail("Parse Error")
//            val s2 = e1.stringifyEin()
//
//            assertEquals(s1,s2,"원래 인코딩과 디코딩 후 인코딩한게 다름")
//        }
//    }
//
//    @Test
//    fun encodeDecode() {
//        val t = createEnt()
//        val s = t.stringifyEin()
//        val t2 = eEntity.parseEin(Ent(),s){
//            log("======report=======")
//            log(it.id ?: "")
//            log(it.message ?: "")
//        } ?: fail("Parse Error")
//        val s2 = t2.stringifyEin()
//        assertEquals(s,s2,"원래 인코딩과 디코딩 후 인코딩한게 다름")
//    }
//}