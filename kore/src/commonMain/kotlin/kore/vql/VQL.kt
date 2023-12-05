@file:Suppress("NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package kore.vql

import kore.vo.VO
import kore.vql.dsl.SelectDSL
import kore.vql.query.*
import kore.vql.query.select.Alias
import kore.vql.query.select.P
import kore.vql.query.select.To

inline fun <FROM:VO, TO:VO> (()->FROM).select(
    noinline to:()->TO,
    noinline block:SelectDSL<FROM, TO>.(from:Alias<FROM>, to:To<TO>)->Unit
): SelectP0<FROM, TO>
= SelectP0(this, to, block)
inline fun <P1:VO, FROM:VO, TO:VO> (()->FROM).select(
    noinline to:()->TO,
    noinline p1:()->P1,
    noinline block:SelectDSL<FROM, TO>.(from:Alias<FROM>, to:To<TO>, p1:P<P1>)->Unit
): SelectP1<P1, FROM, TO>
= SelectP1(this, to, p1, block)
inline fun <P1:VO, P2:VO, FROM:VO, TO:VO> (()->FROM).select(
    noinline to:()->TO,
    noinline p1:()->P1,
    noinline p2:()->P2,
    noinline block:SelectDSL<FROM, TO>.(from:Alias<FROM>, to:To<TO>, p1:P<P1>, p2:P<P2>)->Unit
):SelectP2<P1, P2, FROM, TO>
= SelectP2(this, to, p1, p2, block)
inline fun <P1:VO, P2:VO, P3:VO, FROM:VO, TO:VO> (()->FROM).select(
    noinline to:()->TO,
    noinline p1:()->P1,
    noinline p2:()->P2,
    noinline p3:()->P3,
    noinline block:SelectDSL<FROM, TO>.(from:Alias<FROM>, to:To<TO>, p1:P<P1>, p2:P<P2>, p3:P<P3>)->Unit
):SelectP3<P1, P2, P3, FROM, TO>
= SelectP3(this, to, p1, p2, p3, block)
inline fun <P1:VO, P2:VO, P3:VO, P4:VO, FROM:VO, TO:VO> (()->FROM).select(
    noinline to:()->TO,
    noinline p1:()->P1,
    noinline p2:()->P2,
    noinline p3:()->P3,
    noinline p4:()->P4,
    noinline block:SelectDSL<FROM, TO>.(from:Alias<FROM>, to:To<TO>, p1:P<P1>, p2:P<P2>, p3:P<P3>, p4:P<P4>)->Unit
):SelectP4<P1, P2, P3, P4, FROM, TO>
= SelectP4(this, to, p1, p2, p3, p4, block)
