@file:Suppress("NOTHING_TO_INLINE", "UNCHECKED_CAST")

package kore.wrap

import kotlin.jvm.JvmInline

@JvmInline
value class Wrap<out VALUE:Any> @PublishedApi internal constructor(@PublishedApi internal val value:Any){

    /** R타입을 유지한 상태로 내부의 상태를 바꾸는 연산. 지연연산 모드에서는 계속 지연함수합성이 됨.
     *  map에 전달되는 람다는 throw할 수 있으며 이를 통해 fail상태로 이전시킬 수 있음.
     */
    inline fun <OTHER:Any> map(crossinline block:(VALUE)->OTHER):Wrap<OTHER> = when(value){
        is Throwable -> this as Wrap<OTHER>
        is Thunk<*> -> Wrap(Thunk{
            val v = value.invoke()
            if(v is Throwable) v else block(v as VALUE)
        })
        else -> Wrap(block(value as VALUE))
    }
    /** 최초의 값을 람다로 지정하지 않아도 이후 지연연산하게 변경함*/
    inline fun <OTHER:Any> mapLazy(crossinline block:(VALUE)->OTHER):Wrap<OTHER> = when(value){
        is Throwable -> this as Wrap<OTHER>
        is Thunk<*> -> Wrap(Thunk{
            val v = value.invoke()
            if(v is Throwable) v else block(v as VALUE)
        })
        else -> Wrap(Thunk{block(value as VALUE)})
    }
    inline fun <OTHER:Any, ORIGIN:Any> List<ORIGIN>.flatMapList( block:(ORIGIN)->Wrap<OTHER>):Wrap<List<OTHER>>{
        return W(fold(ArrayList(size)){ acc, it->
            block(it).isEffected{acc.add(it)}?.let {return W(it)}
            acc
        })
    }
    inline fun <OTHER:Any> List<String>.flatMapListToMap( block:(key:String, value:String)->Wrap<OTHER>):Wrap<HashMap<String,OTHER>>{
        val result:HashMap<String, OTHER> = hashMapOf()
        var key:String? = null
        var i = 0
        while(i < size){
            val it = get(i)
            if(key == null) key = it
            else{
                block(key, it).isEffected{result[key!!] = it}?.let {return W(it)}
                key = null
            }
            i++
        }
        return W(result)
    }
    /** map내에서 오류발생 가능성이 있다면 flatMap을 사용해야 함. */
    inline fun <OTHER:Any> flatMap(crossinline block:Wrap<VALUE>.(VALUE)->Wrap<OTHER>):Wrap<OTHER> = when(value){
        is Throwable -> this as Wrap<OTHER>
        is Thunk<*> ->{
            val v = value.invoke()
            if(v is Throwable) W(v) else block(v as VALUE)
        }
        else -> block(value as VALUE)
    }
    /** flatMap이 무조건 지연평가됨. */
    inline fun <OTHER:Any> flatMapLazy(crossinline block:Wrap<VALUE>.(VALUE)->Wrap<OTHER>):Wrap<OTHER> = when(value){
        is Throwable -> this as Wrap<OTHER>
        is Thunk<*> -> Wrap(Thunk{
            val v = value.invoke()
            if(v is Throwable) v else block(v as VALUE).value
        })
        else -> Wrap(Thunk{block(value as VALUE).value})
    }
    /** 실패값을 반드시 복원할 수 있는 정책이 있는 경우 복원용 람다를 통해 현재 상태를 나타내는 예외로부터 값을 만들어냄. 지연연산이 해소됨 */
    inline operator fun invoke(block:(Throwable)-> @UnsafeVariance VALUE):VALUE = when(value) {
        is Throwable -> block(value)
        is Thunk<*> ->{
            val v = value.invoke()
            if(v is Throwable) block(v) else v
        }
        else -> value
    } as VALUE
    /** 정상인 값은 반환되지만 비정상인 값은 null이 됨. 지연연산 설정 시 이 시점에 해소됨*/
    inline operator fun invoke():VALUE? = when(value){
        is Throwable -> null
        is Thunk<*> ->{
            val v = value.invoke()
            if(v is Throwable) null else v as VALUE
        }
        else -> value as VALUE
    }
    /** 정상값 Wrap을 얻거나 null을 얻음. 지연연산이 해소된 새로운 Wrap을 얻음*/
    inline val ok:Wrap<VALUE>? get() = invoke()?.let{W(it)}
    /** 오류값 Wrap을 얻거나 null을 얻음. 지연연산이 해소된 새로운 Wrap을 얻음*/
    inline val fail:Wrap<VALUE>? get() = when(value){
        is Throwable -> this
        is Thunk<*> ->{
            val v = value.invoke()
            if(v is Throwable) W(v) else null
        }
        else -> null
    }
    /** value를 얻어 사이드이펙트만 처리한 뒤 자신을 반환함. 지연연산이 해소된 Wrap을 얻음*/
    inline fun effect(block:(VALUE)->Unit):Wrap<VALUE>{
        block(when(value){
            is Throwable -> return this
            is Thunk<*> ->{
                val v = value.invoke()
                if(v is Throwable) return W(v)
                v
            }
            else -> value
        } as VALUE)
        return this
    }
    inline fun getOrFailEffect(block:(Throwable)->Nothing):VALUE = when(value){
        is Throwable -> block(value)
        is Thunk<*> ->{
            val v = value.invoke()
            if(v is Throwable) block(v) else v
        }
        else -> value
    } as VALUE
    inline fun failEffect(block:(Throwable)->Unit):Wrap<VALUE>{
        when(value){
            is Throwable -> block(value)
            is Thunk<*> ->{
                val v = value.invoke()
                if(v is Throwable) block(v)
            }
        }
        return this
    }
    /** 부수효과가 실행되었다면 null 아니면 Throwable을 반환함. 지연연산이 해소됨 */
    inline fun isEffected(block:(VALUE)->Unit= {}):Throwable?{
        block(when(value){
            is Throwable -> return value
            is Thunk<*> ->{
                val v = value.invoke()
                if(v is Throwable) return v
                v
            }
            else -> value
        } as VALUE)
        return null
    }
}
inline fun <ITEM:Any, OTHER:Any, RESULT:Any> Wrap<ITEM>.map2(other:Wrap<OTHER>, block:(ITEM, OTHER)->RESULT):Wrap<RESULT>{
    this.isEffected{a->
        other.isEffected{b->
            return W(block(a,b))
        }?.let{
            return W(it)
        }
    }?.let{
        return W(it)
    }
    return W.end()
}