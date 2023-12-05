//package vo
//
//import ein2b.core.entity.eEntity
//import ein2b.core.entity.eSlowEntity
//import ein2b.core.entity.field.StringField.default
//import ein2b.core.entity.field.StringListField.default
//import ein2b.core.entity.field.StringMapField.default
//import ein2b.core.entity.field.UtcField.default
//import entity2c.EnumAutoCompleteCat
//import kotlin.reflect.KClass
//import kotlin.test.Test
//import kotlin.test.assertEquals
//import kotlin.test.fail
//
//
//class EnumInGenericTypeTest {
//
//    class EntTest<DATA: eEntity>(val cls: KClass<DATA>, val factory:()->DATA): eSlowEntity(true){
//        var ver by string{
//            default("1")
//        }
//        /*var member by union(Test5.EntTest5Union){
//            default(Test5.EntTest5.LOGOUT)
//        }*/
//        var data by entity(cls, factory){
//            default(factory)
//        }
//    }
//    class Res: eEntity(){
//        var e by enum<EnumAutoCompleteCat>(){default(EnumAutoCompleteCat.CCM_MANAGER)}
//    }
//
//    class Res2: eEntity() {
//        var i by int
//    }
//
//    @Test
//    fun enumInGeneric() {
//        val entity1 = EntTest(Res::class, ::Res).also {
//            it.data.e = EnumAutoCompleteCat.FORWARDER
//        }
//        val ientity1 = EntTest(Res2::class, ::Res2).also {
//            it.data.i = 10
//        }
//
//        val string1 = entity1.stringifyEin()
//        val istring1 = ientity1.stringifyEin()
//
//        val entity2 = eEntity.parseEin(EntTest(Res::class, ::Res), string1){
//            println("parse:${it.id}:${it.message}")
//        } ?: fail("entity parse error")
//
//        val ientity2 = eEntity.parseEin(EntTest(Res2::class, ::Res2), istring1){
//            println("parse:${it.id}:${it.message}")
//        } ?: fail("ientity parse error")
//
//        val string2 = entity2.stringifyEin()
//        val istring2 = ientity2.stringifyEin()
//
//        assertEquals(string1, string2)
//        assertEquals(EnumAutoCompleteCat.FORWARDER, entity2.data.e)
//        assertEquals(istring1, istring2)
//        assertEquals(10, ientity2.data.i)
//    }
//}