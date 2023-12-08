package koreSpring.tables

import kore.vo.VO
import kore.vo.field.value.int

class ClubMember: VO(){
    companion object:()-> ClubMember by ::ClubMember
    val clubmember_rowid by int
    val club_rowid by int
    val member_rowid by int
}