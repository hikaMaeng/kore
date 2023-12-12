//package vo
//
//import ein2b.core.entity.Union
//import ein2b.core.entity.eEntity
//import ein2b.core.log.log
//import kotlin.test.Test
//import kotlin.test.assertTrue
//
//class Test7{
//    class Ent:eEntity(){
//        var a by int
//        var j by string
//        var um by unionMap(EntUnion)
//    }
//    sealed class EntUnion:eEntity(){
//        companion object:Union<EntUnion>(::Type1, ::Type2)
//        class Type1:EntUnion(){
//            var cat by string{ default("cat") }
//        }
//        class Type2:EntUnion(){
//            var value by string{ default("value") }
//        }
//    }
//    //@Test
//    fun create(){
//        val json = """{
//    "a":1,
//    "j":"jjjjj",
//    "um":{
//        "ln":{
//            "cat":"unionMap Type1"
//        },
//        "co":{
//            "value":"unionMap Type2"
//        }
//    }
//}""".trimMargin()
//        eEntity.parse(Ent(), json){
//
//        }?.also{
//            it.um.forEach{ (k,v)->
//                log("k", k)
//                when(v){
//                    is EntUnion.Type1->{
//                        log("v", v.cat)
//                    }
//                    is EntUnion.Type2->{
//                        log("v", v.value)
//                    }
//                    else->{}
//                }
//            }
//        }
//    }
//}