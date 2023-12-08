@file:Suppress("NOTHING_TO_INLINE", "PropertyName")

package kore.vql.query

import kore.vo.VO
import kore.vql.expression.Op
import kore.vql.query.select.*

sealed class Select<FROM:VO, TO:VO>(from:()->FROM, val to:()->TO):Query {
    class ShapeRelation(val rsKey:String, val parentRsKey:String, val op:Op)
    @PublishedApi internal abstract val initializer:()->Unit
    @PublishedApi internal var isInitialized = false
    @PublishedApi internal inline fun init(){
        if(!isInitialized){
            isInitialized = true
            initializer()
        }
    }
    val items:ArrayList<Item> = arrayListOf()
    @PublishedApi internal val joins:ArrayList<Join> = arrayListOf(Join(from, "", 0, ""))
    inline fun getJoinWithRsKey(rsKey:String):Pair<Int, Join>?
    = joins.find{it.bProp.ifEmpty{it.aProp} == rsKey}?.let{joins.indexOf(it) to it}
    @PublishedApi internal var _orders:LinkedHashSet<Order>? = null
    @PublishedApi internal val orders:LinkedHashSet<Order> get() = _orders ?: linkedSetOf<Order>().also {_orders = it}
    @PublishedApi internal var _where:ArrayList<Case>? = null
    @PublishedApi internal val where:ArrayList<Case> get() = _where ?: arrayListOf(Case()).also {_where = it}
    @PublishedApi internal var _shapeRelation:ArrayList<ShapeRelation>? = null
    val shapeRelations:ArrayList<ShapeRelation> get() = _shapeRelation ?: arrayListOf<ShapeRelation>().also {_shapeRelation = it}
    @PublishedApi internal inline fun <A:VO> join(a:Pair<()->A, String>, b:Pair<Alias<*>, String>):Alias<A>
    = Alias(Join(a.first, a.second, b.first.index(joins), b.second).also{joins.add(it)})

    @PublishedApi internal inline fun order(isAsc:Boolean, prop:String) {
        orders.add(Order(isAsc, prop))
    }
    @PublishedApi internal inline fun itemField(alias:Pair<Alias<*>, String>, to:String) {
        items.add(Item.Field(alias, to))
    }
    @PublishedApi internal inline fun task(select:Select<*, *>, to:String) {
        items.add(Item.Shape(select, to))
    }
    @PublishedApi internal inline fun itemParam(p:Pair<Int, String>, to:String) {
        items.add(Item.Param(p, to))
    }
    @PublishedApi internal inline fun shape(rsKey:String, parentRsKey:String, op:Op) {
        shapeRelations.add(ShapeRelation(rsKey, parentRsKey, op))
    }
    @PublishedApi internal inline fun whereIn(op:Op, a:Pair<Alias<*>, String>, values:List<Any>){
        where.last().items.add(Case.Values(op, a, values))
    }
    @PublishedApi internal inline fun whereValue(op:Op, a:Pair<Alias<*>, String>, value:Any) {
        where.last().items.add(Case.Value(op, a, value))
    }
    @PublishedApi internal inline fun whereField(op:Op, a:Pair<Alias<*>, String>, b:Pair<Alias<*>, String>) {
        where.last().items.add(Case.Field(op, a, b))
    }
    @PublishedApi internal inline fun whereParam(op:Op, a:Pair<Alias<*>, String>, b:Pair<Int, String>) {
        where.last().items.add(Case.Param(op, a, b))
    }
    @PublishedApi internal inline fun or() {
        where.add(Case())
    }
}