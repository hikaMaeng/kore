package kore.vql.query

import kore.vo.VO
import kore.vql.dsl.SelectDSL
import kore.vql.query.select.Alias
import kore.vql.query.select.To

class SelectP0<FROM: VO, TO: VO>@PublishedApi internal constructor(
    from:()->FROM, to:()->TO,
    block:SelectDSL<FROM, TO>.(from:Alias<FROM>, to:To<TO>)->Unit
): Select<FROM, TO>(from, to){
    override val initializer:()->Unit = {SelectDSL(this).block(Alias(joins[0]), To(to))}
}