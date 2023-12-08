@file:Suppress("NOTHING_TO_INLINE", "FunctionName", "SqlSourceToSinkFlow")

package kore.vql.sql

import kore.vo.VO
import kore.vosn.toVOSN
import kore.vql.query.*
import kore.vql.query.select.Item
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.bind
import org.springframework.r2dbc.core.flow

@PublishedApi internal suspend fun <FROM:VO, TO:VO, P1:VO, P2:VO, P3:VO, P4:VO> Select<FROM, TO>._r2dbcSelect(client:DatabaseClient, p1:P1, p2:P2, p3:P3, p4:P4, parent:List<Any>? = null):Flow<TO>{
    val result = _sql(p1, p2, p3, p4)
    return client.sql(result.sql).let {
        result.binds.fold(
            if(parent == null) it else it.bind("__PARENT__", parent.toTypedArray())
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
            acc
        }
    }?.fetch()?.flow()?.map{rs->
        to().also{to->rs.forEach{(k, v) ->to[result.map[k]!!] = v}}
    }?.let{flow->
        val shapes = items.filterIsInstance<Item.Shape>()
        if(shapes.isEmpty()) flow else{
            val list:ArrayList<TO> = arrayListOf()
            flow.onCompletion {err->
                shapes.forEach {item->
                    item.select._r2dbcSelect(client, p1, p2, p3, p4, list.map{it.props[item.to]!!}).collect{vo->
                        list.filter{
                            vo.props["a"] == it.props[item.to]
                        }.forEach {
                            @Suppress("UNCHECKED_CAST")
                            (it.props[item.to] as? ArrayList<Any>)?.add(vo)
                        }
                    }
                }
            }.collect(list::add)
            flow{}
        }
    } ?: flow{}
}

suspend inline fun <FROM:VO, TO:VO> SelectP0<FROM, TO>.r2dbcSelect(client: DatabaseClient):Flow<TO> = _r2dbcSelect(client, None, None, None, None)
suspend inline fun <P1:VO, FROM:VO, TO:VO> SelectP1<P1, FROM, TO>.r2dbcSelect(client:DatabaseClient, p1:P1):Flow<TO> = _r2dbcSelect(client, p1, None, None, None)
suspend inline fun <P1:VO, P2:VO, FROM:VO, TO:VO> SelectP2<P1, P2, FROM, TO>.r2dbcSelect(client:DatabaseClient, p1:P1, p2:P2):Flow<TO> = _r2dbcSelect(client, p1, p2, None, None)
suspend inline fun <P1:VO, P2:VO, P3:VO, FROM:VO, TO:VO> SelectP3<P1, P2, P3, FROM, TO>.r2dbcSelect(client:DatabaseClient, p1:P1, p2:P2, p3:P3):Flow<TO> = _r2dbcSelect(client, p1, p2, p3, None)
suspend inline fun <P1:VO, P2:VO, P3:VO, P4:VO, FROM:VO, TO:VO> SelectP4<P1, P2, P3, P4, FROM, TO>.r2dbcSelect(client:DatabaseClient, p1:P1, p2:P2, p3:P3, p4:P4):Flow<TO> = _r2dbcSelect(client, p1, p2, p3, p4)
