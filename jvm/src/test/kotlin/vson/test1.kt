package vson

import kore.vo.VO
import kore.vo.field.value.*
import kore.vosn.VSON
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class test1 {
    class Test1:VO(){
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
    }
    @Test
    fun test1(){
        runBlocking{
            val vo = Test1().also {
                it.a = 1
                it.b = 123.toShort()
                it.c = 121444L
                it.d = 1.2f
                it.e = 1.3
                it.f = true
                it.g = 1u
                it.h = 2u
                it.i = 3u
                it.j = "abc"
            }
            val str = VSON.to(vo).fold(""){acc, c->
                acc + c
            }
            assertEquals(str, "1|123|121444|1.2|1.3|true|1|2|3|abc")
        }
    }
    class Test2:VO(){
        var a by int
        var b by short{
            exclude()
        }
        var c by long{
            optinal()
        }
        var d by float
        var e by string{
            optinal()
        }
    }
    @Test
    fun test2(){
        runBlocking{
            var vo = Test2().also {
                it.a = 1
                it.b = 123.toShort()
                it.d = 1.2f
            }
            var str = VSON.to(vo).fold(""){acc, c->
                acc + c
            }
            //exclude에 값이 있는데 제거되는지
            assertEquals(str, "1|~|1.2")
            vo = Test2().also {
                it.a = 1
                it.d = 1.2f
            }
            str = VSON.to(vo).fold(""){acc, c->
                acc + c
            }
            //exclude에 값이 없는데 제거되는지
            assertEquals(str, "1|~|1.2")
            //옵셔널에 값을 넣은 경우
            vo = Test2().also {
                it.a = 1
                it.c = 15L
                it.d = 1.2f
            }
            str = VSON.to(vo).fold(""){acc, c->
                acc + c
            }
            assertEquals(str, "1|~|15|1.2")
            //문자열 인코딩 테스트
            vo = Test2().also {
                it.a = 1
                it.c = 15L
                it.d = 1.2f
                it.e = "a|b~c!\\n"
            }
            str = VSON.to(vo).fold(""){acc, c->
                acc + c
            }
            assertEquals(str, "1|~|15|1.2|a\\|b~c\\!\\\\n")
        }
    }
}