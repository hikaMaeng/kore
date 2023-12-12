//package vo
//
//import entity2c.EnumAutoCompleteCat
//import ein2b.core.entity.Union
//import ein2b.core.entity.eEntity
//import ein2b.core.entity.eSlowEntity
//import ein2b.core.entity.encoder.serializeEin
//import ein2b.core.log.log
//import kotlin.reflect.KClass
//import kotlin.test.Test
//import kotlin.test.assertEquals
//import kotlin.test.assertNotEquals
//import kotlin.test.fail
//
//class Test5{
//    sealed class EntTest5Union:eEntity(true){
//        companion object:Union<EntTest5Union>(EntTest5Union::Login, EntTest5Union::Logout)
//        class Login:EntTest5Union(){
//            var memberRowid:Long = 0L
//            var userId by string //userId
//            var data by stringMap{ default(defaultStringMap) }
//        }
//        class Logout:EntTest5Union()
//    }
//    class EntTest5<DATA:eEntity>(val cls: KClass<DATA>, val factory:()->DATA): eSlowEntity(true){
//        companion object{
//            val LOGOUT = Immutable<EntTest5Union>(EntTest5Union.Logout())
//        }
//        var ver by string{
//            default("1")
//        }
//        var member by union(EntTest5Union){
//            default(LOGOUT)
//        }
//        var menuCount by stringMap{ default(defaultStringMap) }
//        var error by entity(::Ent){ encoding.isOptional() }
//        var list by stringList{ default(defaultStringList) }
//        var data by entity(cls, factory){
//            default(factory)
//            encoding.setResolver{ !isError }
//        }
//        val isError get() = props["error"] != null
//        //val isLogin get() = member is EntTest5Union.Login
//    }
//    class Res:eEntity(){
//        var r by string
//        var e by enum<EnumAutoCompleteCat>(){default(EnumAutoCompleteCat.FORWARDER)}
//        var u by utc{ default(defaultUtc) }
//    }
//    class Ent:eEntity(){
//        var a by string{ default("a") }
//        var b by string{ default("b") }
//    }
//    class Ent3:eEntity(){
//        var c by string{ default("c") }
//        var d by string{ default("d") }
//    }
//
//    class Ent2:eEntity(){
//        var a by string
//        var b by string{ encoding.isOptional() }
//        var c by string{
//            default("c")
//            encoding.isExcluded()
//        }
//        var d by string{
//            default("d")
//            encoding.setResolver{ false }
//        }
//        val isOptional get() = props["b"] != null
//    }
//
//    class EntInts(aa:Int,bb:Int): eEntity() {
//        val a by int{default(aa)}
//        val b by int{default(bb)}
//    }
//
//    /*
//    @Test
//    fun defaultInts() {
//        val e1 = EntInts(1,2)
//        val e2 = EntInts(3,4)
//
//        assertEquals(e1.a,1)
//        assertEquals(e1.b,2)
//        assertEquals(e2.a,3)
//        assertEquals(e2.b,4)
//    }
//
//    @Test
//    fun create(){
//        println("=====test5000=========")
//
//        val response1 = EntTest5(Ent::class){ Ent() }
//        val response2 = EntTest5(Ent3::class){ Ent3() }
//
//        println(response1.data)
//        println(response2.data)
//
//        assertNotEquals(response1.data::class.qualifiedName, response2.data::class.qualifiedName)
//        assertNotEquals(response1.data.a, response2.data.c)
//        assertNotEquals(response1.data.b, response2.data.d)
//
//        println("response1:${response1.stringify()}")
//        println("response1:${response2.stringify()}")
//
//
//
//
//        /*
//        val response = EntM42ApiResponse(Ent::class){ Ent() }
////        response.member = EntM42ApiResMember.Login().also{
////            it.userId = "hyej"
////        }
////        response.error = EntM42ApiResponse.Error("test").also{
////            it.id = "T001"
////        }
//        response.data.a = "111"
//
//        val body = response.stringify()
//        println("response:${body}")
//
//        eEntity.parse(EntM42ApiResponse(Ent::class){ Ent() }, body){
//            println("parse:${it.message}")
//        }?.also{ res->
//            testLog("response.ver", res.ver, response.ver)
//        }*/
//
//        /*val response = EntTest5(Res::class){ Res() }
//        *//*response.error = Ent().also{
//            it.a = "aaaa"
//            it.b = "bbb"
//        }*//*
//        response.data.r = "10"
//        response.data.e = EnumAutoCompleteCat.CCM_MANAGER
//        response.list = mutableListOf("100","200","300")
//
//        val body = response.stringify()
//        println("response1:${body}")
//
//        eEntity.parse(EntTest5(Res::class){ Res() }, body){
//            println("parse:${it.id}:${it.message}")
//        }?.also{ res->
//            res.serializeEin{
//                log("serializeString error:${it.id}:${it.message}")
//            }?.also{ serial->
//                println("serialString1:${serial}")
//
//                eEntity.parseEin(EntTest5(Res::class){ Res() }, serial){
//                    log("unserializeString error:${it.id}:${it.message}")
//                }?.also{ res2->
//                    //println("res2.data.u.toString():${res2.data.u}")
//
//                    println("serialString2:${res2.stringifyEin()}")
//                    testLog("isError", res2.isError, false)
//                }
//            }
//        }*/
//
//
////        val a = Ent()
////        println("stringify:${a.stringify()}")
////
////        val a = Ent2()
////        a.a = "a"
////        a.b = "b"
////        println("stringify:${a.stringify()}")
////        println("isOptional:${a.isOptional}")
//    }*/
//
//    @Test
//    fun encodeDecode() {
//        run {
//            // test with data
//            val response = EntTest5(Res::class) { Res() }
//            response.data.r = "10"
//            response.data.e = EnumAutoCompleteCat.CCM_MANAGER
//            response.list = mutableListOf("100", "200", "300")
//
//            val body = response.stringify()
//            println("response: ${body}")
//
//            val e = eEntity.parse(EntTest5(Res::class) { Res() }, body) {
//                println("parse:${it.id}:${it.message}")
//            } ?: fail("ParseError")
//
//            val body2 = e.stringify()
//
//            assertEquals(body, body2)
//        }
//    }
//
//    @Test
//    fun encodeDecode2() {
//        run {
//            // test with data
//            val response = EntTest5(Res::class) { Res() }
//            response.data.r = "10"
//            response.data.e = EnumAutoCompleteCat.CCM_MANAGER
//            response.list = mutableListOf("100", "200", "300")
//
//            val body = response.stringify()
//            println("response: ${body}")
//
//            val e = eEntity.parse(EntTest5(Res::class) { Res() }, body) {
//                println("parse:${it.id}:${it.message}")
//            } ?: fail("ParseError")
//
//            val body2 = e.stringify()
//
//            assertEquals(body, body2)
//        }
//    }
//
//    @Test
//    fun encodeDecodeEin() {
//        run {
//            // test with data
//            val response = EntTest5(Res::class) { Res() }
//            response.data.r = "10"
//            response.data.e = EnumAutoCompleteCat.CCM_MANAGER
//            response.list = mutableListOf("100", "200", "300")
//
//            val body = response.stringifyEin()
//            println("response: ${body}")
//
//            val e = eEntity.parseEin(EntTest5(Res::class) { Res() }, body) {
//                println("parse:${it.id}:${it.message}")
//            } ?: fail("ParseError")
//
//            val body2 = e.stringifyEin()
//
//            assertEquals(body, body2)
//        }
//    }
//
//    @Test
//    fun encodeDecodeEin2() {
//        run {
//            // test with data
//            val response = EntTest5(Res::class) { Res() }
//            response.data.r = "10"
//            response.data.e = EnumAutoCompleteCat.CCM_MANAGER
//            response.list = mutableListOf("100", "200", "300")
//
//            val body = response.stringifyEin()
//            println("response: ${body}")
//
//            val e = eEntity.parseEin(EntTest5(Res::class) { Res() }, body) {
//                println("parse:${it.id}:${it.message}")
//            } ?: fail("ParseError")
//
//            val body2 = e.stringifyEin()
//
//            assertEquals(body, body2)
//        }
//    }
//
//    class mapEntity: eEntity() {
//        val map by intMap{default(Immutable(hashMapOf()))}
//    }
//
//    @Test
//    fun noInitMap() {
//        val ent = mapEntity()
//        ent.map["test"] = 1
//        ent.map["test2"] = 2
//
//        assertEquals(ent.map["test"],1)
//        assertEquals(ent.map["test2"],2)
//    }
//}