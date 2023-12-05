package kore.error

import kore.wrap.W
import kore.wrap.Wrap

@Suppress("NOTHING_TO_INLINE")
abstract class E(vararg items:Any):Throwable(){
    /** 예외메세지는 data를 join하여 얻어짐. 첫번째 원소가 구상클래스의 이름임 */
    private val data:ArrayList<Any> = arrayListOf(this::class.simpleName!!)
    init{
        /** 자식 클래스는 items를 생성자에서 넘겨서 추가적인 메세지에 참여함 */
        data.addAll(items)
    }
    /** 예외 메세지가 data를 join하여 처리됨*/
    override val message:String? get() = data.joinToString(" ")

    /** throw를 직접 하지 않고 terminate를 호출하여 처리할 수 있게 해줌*/
    inline fun terminate():Nothing = throw this


}