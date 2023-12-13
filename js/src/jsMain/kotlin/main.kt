import kore.vo.VO
import kore.vo.field.value.int
import kore.vo.field.value.string
import kore.vosn.toVOSN

class Test1: VO(){
    var a by string
    var b by int
}
fun main() {
    val t1 = Test1().also {
        it.a = "hika"
        it.b = 3
    }
    try {
        console.log("aaa", t1.toVOSN()())
    }catch (e:Throwable){
        console.log("error",e)
    }
}

