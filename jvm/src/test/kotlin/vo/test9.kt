//package vo
//
//import ein2b.core.entity.Union
//import ein2b.core.entity.eEntity
//import ein2b.core.log.log
//import kotlin.test.Test
//import kotlin.test.assertEquals
//import kotlin.test.fail
//
//class Test9{
//    class Test9Ent1:eEntity(true){
//        var list by entityList(::Test9Ent2)
//    }
//    class Test9Ent2:eEntity() {
//        var list by stringList
//    }
//
//    @Test
//    fun listOfList() {
//        val ent1 = Test9Ent1().also {
//            it.list = mutableListOf(
//                Test9Ent2().also{ l2 ->
//                    l2.list = mutableListOf()
//                },
//                Test9Ent2().also{ l2 ->
//                    l2.list = mutableListOf("")
//                },
//                Test9Ent2().also{ l2 ->
//                    l2.list = mutableListOf("","")
//                }
//            )
//        }
//        val str = ent1.stringifyEin()
//        log(str)
//
//        val ent2 = eEntity.parseEin(Test9Ent1(), str) {
//            log("======report=======")
//            log(it.id ?: "")
//            log(it.message ?: "")
//        } ?: fail("Parse Error")
//
//        val str2 = ent2.stringifyEin()
//        assertEquals(str, str2, "인코딩과 디코딩 후 인코딩한 결과가 같지 않음")
//    }
//}