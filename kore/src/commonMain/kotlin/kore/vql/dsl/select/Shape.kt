@file:Suppress("NOTHING_TO_INLINE")

package kore.vql.dsl.select

import kore.vo.VO
import kore.vql.dsl.Marker
import kore.vql.expression.Op
import kore.vql.query.Select
import kore.vql.query.instance
import kore.vql.query.select.Alias
import kore.vql.query.select.To
import kotlin.jvm.JvmInline
import kotlin.reflect.KProperty
@Marker
@JvmInline
value class Shape<FROM:VO, TO: VO>(val data: Select<FROM, TO>){
    inline operator fun <V:VO> Alias<V>.invoke(block:V.()->KProperty<*>):AliasField
    = AliasField(this to factory.instance.block().name)
    inline operator fun <V:VO> To<V>.invoke(block:V.()->KProperty<*>):ToField
    = ToField(factory.instance.block().name)

    inline operator fun ToField.contains(from:ToField):Boolean{
        data.shape(from.prop, prop, Op.In)
        return true
    }
}