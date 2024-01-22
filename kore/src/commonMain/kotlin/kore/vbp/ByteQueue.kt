package kore.vbp

import kotlinx.io.Buffer

class ByteQueue{
    private var buffer:ByteArray = ByteArray(1000)
    private var cursor:Int = 0
    private var end:Int = 0

    val size:Int get() = end - cursor
    val curr:Byte get() = buffer[cursor]
    fun isNotEmpty():Boolean = cursor < end
    fun joinToString(sep:String = ","):String = (cursor until end).fold(""){acc, it->
        acc + sep + "${buffer[it]}"
    }.substring(1)

    private inline fun expand(n:Int, block:()->Unit){
        var expandSize:Int = buffer.size
        while(end + n > expandSize) expandSize *= 2
        if(expandSize != buffer.size){
            val prev = buffer
            buffer = ByteArray(expandSize)
            prev.copyInto(buffer)
        }
        block()
        end += n
    }
    operator fun plus(b:ByteArray):ByteQueue{
        expand(b.size){
            b.copyInto(buffer, end)
        }
        return this
    }
    operator fun plus(b:Byte):ByteQueue{
        expand(1){
            buffer[end] = b
        }
        return this
    }
    fun drop(n:Int){
        if (n > size || n < 0) throw IllegalArgumentException("Invalid argument: n must be between 0 and the size of the buffer")
        cursor += n
        if (cursor >= buffer.size / 2) {
            buffer.copyInto(buffer, 0, cursor, end)
            end -= cursor
            cursor = 0
        }else {
            if(cursor >= end) clear()
        }
    }
    fun dropOne():Byte = curr.also{drop(1)}
    fun clear(){
        cursor = 0
        end = 0
    }
    fun consume(n:Int):ByteArray = ByteArray(n).also{
        if (n > size || n < 0) throw IllegalArgumentException("Invalid argument: n must be between 0 and the size($size) of the buffer")
        buffer.copyInto(it, 0, cursor, cursor + n)
        drop(n)
    }
    fun <V:Any> buffer(n:Int, block:Buffer.()->V):V = Buffer().let{buf->
        if (n > size || n < 0) throw IllegalArgumentException("Invalid argument: n must be between 0 and the size of the buffer")
        buf.write(consume(n))
        buf.block().also{buf.close()}
    }
    fun indexOf(b:Byte):Int{
        var i = cursor
        while (i < end) {
            if(buffer[i] == b) return i - cursor
            i++
        }
        return -1
    }
    operator fun get(index:Int):Byte{
        if (index >= size || index < 0) throw IllegalArgumentException("Invalid argument: index must be between 0 and the size of the buffer")
        return buffer[cursor + index]
    }
}