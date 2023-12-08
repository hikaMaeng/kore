package koreSpring.recordset

import kore.vo.VO
import kore.vo.field.value.int
import kore.vo.field.value.string
import kore.vo.field.voList
import kore.vo.field.voListDefault
import koreSpring.tables.Club

class RSmemberClub: VO(){
    companion object:()-> RSmemberClub by ::RSmemberClub
    val member_rowid by int
    val username by string
    val userclub by voList(Club){
        default(::arrayListOf)
    }
}