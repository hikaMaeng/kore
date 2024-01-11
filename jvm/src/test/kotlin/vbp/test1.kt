package vbp

import kore.bytes.Bytes
import kore.vbp.VBP
import kore.vo.VO
import kore.vo.field.value.*
import kore.vosn.VSON
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking
import kotlinx.io.*
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
            val arr = VBP.to(vo).fold(byteArrayOf()){acc, c->
                acc + c
            }
            println("---${arr.joinToString { "$it" }}---")
            val buffer = Buffer().also {
                it.writeByte(0)
                it.writeInt(1)
                it.writeByte(1)
                it.writeShort(123)
                it.writeByte(2)
                it.writeLong(121444)
                it.writeByte(3)
                it.writeFloat(1.2f)
                it.writeByte(4)
                it.writeDouble(1.3)
                it.writeByte(5)
                it.writeByte(1)
                it.writeByte(6)
                it.writeUInt(1u)
                it.writeByte(7)
                it.writeUShort(2u)
                it.writeByte(8)
                it.writeULong(3u)
                it.writeByte(9)
                it.writeString("abc")
                it.writeByte(0)
                it.writeByte(-1)
            }.readByteArray()
            assertEquals(arr.joinToString{"$it"}, buffer.joinToString{"$it"})
            val v = VBP.from(Test1(), flow{emit(arr)}).last()
            assertEquals(v.a, 1)
            assertEquals(v.b, 123.toShort())
            assertEquals(v.c, 121444L)
            assertEquals(v.d, 1.2f)
            assertEquals(v.e, 1.3)
            assertEquals(v.f, true)
            assertEquals(v.g, 1u)
            assertEquals(v.h, 2u)
            assertEquals(v.i, 3u)
            assertEquals(v.j, "abc")
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
            exclude()
        }
    }
    @Test
    fun test2(){
        runBlocking{
            println("---${Bytes.g.joinToString {"${it}"}}---")
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
            assertEquals(str, "1|15|1.2")
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
//            assertEquals(str, "1|15|1.2|a\\|b~c\\!\\\\n")

        }
    }
}