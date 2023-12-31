package kore.vql.query.select

import kore.vql.query.Select

sealed interface Item{
    class Shape(val select:Select<*, *>, val to:String):Item
    class Field(val alias:Pair<Alias<*>, String>, val to:String):Item
    class Param(val p:Pair<Int, String>, val to:String):Item
    companion object{
        val NO_PARAM = Param(-1 to "", "")
    }
}