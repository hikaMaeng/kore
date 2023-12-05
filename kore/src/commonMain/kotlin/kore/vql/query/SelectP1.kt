package kore.vql.query

import kore.vo.VO
import kore.vql.dsl.SelectDSL
import kore.vql.query.select.Alias
import kore.vql.query.select.P
import kore.vql.query.select.To

class SelectP1<P1: VO, FROM: VO, TO: VO>@PublishedApi internal constructor(
    from:()->FROM, to:()->TO, p1:()->P1,
    block:SelectDSL<FROM, TO>.(from:Alias<FROM>, to:To<TO>, p1:P<P1>)->Unit
):Select<FROM, TO>(from, to){
    override val initializer:()->Unit = {
        SelectDSL(this).block(Alias(joins[0]), To(to), P(p1 to 0))
    }
}