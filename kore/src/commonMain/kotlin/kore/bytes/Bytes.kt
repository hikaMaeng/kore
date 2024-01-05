package kore.bytes

import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import kotlinx.io.writeString

object Bytes {
    val a:Buffer = Buffer().also {
        it.writeString("")
        it.writeByte(0)
    }
    //val aaa = a.size

    val g = a.readByteArray()



//    val c = a.readInt()
   //val d = a.readDouble()
//    val e = a.readString()

}