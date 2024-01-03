package vo

import kore.vo.VO
import kore.vo.field.list.intList
import kore.vo.field.list.stringList
import kore.vo.field.value.string
import kore.vo.field.voList
import kore.vosn.fromVOSN
import kore.vosn.toVOSN
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

class EntityList {
    class Ent: VO() {
        var list0 by voList(::Item){default{mutableListOf()}}
        var list by voList(::Item)

        class Item(v:String=""): VO() {
            val value by string{default(v)}
        }
    }

    @Test
    fun emptyEntityList() {
        val t = Ent().also {
            it.list = mutableListOf(Ent.Item("Test1"),Ent.Item("Test2"))
        }
        val s = t.toVOSN()
        val t2 = Ent().fromVOSN(s()!!)
        val s2 = t2()!!.toVOSN()
        assertEquals(s,s2,"원래 인코딩과 디코딩 후 인코딩한게 다름")
    }


    class Ent2: VO() {
        val sList1 by stringList{default((mutableListOf("","")))}  // 빈 문자열이 2개 들어있는 문자열 리스트: [|@]
        val sList2 by stringList{default((mutableListOf(" ","")))} // 공백, 빈문자열 리스트: [ |@]
        val sList3 by stringList{default((mutableListOf(""," ")))} // 빈문자열, 공백 리스트: [| @]
        val sList4 by stringList{default((mutableListOf(" ")))} // 공백이 하나 들어있는 문자열 리스트: [ @]
        val sList5 by stringList{default((mutableListOf()))} // 문자열이 안 들어있는 문자열 리스트: [@]
        val sList6 by stringList{default((mutableListOf("")))} // 빈 문자열이 하나 들어있는 문자열 리스트: [@]
    }
    class Ent3: VO() {
        val iList1 by intList{default(mutableListOf())}
        val iList2 by intList{default(mutableListOf(1,2,3,4,5))}
    }
    class EntNone:VO(){
        var none by string{ default("") }
        var none2 by string{ default("") }
    }
    @Test
    /**주석에는 문자열이 안 들어있는 문자열 리스트는 [@] 변환된다고 써져있음
     * 실제로는 [!]로 변환됨*/
    fun entityStringListWithEmptyEntity() {
        val t = Ent2()
        val s = t.toVOSN()
        val t2 = Ent2().fromVOSN(s()!!)
        val s2 = t2()!!.toVOSN()
        assertEquals(s.toString().split("=")[1].split(")")[0], "|@| |@|| @| @||@|", "문자열이 안들어있는 문자열 리스트")
        assertEquals(s,s2,"원래 인코딩과 디코딩 후 인코딩한게 다름")
    }
    @Test
    fun entityIntListWithEmptyEntity() {
        val t = Ent3()
        val s = t.toVOSN()
        val t2 = Ent3().fromVOSN(s()!!)
        val s2 = t2()!!.toVOSN()
        println(s)
        assertEquals(s.toString().split("=")[1].split(")")[0], "@|1|2|3|4|5@|", "다름")
        assertEquals(s,s2,"원래 인코딩과 디코딩 후 인코딩한게 다름")
    }
    @Test
    fun entityEmptyList() {
        val t = EntNone()
        val s = t.toVOSN()
        val t2 = EntNone().fromVOSN(s()!!)
        val s2 = t2()!!.toVOSN()
        println(s)
        assertEquals(s.toString().split("=")[1].split(")")[0], "||", "다름")
        assertEquals(s,s2,"원래 인코딩과 디코딩 후 인코딩한게 다름")
    }

}
