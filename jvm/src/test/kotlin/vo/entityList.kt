//package vo
//
//import ein2b.core.entity.eEntity
//import ein2b.core.log.log
//import kotlin.test.Test
//import kotlin.test.assertEquals
//import kotlin.test.assertTrue
//import kotlin.test.fail
//
//class EntityList {
//    class Ent: eEntity() {
//        var list0 by entityList(::Item){default{mutableListOf()}}
//        var list by entityList(::Item)
//
//        class Item(v:String=""): eEntity() {
//            val value by string{default(v)}
//        }
//    }
//
//    @Test
//    fun emptyEntityList() {
//        val t = Ent().also {
//            it.list = mutableListOf(Ent.Item("Test1"),Ent.Item("Test2"))
//        }
//        val s = t.stringifyEin()
//        val t2 = eEntity.parseEin(Ent(),s){
//            log("======report=======")
//            log(it.id ?: "")
//            log(it.message ?: "")
//        } ?: fail("Parse Error")
//        val s2 = t2.stringifyEin()
//        assertEquals(s,s2,"원래 인코딩과 디코딩 후 인코딩한게 다름")
//    }
//
//
//    class Ent2: eEntity() {
//        val sList1 by stringList{default(Immutable(mutableListOf<String>()))}
//        val sList2 by stringList{default(Immutable(mutableListOf<String>("")))}
//        val sList3 by stringList{default(Immutable(mutableListOf<String>("","")))}
//        //val iList1 by intList{default(Immutable(mutableListOf<Int>()))}
//        //val iList2 by intList{default(Immutable(mutableListOf(1,2,3,4,5)))}
//        //var e1List by entityList(::EntEmpty){ default{ mutableListOf(EntEmpty(), EntEmpty()) } }
//        //var e2List by entityList(::EntNone){ default{ mutableListOf(EntNone(), EntNone()) } }
//    }
//    class EntEmpty:eEntity()
//    class EntNone:eEntity(){
//        var none by string{ default("") }
//    }
//    @Test
//    fun entityListWithEmptyEntity() {
//        val t = Ent2()
//        val s = t.stringifyEin()
//        println("[$s]")
//        val t2 = eEntity.parseEin(Ent2(),s){
//            log("======report=======")
//            log(it.id ?: "")
//            log(it.message ?: "")
//        } ?: fail("Parse Error")
//        val s2 = t2.stringifyEin()
//        assertEquals(s,s2,"원래 인코딩과 디코딩 후 인코딩한게 다름")
//    }
//}
//
//// TODO: 문자열이 안 들어있는 문자열 리스트: [@]
//// TODO: 빈 문자열이 하나 들어있는 문자열 리스트: [@]
//// 공백이 하나 들어있는 문자열 리스트: [ @]
//// 빈 문자열이 2개 들어있는 문자열 리스트: [|@]
//// 공백, 빈문자열 리스트: [ |@]
//// 빈문자열, 공백 리스트: [| @]
