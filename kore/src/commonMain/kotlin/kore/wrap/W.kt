@file:Suppress("NOTHING_TO_INLINE")
package kore.wrap

/**
 * 성공, 실패를 내포하는 결과값 보고 객체. 최초 값을 람다로 설정하면 이후 모든 map연산이 지연연산으로 처리됨
 */
object W{
    /** 예외 발생이 가능한 블록을 실행하고 그 결과에 따라 Wrap생성 */
    inline fun <VALUE:Any>catch(throwableBlock:()->VALUE): Wrap<VALUE> = try {
        Wrap(throwableBlock())
    }catch(e:Throwable){
        Wrap(e)
    }
    /** 정상인 값을 생성함 */
    inline operator fun <VALUE:Any>invoke(value:VALUE): Wrap<VALUE> = Wrap(value)
    /** 정상인 값을 람다로 생성함. 이후 모든 처리는 지연으로 처리되고 invoke까지 평가가 미뤄짐 */
    inline operator fun <VALUE:Any>invoke(block:Thunk<VALUE>): Wrap<VALUE> = Wrap(block)
    /** 실패인 값을 예외로 생성함. 반드시 타입파라메터를 지정해야 함*/
    inline operator fun <VALUE:Any>invoke(value:Throwable): Wrap<VALUE> = Wrap(value)

    @PublishedApi internal val END = invoke<Nothing>(Throwable("END"))
    inline fun <VALUE:Any>end():Wrap<VALUE> = END
    inline fun isEnd(target:Wrap<*>):Boolean = END == target
    inline fun <VALUE:Any>emptyList():Wrap<WList<VALUE>> = END
}
inline fun <VALUE:Any> Throwable.wrap():Wrap<VALUE> = W(this)