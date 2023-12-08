package koreSpring.tables

import kore.vo.VO
import kore.vo.field.value.int
import kore.vo.field.value.string

class Member: VO(){
    companion object:()->Member by ::Member
    val member_rowid by int
    val username by string
}

