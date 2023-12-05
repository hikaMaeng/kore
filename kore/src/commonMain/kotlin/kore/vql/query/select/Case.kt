package kore.vql.query.select

import kore.vql.expression.Op
import kotlin.reflect.KClass

class Case{
    sealed interface Item
    class Values(val op:Op, val a:Pair<Alias<*>, String>, val values:List<*>):Item
    class Value(val op:Op, val a:Pair<Alias<*>, String>, val value:Any):Item
    class Field(val op:Op, val a:Pair<Alias<*>, String>, val b:Pair<Alias<*>, String>):Item
    class Param(val op:Op, val a:Pair<Alias<*>, String>, val b:Pair<Int, String>):Item

    val items:ArrayList<Item> = arrayListOf()
}