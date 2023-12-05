package kore.wrap

fun interface Thunk<out VALUE:Any>:()->VALUE