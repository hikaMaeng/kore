import kore.json.VOJson
import kore.vo.VO
import kore.vo.field.value.int
import kore.vo.field.value.string
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion

class Test1: VO(){
    var a by string
    var b by int
}
suspend fun main() {
    val t1 = Test1().also {
        it.a = "hika"
        it.b = 3
    }
    var builder = StringBuilder()
    VOJson.to(t1)
        .catch {
            console.log("error", it.toString())
        }
        .onCompletion {
            console.log("complete", builder.toString())
        }
        .collect{
            builder.append(it)
            console.log("collect", it)
        }
//    try {
//        console.log("aaa", t1.toVOSN()())
//    }catch (e:Throwable){
//        console.log("error",e)
//    }
}

