@file:Suppress("NOTHING_TO_INLINE")

package kore.vql.dsl.select

import kore.vo.VO
import kore.vql.dsl.Marker
import kore.vql.query.select.Alias
import kore.vql.query.Select
import kore.vql.query.instance
import kore.vql.query.select.P
import kore.vql.query.select.To
import kotlin.jvm.JvmInline
import kotlin.reflect.KProperty

/** select--------------------------------------------------------
 * SQL : a,b
 * VQL :
 *     AliasField1 to ToField1
 *     AliasField2 to ToField2
 * */
@Marker
@JvmInline
value class Projection<FROM:VO, TO:VO>@PublishedApi internal constructor(@PublishedApi internal val data:Select<FROM, TO>){
    inline operator fun <V:VO> Alias<V>.invoke(block:V.()->KProperty<*>):AliasField
    = AliasField(this to factory.instance.block().name)
    inline operator fun <V:VO> P<V>.invoke(block:V.()->KProperty<*>):ParamField
    = ParamField(this.index to factory.instance.block().name)
    inline operator fun <V:Any> To<TO>.invoke(block:TO.()->KProperty<V>):ToField
    = ToField(factory.instance.block().name)

    @JvmInline
    value class ToFieldList@PublishedApi internal constructor(@PublishedApi internal val prop:String)
    inline fun <LIST:MutableList<out VO>> To<TO>.shape(block:TO.()->KProperty<LIST>):ToFieldList
    = ToFieldList(factory.instance.block().name)

    inline infix fun AliasField.put(target:ToField){
        data.itemField(pair, target.prop)
    }
    inline infix fun ParamField.put(target:ToField){
        data.itemParam(pair, target.prop)
    }
    inline infix fun <F:VO, T:VO> Select<F, T>.put(target:ToFieldList){
        data.task(this, target.prop)
    }
    inline fun orderBy(block:Order<FROM, TO>.()->Unit):Unit = Order(data).block()
}