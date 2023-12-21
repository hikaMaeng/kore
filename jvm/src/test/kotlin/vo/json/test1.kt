package vo.json

import kore.vjson.JSON
import kore.vo.VO
import kore.vo.field.value.int
import kotlinx.coroutines.flow.flow
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
}