package kore.vql.query.select

import kore.vo.VO
import kotlin.jvm.JvmInline

@JvmInline value class To<V0: VO>(val factory:()->V0)
