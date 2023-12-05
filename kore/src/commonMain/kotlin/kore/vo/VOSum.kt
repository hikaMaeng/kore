package kore.vo

import kotlin.reflect.KClass

abstract class VOSum<out V:VO>(vararg val factories:()->V){
    private var _type:List<KClass<*>>? = null
    val type:List<KClass<*>> get() = _type ?: factories.map{it()::class}.also{_type = it}
}