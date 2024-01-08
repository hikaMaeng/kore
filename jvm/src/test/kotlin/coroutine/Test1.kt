package coroutine

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.test.Test


class Test1 {
    class Test1{
        suspend fun a():Int{
            return 1
        }
//        suspend fun b() = susintercepted
    }

    @Test
    fun test1(){

        val a = sequence<Int>{
            yield(1)
            yield(2)
            yield(3)
        }
        runBlocking {
            val t = Test1()
            val a = t.a()
            println(a)
        }
    }
}