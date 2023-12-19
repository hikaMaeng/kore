package vo.json

import kore.vjson.VOJson
import kore.vo.VO
import kore.vo.field.value.int
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class test1 {
    class Test1:VO(){
        var a by int

    }
    @Test
    fun test1(){
        runBlocking{
            VOJson.to(Test1().also {it.a = 1})
                .onCompletion {
                    println("complete")
                }
                .collect{
                    println(it)
                }
            VOJson.from(Test1(), flow{
                emit("{")
                emit(""""a""")
                emit("""":1""")
                emit("""}""")
            }).collect{
                println(it)
            }
        }

    }
}