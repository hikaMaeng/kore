package kore.vo.converter

import kore.vo.VO
import kore.wrap.Wrap


interface Converter<RESULT:Any>{
    fun to(vo:VO):Wrap<RESULT>
    fun <V:VO>from(vo:V, value:RESULT):Wrap<V>
}