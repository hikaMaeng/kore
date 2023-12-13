import kore.vo.VO
import kore.vo.field.value.int
import kore.vo.field.value.string

class Test:VO(){
    var a by int
    var b by string

}



fun main(){
    console.log(Test().also { it.a=1;it.b="2" })
}