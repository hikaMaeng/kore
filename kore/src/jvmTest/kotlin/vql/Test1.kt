@file:Suppress("PropertyName")

package vql

import kore.vo.VO
import kore.vo.field.list.stringList
import kore.vo.field.value.int
import kore.vo.field.value.string
import kore.vo.field.voList
import kore.vql.select
import kore.vql.sql.sql
import kotlin.test.Test

class CustomMap:Map<String, String> by mapOf()

class Test1 {
    class Member:VO(){
        companion object:()->Member by ::Member
        val rowid by int
        val name by string
    }
    class Club:VO(){
        companion object:()->Club by ::Club
        val rowid by int
        val name by string
    }
    class ClubMember: VO() {
        companion object:()->ClubMember by ::ClubMember
        val rowid by int
        val club_rowid by int
        val member_rowid by int
    }
    class Rs1:VO(){
        companion object:()->Rs1 by ::Rs1
        val member_rowid by int
        val club_rowid by int
        val member_name by string
        val club_name by string
    }
    @Test
    fun test1(){
        val query1 = Member.select(Rs1){ member, rs->
            val clubMember = ClubMember{::member_rowid} join member{::rowid}
            val club = Club{::rowid} join clubMember{::club_rowid}
            select{
                member{::rowid} put rs{::member_rowid}
                club{::rowid} put rs{::club_rowid}
                member{::name} put rs{::member_name}
                club{::name} put rs{::club_name}
                orderBy{
                    rs{::member_rowid}.asc
                    rs{::club_rowid}.asc
                }
            }
            where{
                club{::name} equal 3
                member{::name} equal club{::name}
                or
                club{::name} equal 3
                club{::name} in listOf("1","2","3")
                club{::name} notIn listOf("1","2","3")
            }
        }
        println(query1.sql())
    }
    class P1:VO(){
        companion object:()->P1 by ::P1
        var members by stringList
    }
    @Test
    fun test2() {
        val query1 = Member.select(Rs1, P1){from, rs, p1 ->
            select {
                p1{::members} put rs{::member_name}
            }
            where {
                from{::name} in p1{::members}
            }
        }
        println(query1.sql(P1().also { it.members = arrayListOf("1", "2", "3")}))
    }
    class T1:VO(){
        companion object:()->T1 by ::T1
        var t1_rowid by int
        var title by string
        var t2 by voList(::T2)
    }
    class T2:VO(){
        companion object:()->T2 by ::T2
        var t2_rowid by int
        var t1_rowid by int
        var title by string
    }
    @Test
    fun test3(){
        val query1 = T1.select(T1){from, rs->
            select{
                from{::t1_rowid}
                from{::title}

                T2.select(T2) {fromS1, rsS1->
                    val join1 = T1{::t1_rowid} join fromS1{::t1_rowid}
                    select {
                        fromS1 {::t2_rowid}
                        fromS1 {::title}
                    }
                    shape{
                        fromS1{::t1_rowid} in rs{::t1_rowid}
                    }
                } put rs{::t2}
            }
        }
        println(query1.sql())
    }
}