@file:Suppress("NOTHING_TO_INLINE")

package kore.vo.task

import kore.error.E
import kore.vo.VO

abstract class Task{
    fun interface Default{
        operator fun invoke(target:VO, key:String):Any?
    }
    class NoDefault(val vo:VO, val name:String):E(name)
    class TaskFail(val type:String, val vo:VO, val key:String, val result:Any):E(result)
    companion object{
        val INCLUDE:(String, Any?)->Boolean = {_, _->true}
        val EXCLUDE:(String, Any?)->Boolean = {_, _->false}
        val OPTIONAL:(String, Any?)->Boolean = {_, v->v != null}
    }
    @PublishedApi internal var _default:Any? = null
    @PublishedApi internal var _setTasks:ArrayList<(VO, String, Any)->Any?>? = null
    @PublishedApi internal var _getTasks:ArrayList<(VO, String, Any)->Any?>? = null
    var include:(String, Any?)->Boolean = INCLUDE
        internal set
    inline fun getDefault(vo:VO, key:String):Any? = _default?.let{
        when(it){
            is Default-> it(vo, key)
            else->it
        }?.also {v->
            vo[key] = v
            vo.values[key]
        }
    }
    inline fun getFold(vo:VO, key:String, v:Any):Any?
    = if(_getTasks == null) v else
        _getTasks?.fold(v as Any?){acc, next->acc?.let{next(vo, key, it)}}
    inline fun setFold(vo:VO, key:String, v:Any):Any? = _setTasks?.fold(v as Any?){acc, next->
        acc?.let{next(vo, key, it)}
    }
    fun exclude(){include = EXCLUDE}
    fun optinal(){include = OPTIONAL}
    fun isInclude(block:(String, Any?)->Boolean){include = block}
    fun default(block:Default){_default = block}
}