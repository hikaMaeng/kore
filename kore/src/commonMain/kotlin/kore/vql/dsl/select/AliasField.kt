package kore.vql.dsl.select

import kore.vql.query.select.Alias
import kotlin.jvm.JvmInline

@JvmInline
value class AliasField@PublishedApi internal constructor(@PublishedApi internal val pair:Pair<Alias<*>, String>)