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
    return client.sql(result.sql).let {
        result.binds.fold(
            if(parent == null) it else relations.fold(it){acc, relation->
                acc.bind("P_${relation.parentRsKey}", parent.map{vo->vo[relation.parentRsKey]!!})
            }
        ){acc, s ->
            val key = s.substring(2)
            acc.bind(key, when(s[0]){
                '0' -> p1
                '1' -> p2
                '2' -> p3
                '3' -> p4
                else -> return@let null
            }[key])
        }
    }?.fetch()?.flow()?.map{rs->
        to().also{to->rs.forEach{(k, v) ->to[result.map[k]!!] = v}}
    }?.let{f->
        val shapes:List<Item.Shape> = getShapesFromItem()
        if(shapes.isEmpty()) f else flow{
            val emitter:FlowCollector<TO> = this
            val list:ArrayList<TO> = f.fold(arrayListOf()){list, to->list.also{it.add(to)}}
            var count = shapes.size
            shapes.forEach {shape->
                shape.select._r2dbcSelect(client, p1, p2, p3, p4, list)
                .onCompletion {
                    count--
                    if(count == 0)list.forEach{emitter.emit(it)}
                }
                .collect{vo->
                    list.filter{parentItem->
                        shape.select.relations.all{relation->
                            vo[relation.rsKey] == parentItem[relation.parentRsKey]
                        }
                    }.forEach{parentItem->
                        @Suppress("UNCHECKED_CAST")
                        (parentItem[shape.to] as? MutableList<Any>)?.add(vo)
                    }
                }
            }
        }
    } ?: flow{}
}

suspend inline fun <FROM:VO, TO:VO> SelectP0<FROM, TO>.r2dbcSelect(client: DatabaseClient):Flow<TO> = _r2dbcSelect(client, None, None, None, None)
suspend inline fun <P1:VO, FROM:VO, TO:VO> SelectP1<P1, FROM, TO>.r2dbcSelect(client:DatabaseClient, p1:P1):Flow<TO> = _r2dbcSelect(client, p1, None, None, None)
suspend inline fun <P1:VO, P2:VO, FROM:VO, TO:VO> SelectP2<P1, P2, FROM, TO>.r2dbcSelect(client:DatabaseClient, p1:P1, p2:P2):Flow<TO> = _r2dbcSelect(client, p1, p2, None, None)
suspend inline fun <P1:VO, P2:VO, P3:VO, FROM:VO, TO:VO> SelectP3<P1, P2, P3, FROM, TO>.r2dbcSelect(client:DatabaseClient, p1:P1, p2:P2, p3:P3):Flow<TO> = _r2dbcSelect(client, p1, p2, p3, None)
suspend inline fun <P1:VO, P2:VO, P3:VO, P4:VO, FROM:VO, TO:VO> SelectP4<P1, P2, P3, P4, FROM, TO>.r2dbcSelect(client:DatabaseClient, p1:P1, p2:P2, p3:P3, p4:P4):Flow<TO> = _r2dbcSelect(client, p1, p2, p3, p4)
