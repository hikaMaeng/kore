package kore.vo.field

import kore.vo.VO
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadWriteProperty

typealias Prop<T> = PropertyDelegateProvider<VO, ReadWriteProperty<VO, T>>