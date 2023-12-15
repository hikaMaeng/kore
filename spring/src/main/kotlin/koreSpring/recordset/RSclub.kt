package koreSpring.recordset

import kore.vo.VO
import kore.vo.field.value.int
import kore.vo.field.value.string

class RSclub: VO(){
    companion object:()-> RSclub by ::RSclub
    val club_rowid by int
    val title by string
    val member_rowid by int
}