package vo.json

import kore.vjson.JSON
import kore.vo.VO
import kore.vo.field.list.*
import kore.vo.field.value.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class test1 {
    class Test1:VO(){
        var a by int
        var b by int

    }
    @Test
    fun test1(){
//        runBlocking{
//            VOJson.to(Test1().also {it.a = 1})
//                .onCompletion {
//                    println("complete")
//                }
//                .collect{
//                    println(it)
//                }
//            VOJson.from(Test1(), flow{
//                emit("""{"a"""")
//                emit(""":1, """")
//                emit("""b":12""")
//                emit("""3}""")
//            })
//            .buffer()
//            .collect{
//                println("------------------------------------")
//                println("${it.a}, ${try{it.b}catch(_:Throwable){null}}")
//                //내가 원하는게 a였으면 a가 확보되는 순간 collect를 멈추고 싶다.
//                //a값을 이용한 처리 발동 하다보면 여기가 늦어질 수도 있으니
//                coroutineContext.cancel()
//                //1. 컨버터의 타입기반의 정렬
//                //2. 최종 vo의 키에 값이 들어올때마다 emit
//                //3. 클라이언트가 원할때 flow의 collect를 멈춘다.
//            }
//        }

    }
    @Test
    fun test2(){
        runBlocking{
            var isContinue = true
            JSON.from(Test1(), flow{
                emit("""{"a"""")
                emit(""":1, """")
                emit("""b":12""")
                emit("""3}""")
            }).takeWhile { isContinue }
                .collect{
                    println("------------------------------------")
                    println("${it.a}, ${try{it.b}catch(_:Throwable){null}}")

                    isContinue = false
                }

        }
    }
    class Test3:VO(){
        var a by int
        var b by short
        var c by long
        var d by float
        var e by double
        var f by boolean
        var g by uint
        var h by ushort
        var i by ulong
        var j by string
        var al  by intList
        var bl  by shortList
        var cl  by longList
        var dl  by floatList
        var el  by doubleList
        var fl  by booleanList
        var gl  by uintList
        var hl  by ushortList
        var il  by ulongList
        var jl  by stringList
    }
    @Test
    fun test3(){
        runBlocking{
            var isContinue = true
            val vo = JSON.from(Test3(), flow{
                emit("""{"a"""")
                emit(""":1, """)
                emit(""""b":12""")
                emit("""3, "c":121444, """)
                emit(""""al":[1,2,3], """)
                emit(""""bl":[1,2,3], """)
                emit(""""cl":[1,2,3], """)
                emit(""""dl":[1.1,2.2,3.3], """)
                emit(""""el":[1.1,2.1,3.1], """)
                emit(""""fl":[true,false, true], """)
                emit(""""gl":[1,2,3], """)
                emit(""""hl":[1,2,3], """)
                emit(""""il":[1,2,3], """)
                emit(""""jl":["a","b","c"], """)
                emit(""""d":1.2, "e":1.3, "f":true, "g":1, "h":2, "i":3,""")
                emit(""""j":"abc"}""")
            }).takeWhile { isContinue }
                .last()
            println(vo)
        }
    }
}