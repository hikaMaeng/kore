//package vo
//
//import ein2b.core.entity.Union
//import ein2b.core.entity.eEntity
//import ein2b.core.log.log
//import kotlin.test.Test
//import kotlin.test.assertEquals
//import kotlin.test.fail
//
//class Test8{
//    class Test8Ent1:eEntity(true){
//        var isBoolean by boolean{ default(true) }  // 0
//        var ver by string{ default("") }           // 1
//        var rscver by string{ default("rscver") }  // 2
//        var list1 by stringList{ default{ mutableListOf() } }  // 3
//        var list2 by stringList{ default{ mutableListOf("") } }  // 4
//        var list3 by stringList{ default{ mutableListOf("|||||") } }  // 5
//        var map by stringMap{ default{ hashMapOf("a" to "", "b" to "") } }  // 6
//        var e1List by entityList(::Test8EntEmpty){ default{ mutableListOf(Test8EntEmpty(), Test8EntEmpty()) } } // 7
//        var e2List by entityList(::Test8EntNone){ default{ mutableListOf(Test8EntNone(), Test8EntNone()) } }  // 8
//        var e1Map by entityMap(::Test8EntEmpty){ default{                             // 9
//            mutableMapOf("ea" to Test8EntEmpty(), "eb" to Test8EntEmpty())
//        } }
//        var e2Map by entityMap(::Test8EntNone){ default{                              // 10
//            mutableMapOf("ea" to Test8EntNone(), "eb" to Test8EntNone())
//        } }
//
//        var union1 by union(Test8Ent3)  // 11
//        var union2 by union(Test8Ent3)  // 12
//        var union3 by union(Test8Ent3)  // 13
//        var data by unionMap(Test8Ent3) // 14
//        var sep4 by string{ default("list3") }  // 15
//        var list4 by unionList(Test8Ent3){default{mutableListOf()}} // 16
//        var emptyData by unionMap(Test8Ent3){default{hashMapOf()}}  // 17
//        var emptyList by unionList(Test8Ent3){default{mutableListOf()}}  // 18
//    }
//    class Test8EntEmpty:eEntity()
//    class Test8EntNone:eEntity(){
//        var none by string{ default("") }
//    }
//    sealed class Test8Ent3:eEntity(){
//        companion object:Union<Test8Ent3>(Test8Ent3::Cat, Test8Ent3::Value, Test8Ent3::None)
//        class Cat:Test8Ent3(){
//            var cat by string{ default("cat") }
//        }
//        class Value:Test8Ent3(){
//            var value by string{ default("") }
//        }
//        class None:Test8Ent3()
//    }
//
//    @Test
//    fun union1Field() {
//        val ent1 = Test8Ent1().also {
//            it.union1 = Test8Ent3.Cat()
//            it.union2 = Test8Ent3.Cat()
//            it.union3 = Test8Ent3.None()
//            it.data = mutableMapOf()
//        }
//        val str = ent1.stringifyEin()
//        log(str)
//
//        val ent2 = eEntity.parseEin(Test8Ent1(), str) {
//            log("======report=======")
//            log(it.id ?: "")
//            log(it.message ?: "")
//        } ?: fail("Parse Error")
//
//        val str2 = ent2.stringifyEin()
//        assertEquals(str, str2, "인코딩과 디코딩 후 인코딩한 결과가 같지 않음")
//    }
//
//    @Test
//    fun union1Field1() {
//        val ent1 = Test8Ent1().also {
//            it.union1 = Test8Ent3.None()
//            it.union2 = Test8Ent3.None()
//            it.union3 = Test8Ent3.None()
//            it.data = mutableMapOf()
//        }
//        val str = ent1.stringifyEin()
//        log(str)
//
//        val ent2 = eEntity.parseEin(Test8Ent1(), str) {
//            log("======report=======")
//            log(it.id ?: "")
//            log(it.message ?: "")
//        } ?: fail("Parse Error")
//
//        val str2 = ent2.stringifyEin()
//        assertEquals(str, str2, "인코딩과 디코딩 후 인코딩한 결과가 같지 않음")
//    }
//
//    //@Test
//    fun create(){
//        val ent1 = Test8Ent1().also{
//            it.union1 = Test8Ent3.Cat()
//            it.union2 = Test8Ent3.Value()
//            it.union3 = Test8Ent3.None()
//            it.data = mutableMapOf(
//                "c" to Test8Ent3.Cat(),
//                "v" to Test8Ent3.Value(),
//                "n" to Test8Ent3.None()
//            )
//            it.list4 = mutableListOf(
//                Test8Ent3.Cat(),
//                Test8Ent3.Cat().apply{cat = "Cat"},
//                Test8Ent3.Value().apply{value = "value"},
//                Test8Ent3.Cat().apply{cat = ""},
//                Test8Ent3.Value().apply{value = ""},
//                Test8Ent3.None()
//            )
//        }
//
//        val str = ent1.stringifyEin()
//        log(str)
//
//        val ent2 = eEntity.parseEin(Test8Ent1(), str){
//            log("======report=======")
//            log(it.id ?: "")
//            log(it.message ?: "")
//        }?:fail("Parse Error")
//
//        val str2 = ent2.stringifyEin()
//        assertEquals(str,str2,"인코딩과 디코딩 후 인코딩한 결과가 같지 않음")
//
//        /*eEntity.parseEin(EntApiResourceCdata(), str){
//        log("======Test8 parse error=======", it.id ?: "", it.message ?: "")    }*/
//    }
//}