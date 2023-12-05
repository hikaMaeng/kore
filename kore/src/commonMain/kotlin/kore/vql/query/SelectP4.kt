package kore.vql.query

import kore.vo.VO
import kore.vql.dsl.SelectDSL
import kore.vql.query.select.Alias
import kore.vql.query.select.P
import kore.vql.query.select.To

class SelectP4<P1:VO, P2:VO, P3:VO, P4:VO, FROM:VO, TO:VO> @PublishedApi internal constructor(
    from:()->FROM, to:()->TO, p1:()->P1, p2:()->P2, p3:()->P3, p4:()->P4,
    block:SelectDSL<FROM, TO>.(from:Alias<FROM>, to:To<TO>, p1:P<P1>, p2:P<P2>, p3:P<P3>, p4:P<P4>)->Unit
):Select<FROM, TO>(from, to) {
    override val initializer:()->Unit = {
        SelectDSL(this).block(Alias(joins[0]), To(to), P(p1 to 0), P(p2 to 1), P(p3 to 2), P(p4 to 3))
    }
}