@file:Suppress("UNCHECKED_CAST")

package kore.vjson

import kotlinx.coroutines.flow.FlowCollector

internal class ToEmitter(type:Int/** 0 value, 1 string, 2 list, 3 map*/){
    val f:suspend FlowCollector<String>.(v:Any)->Unit = when(type){
        0->({emit("$it")})
        1->({emit("\"$it\"")})
        2->({
            it as List<Any>
            emit("[")
            val size:Int = it.size
            var i:Int = 0
            do{
                if(i != 0) emit(",")
                val v = it[i]
                JSON.getStringify(v::class)(v)
            }while(++i < size)
            emit("]")
        })
        3->({
            it as Map<String, Any>
            val keys:Array<String> = it.keys.toTypedArray()
            emit("{")
            val size:Int = keys.size
            var i:Int = 0
            do{
                if(i != 0) emit(",")
                val v = it[keys[i]]!!
                JSON.getStringify(v::class)(v)
            }while(++i < size)
            emit("}")
        })
        else->throw Throwable("invalid type $type")
    }
}