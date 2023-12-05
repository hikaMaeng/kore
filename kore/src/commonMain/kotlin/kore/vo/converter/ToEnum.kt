package kore.vo.converter

import kore.error.E

class ToEnum(val enums:Array<*>, val value:Any): E(value)