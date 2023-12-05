package kore.vql.dsl.select

import kore.vo.VO
import kore.vql.dsl.Marker
import kore.vql.query.Select
import kore.vql.query.instance
import kore.vql.query.select.To
import kotlin.jvm.JvmInline
import kotlin.reflect.KProperty

/** order by--------------------------------------------------------
 * SQL : a asc, b desc
 * VQL :
 *     ToField1.asc
 *     ToField2.desc
 * */
@Marker
@JvmInline
value class Order<FROM:VO, TO:VO>@PublishedApi internal constructor(@PublishedApi internal val data:Select<FROM, TO>){
    inline operator fun <TO:VO> To<TO>.invoke(block:TO.()->KProperty<*>):ToField
    = ToField(factory.instance.block().name)

    inline val ToField.asc:Unit get() = data.order(true, this.prop)
    inline val ToField.desc:Unit get() = data.order(false, this.prop)
}