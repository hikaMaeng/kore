@file:Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE", "FunctionName")

package kore.vbp

import kore.vo.VO
import kore.vo.field
import kore.vo.field.*

internal class Parser<V:VO>(val vo:V){
    companion object{
        val EMPTY:ByteArray = byteArrayOf()
    }
    private data class Stack(val type:Any, val target:Any, var key:String = "", var field:Field<*>? = null)

    private var curr:Stack = Stack(vo, vo)
    private val stack:ArrayList<Stack> = arrayListOf(curr)

    private var state:Int = 100
    private var next:Int = 0
    private var s:ByteQueue = ByteQueue()

    operator fun invoke(input:ByteArray):Pair<ByteArray, V>?{
        if(input.isNotEmpty()) s += input
        return run()?.let{it to vo}
    }
    private inline fun run():ByteArray?{
        println("run ${s.joinToString(",")}")
        while(s.isNotEmpty()){
            when(state){
                100->{/** VO 필드 인식 1바이트 */
                    println("100 ${s.joinToString(",")}")
                    when(curr.target){
                        is VO->{
                            val index:Int = s.dropOne().toInt()
                            if(index == -1) state = 120 else { /** 120은 VO종료 */
                                vo.field(index).let{(k, f)->
                                    curr.key = k
                                    curr.field = f
                                    when(field) {
                                        is ListFields<*>-> {
                                            stack.add(Stack(field, vo[key] ?: field.defaultFactory()).also {curr = it})
                                            state = 1010
                                        }
                                        is MapFields<*>->state = 1020
                                        is VOField<*>->state = 1030
                                        is VOListField<*>->state = 1040
                                        is VOMapField<*>->state = 1050
                                        is VOSumField<*>->state = 1060
                                        is VOSumListField<*>->state = 1070
                                        is VOSumMapField<*>->state = 1080
                                        is EnumListField<*>->state = 1090
                                        is EnumMapField<*>->state = 1100
                                        else->
                                    }
                                }
                                next = 100
                                state = 110
                            }
                        }
                    }
                }
                110->{ /** VO 필드 값처리 */
                    println("110 ${s.joinToString(",")}, ${curr.key}")
                    val vo:VO = curr.target as VO

                    when(field){
                        is ListFields<*>->{
                            stack.add(Stack(field, vo[key] ?: field.defaultFactory()).also{curr = it})
                            state = 1010
                        }
                        is MapFields<*>->state = 1020
                        is VOField<*>->state = 1030
                        is VOListField<*>->state = 1040
                        is VOMapField<*>->state = 1050
                        is VOSumField<*>->state = 1060
                        is VOSumListField<*>->state = 1070
                        is VOSumMapField<*>->state = 1080
                        is EnumListField<*>->state = 1090
                        is EnumMapField<*>->state = 1100
                        else->VBP.parseValue(field::class, field, s)?.let{(v, arr)->
                            vo[key] = when(field){
                                is EnumField<*>->field.enums[v as Int]
                                else->v
                            }
                            state = 100
                            s = arr
                            if(stack.size == 1) return EMPTY
                        } ?: return null
                    }
                }
                120->{ /** VO 종료 */
                    if(stack.size == 1) state = 10000
                    else{
                        val prev:Stack = curr
                        curr = stack.removeAt(stack.size - 1)
                        when(curr.target){
                            is ListFields<*>->{
                                val list:MutableList<Any?> = curr.target as MutableList<Any?>
                                list.add(prev.target)
                                state = 200
                            }
                            is MapFields<*>->{
                                val map:MutableMap<String, Any?> = curr.target as MutableMap<String, Any?>
                                map[curr.key] = prev.target
                                state = 300
                            }
                            is VO->{
                                val vo:VO = curr.target as VO
                                vo[curr.key] = prev.target
                                state = 100
                            }
                        }
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