package kore.vo.converter

import kore.error.E
import kore.vo.VO

class ToNullField(val data:VO,val value:Any): E(data, value)