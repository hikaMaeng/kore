@file:Suppress("NOTHING_TO_INLINE", "UNCHECKED_CAST")

package kore.vo.field

interface Field<VALUE:Any>{
    fun defaultFactory():VALUE
}
interface ListFields<VALUE:Any>:Field<MutableList<VALUE>>
interface MapFields<VALUE:Any>:Field<MutableMap<String, VALUE>>
