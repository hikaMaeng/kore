//package vo
//
//import UnionTest.EntTest5Union.Companion.LOGOUT
//import ein2b.core.entity.Union
//import ein2b.core.entity.eEntity
//import ein2b.core.entity.eSlowEntity
//import ein2b.core.log.log
//import entity2c.EnumAutoCompleteCat
//import kotlin.reflect.KClass
//import kotlin.test.Test
//import kotlin.test.assertEquals
//import kotlin.test.assertTrue
//import kotlin.test.fail
//
//class UnionTest{
//    sealed class EntTest5Union:eEntity(true){
//        companion object: Union<EntTest5Union>(EntTest5Union::Login, EntTest5Union::Logout) {
//            val LOGOUT = Immutable<EntTest5Union>(Logout())
//        }
//        class Login:EntTest5Union(){
//            var memberRowid:Long = 0L
//            var userId by string //userId
//            var data by stringMap{ default(defaultStringMap) }
//        }
//        class Logout:EntTest5Union()
//    }
//
//    class UnionOnlyTest: eEntity() {
//        var member by union(EntTest5Union){
//            default(LOGOUT)
//        }
//    }
//
//    class EntTest<DATA:eEntity>(val cls: KClass<DATA>, val factory:()->DATA): eSlowEntity(true){
//        companion object
//        var member by union(EntTest5Union){
//            default(LOGOUT)
//        }
//        var data by entity(cls, factory)
//    }
//    class Res:eEntity(){
//        var r by string
//        var e by enum<EnumAutoCompleteCat>(){default(EnumAutoCompleteCat.FORWARDER)}
//        var u by utc{ default(defaultUtc) }
//        var login2 by union(EntTest5Union){
//            default(LOGOUT)
//        }
//    }
//
//    @Test
//    fun testUnion() {
//        val entity1 = EntTest(Res::class, ::Res).also {
//            it.data = Res().also {
//                it.r = "Hello World"
//                it.e = EnumAutoCompleteCat.CCM_MANAGER
//            }
//        }
//
//        val string1 = entity1.stringify()
//
//        println("string1 = ${string1}")
//
//        val entity2 = eEntity.parse(EntTest(Res::class,::Res), string1) {
//            println(it)
//        } ?: fail("entity2 parse error")
//
//        val string2 = entity2.stringify()
//
//        assertEquals("Hello World", entity1.data.r)
//        assertEquals("Hello World", entity2.data.r)
//        assertEquals(string2, string1)
//    }
//
//    @Test
//    fun testUnionEin() {
//        val entity1 = EntTest(Res::class, ::Res).also {
//            it.data = Res().also {
//                it.r = "Hello World"
//                it.e = EnumAutoCompleteCat.CCM_MANAGER
//            }
//        }
//
//        val string1 = entity1.stringifyEin()
//
//        println("string1 = ${string1}")
//
//        val entity2 = eEntity.parseEin(EntTest(Res::class,::Res), string1) {
//            println(it)
//        } ?: fail("entity2 parse error")
//
//        val string2 = entity2.stringifyEin()
//
//        assertEquals("Hello World", entity1.data.r)
//        assertEquals("Hello World", entity2.data.r)
//        assertEquals(string2, string1)
//    }
//
//    @Test
//    fun testUnionOnlyEin() {
//        val entity1 = UnionOnlyTest()
//        val string1 = entity1.stringifyEin()
//
//        println("string1 = ${string1}")
//
//        val entity2 = eEntity.parseEin(UnionOnlyTest(), string1) {
//            println(it)
//        } ?: fail("entity2 parse error")
//
//        val string2 = entity2.stringifyEin()
//
//        assertEquals<KClass<*>>(EntTest5Union.Logout::class,entity2.member::class)
//        assertEquals(string2, string1)
//    }
//}