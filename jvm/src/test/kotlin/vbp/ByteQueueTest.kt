package vbp

import kore.bytes.ByteQueue
import kotlinx.io.readDouble
import kotlinx.io.readFloat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ByteQueueTest {
    @Test
    fun testByteQueue() {
        // ByteQueue 인스턴스 생성
        val byteQueue = ByteQueue()

        // 바이트 배열 추가
        byteQueue + byteArrayOf(1, 2, 3, 4, 5)

        // 단일 바이트 추가
        byteQueue + 6.toByte()
        println("0")
        // size, curr, isNotEmpty, joinToString, indexOf, get 메서드 테스트
        assertEquals(6, byteQueue.size)
        assertEquals(1.toByte(), byteQueue.curr)
        assertTrue(byteQueue.isNotEmpty())
        assertEquals("1,2,3,4,5,6", byteQueue.joinToString())
        assertEquals(2, byteQueue.indexOf(3.toByte()))
        assertEquals(4.toByte(), byteQueue[3])
        println("1")
        // drop, dropOne 메서드 테스트
        byteQueue.drop(2)
        assertEquals(4, byteQueue.size)
        assertEquals(3.toByte(), byteQueue.curr)
        assertEquals(3.toByte(), byteQueue.dropOne())
        assertEquals(3, byteQueue.size)
        println("2")
        // read, buffer 메서드 테스트
        assertArrayEquals(byteArrayOf(4, 5, 6), byteQueue.consume(3))
        assertEquals(0, byteQueue.size)
        println("3")
        // clear 메서드 테스트
        byteQueue + byteArrayOf(7, 8, 9)
        byteQueue.clear()
        assertEquals(0, byteQueue.size)
        assertFalse(byteQueue.isNotEmpty())
    }
    @Test
    fun testBuffer() {
        val byteQueue = ByteQueue()

        // Int 값 테스트
        byteQueue + byteArrayOf(0, 0, 0, 1)
        val intVal = byteQueue.buffer(4) { readInt() }
        assertEquals(1, intVal)
        assertEquals(0, byteQueue.size) // buffer 메서드가 drop을 호출했음을 확인

        // Short 값 테스트
        byteQueue + byteArrayOf(0, 1)
        val shortVal = byteQueue.buffer(2) { readShort() }
        assertEquals(1, shortVal.toInt())
        assertEquals(0, byteQueue.size) // buffer 메서드가 drop을 호출했음을 확인

        // Long 값 테스트
        byteQueue + byteArrayOf(0, 0, 0, 0, 0, 0, 0, 1)
        val longVal = byteQueue.buffer(8) { readLong() }
        assertEquals(1L, longVal)
        assertEquals(0, byteQueue.size) // buffer 메서드가 drop을 호출했음을 확인

        // Float 값 테스트
        byteQueue + byteArrayOf(63, -116, -52, -51) // 1.1f의 바이트 표현
        val floatVal = byteQueue.buffer(4) { readFloat() }
        assertEquals(1.1f, floatVal)
        assertEquals(0, byteQueue.size) // buffer 메서드가 drop을 호출했음을 확인

        // Double 값 테스트
        byteQueue + byteArrayOf(63, -16, 0, 0, 0, 0, 0, 0) // 1.0의 바이트 표현
        val doubleVal = byteQueue.buffer(8) { readDouble() }
        assertEquals(1.0, doubleVal)
        assertEquals(0, byteQueue.size) // buffer 메서드가 drop을 호출했음을 확인

        // Boolean 값 테스트
        byteQueue + byteArrayOf(1) // true의 바이트 표현
        val booleanVal = byteQueue.buffer(1) { readByte().toInt() == 1 }
        assertTrue(booleanVal)
        assertEquals(0, byteQueue.size) // buffer 메서드가 drop을 호출했음을 확인
    }
}