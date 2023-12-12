package kore.wrap

fun interface Thunk<out VALUE:Any>{
    operator fun invoke():VALUE
}