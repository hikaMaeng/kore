@file:Suppress("NOTHING_TO_INLINE")

package kore.vql.sql

import kore.vo.VO
import kore.vql.query.*
import kore.vql.query.select.Alias
import kore.vql.query.select.Case
import kore.vql.query.select.Item

class SelectSQLResult(val sql:String, val binds:ArrayList<String>, val tasks:List<SelectSQLResult>?){
    override fun toString(): String = "sql:\n$sql${tasks?.joinToString(",\n", "\ntasks: [\n", "\n]"){it.toString()} ?: ""}"
}
fun <FROM:VO, TO:VO, P1:VO, P2:VO, P3:VO, P4:VO> Select<FROM, TO>._sql(p1:P1, p2:P2, p3:P3, p4:P4):SelectSQLResult{
    init()
    val params: Array<VO> = arrayOf(p1, p2, p3, p4)
    val binds:ArrayList<String> = arrayListOf()
    val selectStr:String = items.foldIndexed(""){i, acc, item->
        acc + when(item){
            is Item.Field -> {
                val (alias, prop) = item.alias
                (if(i == 0) "" else ",") + "${alias.sqlName(this)}.$prop"
            }
            is Item.Param ->if(params.getOrNull(item.p.first) === None) "" else{
                binds.add("${item.p.first}:${item.p.second}")
                (if(i == 0) "" else ",") + ":${item.p.second}"
            }
            is Item.Shape->""
        }
    }
    val joinStr = if(joins.size == 1) "" else "\n" + joins.foldIndexed(""){i, acc, join->
        acc + if(i == 0) "" else (if(i == 1) "" else "\n") + "INNER JOIN ${join.a.instance::class.simpleName} j$i on j$i.${join.aProp} = j${join.bJoinIndex}.${join.bProp}"
    }
    val shapeStr:String? = _shape?.let {shapes->
        shapes.foldIndexed("") { i, acc, item ->
            val (alias, prop) = item.prop
            acc + (if(i == 0) "" else " and ") + "${alias.sqlName(this)}.$prop${item.op}(@@HOLDER:${item.to}@@)"
        }
    }
    val whereStr = _where?.let{
        "\nWHERE " + (shapeStr?.let{it + " and "}  ?: "") + it.foldIndexed(""){i, acc, case->
            acc + (if(i == 0) "" else " or ") + "(${case.items.foldIndexed(""){i2, acc2, item->
                acc2 + (if(i2 == 0) "" else " and ") + when(item){
                    is Case.Values -> {
                        val (alias, prop) = item.a
                        val index = joins.indexOf(alias.join)
                        "j$index.$prop${item.op}(${item.values.joinToString(","){if(it is String) "'$it'" else "it"}})"
                    }
                    is Case.Value -> {
                        val (alias, prop) = item.a
                        "${alias.sqlName(this)}.$prop${item.op}${if(item.value is String) "'${item.value}'" else "${item.value}"}"
                    }
                    is Case.Field -> {
                        val (aliasA, propA) = item.a
                        val (aliasB, propB) = item.b
                        "${aliasA.sqlName(this)}.$propA${item.op}${aliasB.sqlName(this)}.$propB"
                    }
                    is Case.Param ->{
                        val (alias: Alias<*>, propA: String) = item.a
                        binds.add("${item.b.first}:${item.b.second}")
                        "${alias.sqlName(this)}.$propA${item.op}:${item.b.second}"  
                    }
                }
            }})"
        }
    } ?: shapeStr?.let{"\nWHERE " + it}  ?: ""
    val orderStr = _orders?.let {
        "\nORDER BY " + it.joinToString(",") {order->
            items.find {
                when(it) {
                    is Item.Field -> it.to
                    is Item.Param -> ""
                    is Item.Shape -> ""
                } == order.prop
            }?.let {
                val joinAlias = when(it) {
                    is Item.Field -> it.alias.first.sqlName(this)
                    is Item.Param -> ""
                    is Item.Shape -> ""
                }
                "$joinAlias.${order.prop}${if(order.isAsc) "" else " desc"}"
            } ?: ""
        }
    } ?: ""
    return SelectSQLResult(
        "SELECT $selectStr FROM ${joins[0].a.instance::class.simpleName} j0$joinStr$whereStr$orderStr",
        binds,
        _serialTask?.map{
            it.select._sql(p1, p2, p3, p4)
        }
    )
}
object None:VO()
inline fun <FROM:VO, TO:VO> SelectP0<FROM, TO>.sql():SelectSQLResult = _sql(None, None, None, None)
inline fun <P1:VO, FROM:VO, TO:VO> SelectP1<P1, FROM, TO>.sql(p1:P1):SelectSQLResult = _sql(p1, None, None, None)
inline fun <P1:VO, P2:VO, FROM:VO, TO:VO> SelectP2<P1, P2, FROM, TO>.sql(p1:P1, p2:P2):SelectSQLResult = _sql(p1, p2, None, None)
inline fun <P1:VO, P2:VO, P3:VO, FROM:VO, TO:VO> SelectP3<P1, P2, P3, FROM, TO>.sql(p1:P1, p2:P2, p3:P3):SelectSQLResult = _sql(p1, p2, p3, None)
inline fun <P1:VO, P2:VO, P3:VO, P4:VO, FROM:VO, TO:VO> SelectP4<P1, P2, P3, P4, FROM, TO>.sql(p1:P1, p2:P2, p3:P3, p4:P4):SelectSQLResult = _sql(p1, p2, p3, p4)
