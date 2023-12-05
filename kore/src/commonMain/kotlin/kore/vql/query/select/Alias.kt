@file:Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")

package kore.vql.query.select

import kore.vo.VO
import kotlin.jvm.JvmInline

@JvmInline
value class Alias<V:VO>(val join:Join){
    inline val factory:()->V get() = join.a as ()->V
    inline fun index(joins:ArrayList<Join>):Int = joins.indexOf(join)
}