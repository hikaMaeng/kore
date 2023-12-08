@file:Suppress("NOTHING_TO_INLINE", "FunctionName", "SqlSourceToSinkFlow")

package kore.vql.sql

import kore.vo.VO
import kore.vql.query.*
import kore.vql.query.select.Item
import kotlinx.coroutines.flow.*
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.bind
import org.springframework.r2dbc.core.flow

@PublishedApi internal suspend fun <FROM:VO, TO:VO, P1:VO, P2:VO, P3:VO, P4:VO> Select<FROM, TO>._r2dbcSelect(client:DatabaseClient, p1:P1, p2:P2, p3:P3, p4:P4, parent:List<VO>? = null):Flow<TO>{
    val result = _sql(p1, p2, p3, p4)
//    println("-----------------")
//    println("${result.sql}")
//    println("$parent")
//    println("-----------------")
    return client.sql(result.sql).let {
        result.binds.fold(
            if(parent == null) it else shapeRelations.fold(it){acc, relation->
//                println("+++P_${relation.parentRsKey}, ${parent.map{vo->vo.props[relation.parentRsKey]!!}.toTypedArray().joinToString()}")
                acc.bind("P_${relation.parentRsKey}", parent.map{vo->vo.props[relation.parentRsKey]!!})
            }
        ){acc, s ->
            val p = when (s[0]) {
                '0' -> p1
                '1' -> p2
                '2' -> p3
                '3' -> p4
                else -> return@let null
            }
            val key = s.substring(2)
            acc.bind(key, p.props[key]!!)
        }
    }?.fetch()?.flow()?.map{rs->
        to().also{to->rs.forEach{(k, v) ->to[result.map[k]!!] = v}}
    }?.let{flow->
        val shapes = items.filterIsInstance<Item.Shape>()
        if(shapes.isEmpty()) flow else flow{
            val emitter:FlowCollector<TO> = this
            val list:ArrayList<TO> = arrayListOf()
            flow.onCompletion {err->
//                println("*************root complete:$list")
                var count = shapes.size
                shapes.forEach {shape->
                    shape.select._r2dbcSelect(client, p1, p2, p3, p4, list)
                    .onCompletion {
//                        println("onCompletion:$count")
                        count--
                        if(count == 0){
//                            println("onEmit:$emitter")
                            list.forEach { emitter.emit(it) }
                        }
                    }
                    .collect{vo->
//                        println("onCollect:-----------------$vo")
                        list.filter{parentItem->
//                            println("filter----$parentItem")
                            shape.select.shapeRelations.all{relation->
//                                println("voKey:${relation.rsKey}, ${vo[relation.rsKey]}")
//                                println("pKey:${relation.parentRsKey}, ${parentItem[relation.parentRsKey]}")
                                vo[relation.rsKey] == parentItem[relation.parentRsKey]
                            }
                        }.forEach{parentItem->
//                            println("parentItem:$parentItem, | to:${shape.to},| ${parentItem[shape.to].hashCode()} | ${parentItem[shape.to] as? MutableList<Any>}")
//                            println("$vo")
                            @Suppress("UNCHECKED_CAST")
                            val shapeTarget = parentItem[shape.to] as? MutableList<Any>
                            if(shapeTarget != null){
//                                println("-----${shapeTarget.size}------")
                                shapeTarget.add(vo)
//                                println("-----${shapeTarget.size}------")
                            }

                        }
                    }
                }
            }.collect{
//                println("*************root collect:$it")
                list.add(it)
            }
        }
    } ?: flow{}
}

suspend inline fun <FROM:VO, TO:VO> SelectP0<FROM, TO>.r2dbcSelect(client: DatabaseClient):Flow<TO> = _r2dbcSelect(client, None, None, None, None)
suspend inline fun <P1:VO, FROM:VO, TO:VO> SelectP1<P1, FROM, TO>.r2dbcSelect(client:DatabaseClient, p1:P1):Flow<TO> = _r2dbcSelect(client, p1, None, None, None)
suspend inline fun <P1:VO, P2:VO, FROM:VO, TO:VO> SelectP2<P1, P2, FROM, TO>.r2dbcSelect(client:DatabaseClient, p1:P1, p2:P2):Flow<TO> = _r2dbcSelect(client, p1, p2, None, None)
suspend inline fun <P1:VO, P2:VO, P3:VO, FROM:VO, TO:VO> SelectP3<P1, P2, P3, FROM, TO>.r2dbcSelect(client:DatabaseClient, p1:P1, p2:P2, p3:P3):Flow<TO> = _r2dbcSelect(client, p1, p2, p3, None)
suspend inline fun <P1:VO, P2:VO, P3:VO, P4:VO, FROM:VO, TO:VO> SelectP4<P1, P2, P3, P4, FROM, TO>.r2dbcSelect(client:DatabaseClient, p1:P1, p2:P2, p3:P3, p4:P4):Flow<TO> = _r2dbcSelect(client, p1, p2, p3, p4)
