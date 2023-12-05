package kore.vo.converter

import kore.error.E

class ToNoEnum(val enums:Array<*>, val value:Any): E(enums, value)