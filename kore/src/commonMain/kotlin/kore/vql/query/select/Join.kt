package kore.vql.query.select

import kore.vo.VO

class Join(val a:()-> VO, val aProp:String, val bJoinIndex:Int, val bProp:String)