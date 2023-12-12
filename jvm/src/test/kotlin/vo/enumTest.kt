//package vo
//
//import entity2c.EnumAutoCompleteCat
//import ein2b.core.entity.Union
//import ein2b.core.entity.eEntity
//import ein2b.core.entity.encoder.serializeEin
//import ein2b.core.log.log
//import kotlin.reflect.KClass
//import kotlin.test.Test
//import kotlin.test.assertEquals
//import kotlin.test.assertNotEquals
//import kotlin.test.fail
//
//class EnumTest {
//    class EnumOnly:eEntity() {
//        var e by enum<EnumAutoCompleteCat>()
//    }
//    class EnumListOnly:eEntity() {
//        var el by enumList<EnumAutoCompleteCat>()
//    }
//
//    class EnumMapOnly:eEntity() {
//        var em by enumMap<EnumAutoCompleteCat>()
//    }
//
//    @Test
//    fun encodeDecodeEin() {
//        val entity1 = EnumOnly().also {
//            it.e = EnumAutoCompleteCat.CCM_MANAGER
//        }
//
//        val body1 = entity1.stringifyEin()
//
//        val entity2 = eEntity.parseEin(EnumOnly(), body1) {
//            println("parse:${it.id}:${it.message}")
//        } ?: fail("ParseError")
//
//        assertEquals(EnumAutoCompleteCat.CCM_MANAGER, entity2.e)
//
//        val body2 = entity2.stringifyEin()
//
//        assertEquals(body1, body2)
//    }
//
//    @Test
//    fun encodeDecodeEin2() {
//        val entity1 = EnumListOnly().also {
//            it.el = mutableListOf(EnumAutoCompleteCat.CCM_MANAGER, EnumAutoCompleteCat.FORWARDER)
//        }
//
//        val body1 = entity1.stringifyEin()
//
//        val entity2 = eEntity.parseEin(EnumListOnly(), body1) {
//            println("parse:${it.id}:${it.message}")
//        } ?: fail("ParseError")
//
//        assertEquals(EnumAutoCompleteCat.CCM_MANAGER, entity2.el[0])
//        assertEquals(EnumAutoCompleteCat.FORWARDER, entity2.el[1])
//
//        val body2 = entity2.stringifyEin()
//
//        assertEquals(body1, body2)
//    }
//
//    @Test
//    fun encodeDecodeEin3() {
//        val entity1 = EnumMapOnly().also {
//            it.em = hashMapOf("a" to EnumAutoCompleteCat.CCM_MANAGER, "b" to EnumAutoCompleteCat.FORWARDER)
//        }
//
//        val body1 = entity1.stringifyEin()
//
//        val entity2 = eEntity.parseEin(EnumMapOnly(), body1) {
//            println("parse:${it.id}:${it.message}")
//        } ?: fail("ParseError")
//
//        assertEquals(EnumAutoCompleteCat.CCM_MANAGER, entity2.em["a"])
//        assertEquals(EnumAutoCompleteCat.FORWARDER, entity2.em["b"])
//
//        val body2 = entity2.stringifyEin()
//
//        assertEquals(body1, body2)
//    }
//}