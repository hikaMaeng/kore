package kore.vo.converter

import kore.error.E
import kore.vo.VO

class ToNoConverter(val data:VO,val value:Any): E(data, value)