@file:Suppress("UNCHECKED_CAST")

package kore.vql.query

import kore.vo.VO

@PublishedApi internal val queryVOStore:HashMap<()->VO, VO> = hashMapOf()
@PublishedApi internal inline val <V:VO> (()->V).instance:V get() = queryVOStore.getOrPut(this, this) as V

sealed interface Query