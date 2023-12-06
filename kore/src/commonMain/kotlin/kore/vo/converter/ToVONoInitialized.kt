package kore.vo.converter

import kore.error.E
import kore.vo.VO

class ToVONoInitialized(val data: VO, key:String = ""): E(data, key)