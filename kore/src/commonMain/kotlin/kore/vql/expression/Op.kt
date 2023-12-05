package kore.vql.expression

enum class Op {
    Not{override fun toString():String = "<>"},
    Equal{override fun toString():String = "="},
    Less{override fun toString():String = "<="},
    Greater{override fun toString():String = ">="},
    Under{override fun toString():String = "<"},
    Over{override fun toString():String = ">"},
    In{override fun toString():String = " IN "},
    NotIn{override fun toString():String = " NOT IN "},
}