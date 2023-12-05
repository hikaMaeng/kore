package kore.vql.sql

import kore.vo.VO
import kore.vql.query.*
import kore.vql.query.select.Case
import kore.vql.query.select.Item

class SelectSQLResult(val sql:String, val tasks:List<SelectSQLResult>?){
    override fun toString(): String = "sql:\n$sql${tasks?.joinToString(",\n", "\ntasks: [\n", "\n]"){it.toString()} ?: ""}"
}
@PublishedApi internal fun <FROM:VO, TO:VO, P1:VO, P2:VO, P3:VO, P4:VO> sql(select:Select<FROM, TO>, p1:P1, p2:P2, p3:P3, p4:P4):SelectSQLResult{
    select.init()
    val selectStr:String = select.items.foldIndexed(""){i, acc, item->
        acc + when(item){
            is Item.Field -> {
                val (alias, prop) = item.alias
                val index = alias.index(select.joins)
                (if(i == 0) "" else ",") + "j$index.$prop"
            }
            is Item.Param -> (if(i == 0) "" else ",") + when(item.p.first){
                0 -> "${p1.values[item.p.second]}"
                1 -> "${p2.values[item.p.second]}"
                2 -> "${p3.values[item.p.second]}"
                3 -> "${p4.values[item.p.second]}"
                else -> ""
            }
            is Item.Shape->""
        }
    }
    val joinStr = if(select.joins.size == 1) "" else "\n" + select.joins.foldIndexed(""){i, acc, join->
        acc + if(i == 0) "" else (if(i == 1) "" else "\n") + "INNER JOIN ${join.a.instance::class.simpleName} j$i on j$i.${join.aProp} = j${join.bJoinIndex}.${join.bProp}"
    }
    val shapeStr:String? = select._shape?.let {shapes->
        shapes.foldIndexed("") { i, acc, item ->
            val (alias, prop) = item.prop
            val index = select.joins.indexOf(alias.join)
            acc + (if(i == 0) "" else " and ") + "j$index.$prop${item.op}(@@HOLDER:${item.to}@@)"
        }
    }
    val whereStr = select._where?.let{
        "\nWHERE " + (shapeStr?.let{it + " and "}  ?: "") + it.foldIndexed(""){i, acc, case->
            acc + (if(i == 0) "" else " or ") + "(${case.items.foldIndexed(""){i2, acc2, item->
                acc2 + (if(i2 == 0) "" else " and ") + when(item){
                    is Case.Values -> {
                        val (alias, prop) = item.a
                        val index = select.joins.indexOf(alias.join)
                        "j$index.$prop${item.op}(${item.values.joinToString(","){if(it is String) "'$it'" else "it"}})"
                    }
                    is Case.Value -> {
                        val (alias, prop) = item.a
                        val index = select.joins.indexOf(alias.join)
                        "j$index.$prop${item.op}${if(item.value is String) "'${item.value}'" else "${item.value}"}"
                    }
                    is Case.Field -> {
                        val (aliasA, propA) = item.a
                        val indexA = select.joins.indexOf(aliasA.join)
                        val (aliasB, propB) = item.b
                        val indexB = select.joins.indexOf(aliasB.join)
                        "j$indexA.$propA${item.op}j$indexB.$propB"
                    }
                    is Case.Param ->{
                        val (aliasA, propA) = item.a
                        val indexA = select.joins.indexOf(aliasA.join)
                        val v = when(item.b.first){
                            0 -> p1.values[item.b.second]
                            1 -> p2.values[item.b.second]
                            2 -> p3.values[item.b.second]
                            3 -> p4.values[item.b.second]
                            else -> ""
                        }
                        "j$indexA.$propA${item.op}${when(v){
                            is String -> "'$v'"
                            is List<*> -> v.joinToString(",", "(", ")"){if(it is String) "'$it'" else "$it"}
                            else -> "$v"
                        }}"  
                    }
                }
            }})"
        }
    } ?: shapeStr?.let{"\nWHERE " + it}  ?: ""
    val orderStr = select._orders?.let {
        "\nORDER BY " + it.joinToString(",") {order->
            select.items.find {
                when(it) {
                    is Item.Field -> it.to
                    is Item.Param -> ""
                    is Item.Shape -> ""
                } == order.prop
            }?.let {
                val index = when(it) {
                    is Item.Field -> it.alias.first.index(select.joins)
                    is Item.Param -> 0
                    is Item.Shape -> 0
                }
                "j$index.${order.prop}${if(order.isAsc) "" else " desc"}"
            } ?: ""
        }
    } ?: ""
    return SelectSQLResult(
        "SELECT $selectStr \nFROM ${select.joins[0].a.instance::class.simpleName} j0$joinStr$whereStr$orderStr",
        select._serialTask?.map{
            sql(it.select, p1, p2, p3, p4)
        }
    )
}

inline fun <FROM:VO, reified TO:VO> SelectP0<FROM, TO>.sql():SelectSQLResult = sql(this, to.instance, to.instance, to.instance, to.instance)
inline fun <P1:VO, FROM:VO, reified TO:VO> SelectP1<P1, FROM, TO>.sql(p1:P1):SelectSQLResult = sql(this, p1, p1, p1, p1)
inline fun <P1:VO, P2:VO, FROM:VO, reified TO:VO> SelectP2<P1, P2, FROM, TO>.sql(p1:P1, p2:P2):SelectSQLResult = sql(this, p1, p2, p2, p2)
inline fun <P1:VO, P2:VO, P3:VO, FROM:VO, reified TO:VO> SelectP3<P1, P2, P3, FROM, TO>.sql(p1:P1, p2:P2, p3:P3):SelectSQLResult = sql(this, p1, p2, p3, p3)
inline fun <P1:VO, P2:VO, P3:VO, P4:VO, FROM:VO, reified TO:VO> SelectP4<P1, P2, P3, P4, FROM, TO>.sql(p1:P1, p2:P2, p3:P3, p4:P4):SelectSQLResult = sql(this, p1, p2, p3, p4)
