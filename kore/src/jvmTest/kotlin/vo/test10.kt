//package vo
//
//import ein2b.core.date.eUtc
//import ein2b.core.entity.eEntity
//import kotlin.test.Test
//import kotlin.test.assertEquals
//import kotlin.test.fail
//
//class Test10 {
//
//    class ent: eEntity() {
//        var time by utc
//    }
//
//    @Test
//    fun test() {
//        val entity = ent().also {
//            it.time = eUtc.of("2020-01-01T01:01:00Z") ?:
//                  throw IllegalStateException("eUTC string parseing error")
//        }
//
//        val js = entity.stringify()
//        val es = entity.stringifyEin()
//
//        val entity2 = eEntity.parse(ent(),js) {
//            println(it.message)
//            println(it.id)
//        } ?: fail("entity2(JSON) parse error")
//
//        val s2 = entity.stringify()
//
//        val entity3 = eEntity.parseEin(ent(),es) {
//            println(it.message)
//            println(it.id)
//        } ?: fail("entity2(JSON) parse error")
//
//        val s3 = entity3.stringifyEin()
//
//        assertEquals(js,s2)
//        assertEquals(es,s3)
//    }
//}