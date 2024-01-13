package kore.vbp

class ByteQueue{
    private var buffer:ByteArray = ByteArray(1000)
    private var cursor:Int = 0
    private var length:Int = 0

    val size:Int get() = length - cursor
    val curr:Byte get() = buffer[cursor]
    fun isNotEmpty():Boolean = cursor < length
    fun joinToString(sep:String = ","):String = (cursor until length).fold(""){acc, it->
        acc + sep + "${buffer[it]}"
    }.substring(1)

    private inline fun expand(n:Int, block:()->Unit){
        var expandSize:Int = buffer.size
        while(length + n > expandSize) expandSize *= 2
        if(expandSize != buffer.size){
            val prev = buffer
            buffer = ByteArray(expandSize)
            prev.copyInto(buffer)
        }
        block()
        length += n
    }
    operator fun plus(b:ByteArray):ByteQueue{
        expand(b.size){
            b.copyInto(buffer, length)
        }
        return this
    }
    operator fun plus(b:Byte):ByteQueue{
        expand(1){
            buffer[length] = b
        }
        return this
    }
    fun drop(n:Int){
        cursor += n
        if(cursor >= length) clear()
    }
    fun dropOne():Byte = curr.also {drop(1)}
    fun clear(){
        cursor = 0
        length = 0
    }


}