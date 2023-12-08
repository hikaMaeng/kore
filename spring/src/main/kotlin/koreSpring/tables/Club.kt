package koreSpring.tables

import kore.vo.VO
import kore.vo.field.value.int
import kore.vo.field.value.string

class Club: VO(){
    companion object:()-> Club by ::Club
    val club_rowid by int
    val title by string
}