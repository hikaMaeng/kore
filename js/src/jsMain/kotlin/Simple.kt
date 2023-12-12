import kore.vo.VO
import kore.vo.field.value.int
import kore.vo.field.value.string
import kore.vosn.toVOSN
import kotlinx.browser.document
import kotlinx.dom.appendText

class Test1: VO(){
    var a by string
    var b by int
}
fun main() {
    Test1().also {
        it.a = "hika"
        it.b = 3
    }.let{
        document.body?.appendText(it.toVOSN()()!!)
    }
}

