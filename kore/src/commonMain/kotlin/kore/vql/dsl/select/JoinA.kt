package kore.vql.dsl.select

import kore.vo.VO
import kotlin.jvm.JvmInline

/** Join--------------------------------------------------------
 *  SQL : join A on A.prop = B.prop
 *  VQL : JoinA on Alias
 * */
@JvmInline
value class JoinA<A:VO>@PublishedApi internal constructor(@PublishedApi internal val pair:Pair<()->A, String>)