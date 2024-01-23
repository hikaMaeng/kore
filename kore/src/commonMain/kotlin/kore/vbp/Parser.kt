@file:Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE", "FunctionName")

package kore.vbp

import kore.vo.VO
import kore.vo.VOSum
import kore.vo.field
import kore.vo.field.*

internal class Parser<V:VO>(val vo:V){
    companion object{
        val EMPTY:ByteArray = byteArrayOf()
    }
    private data class Stack(val type:Any, val target:Any, var field:Field<*>? = null, var key:String = "", var size:Int = 0)

    private var curr:Stack = Stack(VOField::class, vo)
    private val stack:ArrayList<Stack> = arrayListOf(curr)

    private var state:Int = 100
    private var next:Int = 0
    private var valueNext:Int = 0
    private var mapValue:Int = 0
    private var que:ByteQueue = ByteQueue()

    operator fun invoke(input:ByteArray):Pair<ByteArray, V>?{
        if(input.isNotEmpty()) que += input
        return run()?.let{it to vo}
    }
    private inline fun run():ByteArray?{
        println("run ${que.joinToString(",")}")
        while(que.isNotEmpty()){
            when(state){
                1000->{ //size계산
                    VBP.parseValue(Int::class, Int::class, que)?.let{v->
                        println("1000 ${curr.target}, $v")
                        curr.size = v as Int
                        state = next
                    } ?: return null
                }
                2000->{ //value처리
                    println("2000 ${curr.target}, $next, ${que.joinToString(",")}")
                    VBP.parseValue(curr.type, curr.field!!, que)?.let{v->
                        when(curr.target){
                            is List<*>->(curr.target as MutableList<Any>).add(v)
                            is Map<*, *>->(curr.target as MutableMap<String, Any>)[curr.key] = v
                            is VO->(curr.target as VO)[curr.key] = v
                            else->throw Throwable("invalid target ${curr.target}")
                        }
                        curr.size--
                        if(curr.size == 0) state = 1100 else state = next
                    } ?: return null
                }
                1020->{ //map 키
                    println("1020 ${curr.target}, $mapValue")
                    if(curr.size == 0) state = 1100 else {
                        VBP.parseValue(String::class, String::class, que)?.let{v->
                            curr.key = v as String
                            state = mapValue
                        } ?: return null
                    }
                }
                1100->{ //vo로 돌아감
                    println("1100 ${curr.target}")
                    if(stack.size == 1) state = 10000
                    else{
                        val prev:Stack = curr
                        stack.removeLast()
                        curr = stack.last()
                        println("1100-1 ${curr.target}, ${prev.target}")
                        when(curr.target){
                            is List<*>->{
                                val list:MutableList<Any?> = curr.target as MutableList<Any?>
                                list.add(prev.target)
                                state = 100
                            }
                            is Map<*, *>->{
                                val map:MutableMap<String, Any?> = curr.target as MutableMap<String, Any?>
                                map[curr.key] = prev.target
                                state = 100
                            }
                            is VO->{
                                val vo:VO = curr.target as VO
                                vo[curr.key] = prev.target
                                state = 100
                            }
                            else->throw Throwable("invalid target ${curr.target}")
                        }
                    }
                }
                90->{/** volist용 VO생성 */
                    println("90 ${curr.size}-${que.joinToString(",")}")
                    state = if(curr.size == 0) 1100 else{
                        curr.size--
                        when(val f = curr.field){
                            is VOMapField<*>->stack.add(Stack(f::class, f.factory(), f).also{curr = it})
                            is VOListField<*>->stack.add(Stack(f::class, f.factory(), f).also{curr = it})
                            else->throw Throwable("invalid field $f")
                        }
                        100
                    }
                }
                100->{/** VO 필드 인식 1바이트 */
                    println("100 ${curr.target}, ${que.joinToString(",")}")
                    val vo:VO = curr.target as VO
                    val index:Int = que.dropOne().toInt()
                    if(index == -1) state = 120 else { /** 120은 VO종료 */
                        vo.field(index).let{(k, f)->
                            println("100 || $k, $f")
                            curr.key = k
                            curr.field = f
                            when(f){
                                is ListFields<*>, is EnumListField<*>-> {
                                    stack.add(Stack(f::class, vo[k] ?: f.defaultFactory(), f).also{curr = it})
                                    next = 2000
                                    state = 1000
                                }
                                is MapFields<*>, is EnumMapField<*>->{
                                    stack.add(Stack(f::class, vo[k] ?: f.defaultFactory(), f).also{curr = it})
                                    next = 1020
                                    mapValue = 2000
                                    state = 1000
                                }
                                is VOField<*>->{
                                    println("VOField ${f::class} -----------------------------------------------")
                                    stack.add(Stack(f::class, vo[k] ?: f.factory(), f).also{curr = it})
                                }
                                is VOListField<*>->{
                                    println("VOListField ${f::class} -----------------------------------------------")
                                    stack.add(Stack(f::class, vo[k] ?: f.defaultFactory(), f).also{curr = it})
                                    next = 90
                                    state = 1000
                                }
                                is VOMapField<*>->{
                                    println("voMapField ${f::class} -----------------------------------------------")
                                    stack.add(Stack(f::class, vo[k] ?: f.defaultFactory(), f).also{curr = it})
                                    next = 1020
                                    mapValue = 90
                                    state = 1000
                                }
                                is VOSumField<*>->{
                                    println("VOSumField ${f::class} -----------------------------------------------")
                                    curr.size = 1
                                    state = 200
                                }
                                is VOSumListField<*>->{
                                    println("VOSumListField ${f::class} -----------------------------------------------")
                                    stack.add(Stack(f::class, vo[k] ?: f.defaultFactory(), f).also{curr = it})
                                    next = 200
                                    state = 1000
                                }
                                is VOSumMapField<*>->{
                                    println("VOSumMapField ${f::class} -----------------------------------------------")
                                    stack.add(Stack(f::class, vo[k] ?: f.defaultFactory(), f).also{curr = it})
                                    next = 1020
                                    mapValue = 200
                                    state = 1000
                                }
                                else->{
                                    curr.field = f
                                    state = 110
                                }
                            }
                        }
                    }
                }
                110->{ /** vo 값필드 처리 */
                    println("110 ${curr.target}, ${curr.field}, ${que.joinToString(",")}")
                    val f = curr.field!!
                    VBP.parseValue(f::class, f, que)?.let{v->
                        val vo:VO = curr.target as VO
                        vo[curr.key] = v
                        state = 100
                    } ?: return null
                }
                120->{ /** VO 종료 */
                    if(stack.size == 1) state = 10000
                    else{
                        val prev:Stack = curr
                        stack.removeLast()
                        curr = stack.last()
                        println("120-0 ${curr.target}")
                        when(curr.target){
                            is List<*>->{
                                val list:MutableList<Any?> = curr.target as MutableList<Any?>
                                list.add(prev.target)
                                println("120-1 ${list.joinToString(",")}, ${curr.field}")
                                state = if(curr.field is VOSumListField<*>) 200 else 90
                            }
                            is Map<*, *>->{
                                val map:MutableMap<String, Any?> = curr.target as MutableMap<String, Any?>
                                map[curr.key] = prev.target
                                println("120-2 ${curr.key}, ${prev.target}, $map")
                                state = 1020
                            }
                            is VO->{
                                val vo:VO = curr.target as VO
                                vo[curr.key] = prev.target
                                state = 100
                            }
                        }
                    }
                }
                200->{ /** Sum타입의 구상타입 인덱스 확인*/
                    println("200 ${curr.target}, ${que.joinToString(",")}")
                    state = if(curr.size == 0) 1100 else {
                        curr.size--
                        val index:Int = que.dropOne().toInt()
                        val f:Field<*> = curr.field!!
                        val sum:VOSum<VO> = when(f) {
                            is VOSumField<*>->f.sum
                            is VOSumListField<*>->f.sum
                            is VOSumMapField<*>->f.sum
                            else->throw Throwable("invalid sum field $f")
                        }
                        println("200-1 $index, $f, ${que.joinToString(",")}")
                        sum.factories[index].let {factory->
                            println("200-2 $factory")
                            stack.add(Stack(factory::class, factory(), f).also {curr = it})
                        }
                        100
                    }
                }
                10000->{ /** 종료 */
                    return null
                }
            }
        }
        return null
    }
}