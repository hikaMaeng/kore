package kore.vql.query.select

import kore.vo.VO
import kotlin.jvm.JvmInline

@JvmInline
value class P<V0:VO>(val pair:Pair<()->V0, Int>){
    inline val factory:()->V0 get() = pair.first
    inline val index:Int get() = pair.second
}