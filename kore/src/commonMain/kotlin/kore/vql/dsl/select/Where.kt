@file:Suppress("NOTHING_TO_INLINE")

package kore.vql.dsl.select

import kore.vo.VO
import kore.vql.dsl.Marker
import kore.vql.expression.Op
import kore.vql.query.select.Alias
import kore.vql.query.Select
import kore.vql.query.instance
import kore.vql.query.select.P
import kotlin.jvm.JvmInline
import kotlin.reflect.KProperty
@Marker
@JvmInline value class Where<FROM:VO, TO:VO>@PublishedApi internal constructor(@PublishedApi internal val data:Select<FROM, TO>){
    inline operator fun <V:VO> Alias<V>.invoke(block:V.()->KProperty<*>):AliasField
    = AliasField(this to factory.instance.block().name)

    inline operator fun <V:VO> P<V>.invoke(block:V.()->KProperty<*>):ParamField
    = ParamField(index to factory.instance.block().name)

    inline val or:Unit get() = data.or()

    inline infix fun AliasField.not(value:String):Unit = data.whereValue(Op.Not, this.pair, value)
    inline infix fun AliasField.equal(value:String):Unit = data.whereValue(Op.Equal, this.pair, value)
    inline infix fun AliasField.less(value:String):Unit = data.whereValue(Op.Less, this.pair, value)
    inline infix fun AliasField.greater(value:String):Unit = data.whereValue(Op.Greater, this.pair, value)
    inline infix fun AliasField.under(value:String):Unit = data.whereValue(Op.Under, this.pair, value)
    inline infix fun AliasField.over(value:String):Unit = data.whereValue(Op.Over, this.pair, value)

    inline infix fun AliasField.not(value:Number):Unit = data.whereValue(Op.Not, this.pair, value)
    inline infix fun AliasField.equal(value:Number):Unit = data.whereValue(Op.Equal, this.pair, value)
    inline infix fun AliasField.less(value:Number):Unit = data.whereValue(Op.Less, this.pair, value)
    inline infix fun AliasField.greater(value:Number):Unit = data.whereValue(Op.Greater, this.pair, value)
    inline infix fun AliasField.under(value:Number):Unit = data.whereValue(Op.Under, this.pair, value)
    inline infix fun AliasField.over(value:Number):Unit = data.whereValue(Op.Over, this.pair, value)

    inline infix fun AliasField.not(value:Boolean):Unit = data.whereValue(Op.Not, this.pair, value)
    inline infix fun AliasField.equal(value:Boolean):Unit = data.whereValue(Op.Equal, this.pair, value)
    inline infix fun AliasField.less(value:Boolean):Unit = data.whereValue(Op.Less, this.pair, value)
    inline infix fun AliasField.greater(value:Boolean):Unit = data.whereValue(Op.Greater, this.pair, value)
    inline infix fun AliasField.under(value:Boolean):Unit = data.whereValue(Op.Under, this.pair, value)
    inline infix fun AliasField.over(value:Boolean):Unit = data.whereValue(Op.Over, this.pair, value)

    inline operator fun <L:Any> List<L>.contains(a:AliasField):Boolean{
        data.whereIn(Op.In, a.pair, this)
        return true
    }
    inline infix fun <L:Any> AliasField.notIn(values:List<L>){
        data.whereIn(Op.NotIn, this.pair, values)
    }

    inline infix fun AliasField.equal(b:AliasField):Unit = data.whereField(Op.Equal, this.pair, b.pair)
    inline infix fun AliasField.less(b:AliasField):Unit = data.whereField(Op.Less, this.pair, b.pair)
    inline infix fun AliasField.greater(b:AliasField):Unit = data.whereField(Op.Greater, this.pair, b.pair)
    inline infix fun AliasField.under(b:AliasField):Unit = data.whereField(Op.Under, this.pair, b.pair)
    inline infix fun AliasField.over(b:AliasField):Unit = data.whereField(Op.Over, this.pair, b.pair)

    inline infix fun AliasField.equal(b:ParamField):Unit = data.whereParam(Op.Equal, this.pair, b.pair)
    inline operator fun ParamField.contains(a:AliasField):Boolean{
        data.whereParam(Op.In, a.pair, this.pair)
        return true
    }
}