package kore.vo.converter

import kore.error.E
import kore.vo.VOSum

class ToInvalidSum(val sum: VOSum<*>, val it:Any): E(sum, it)