@file:Suppress("NOTHING_TO_INLINE")

package kore.vql.dsl

import kore.vo.VO
import kore.vql.dsl.select.*
import kore.vql.query.*
import kore.vql.query.select.Alias
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmInline
import kotlin.reflect.KProperty
@Marker
@JvmInline value class SelectDSL<FROM:VO, TO:VO>@PublishedApi internal constructor(@PublishedApi internal val data:Select<FROM, TO>){
    inline operator fun <A:VO> (()->A).invoke(block:A.()->KProperty<*>):JoinA<A>
    = JoinA(this to instance.block().name)
    inline operator fun <V:VO> Alias<V>.invoke(block:V.()->KProperty<*>):AliasField
    = AliasField(this to factory.instance.block().name)
    inline infix fun <A:VO> JoinA<A>.join(target:AliasField):Alias<A>
    = data.join(pair, target.pair)

    inline fun select(block: Projection<FROM, TO>.()->Unit):Unit = Projection(data).block()
    inline fun where(block: Where<FROM, TO>.()->Unit):Unit = Where(data).block()
    inline fun shape(block:Shape<FROM, TO>.()->Unit){
        Shape(data).block()
    }
}