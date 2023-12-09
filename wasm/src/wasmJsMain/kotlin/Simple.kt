import kore.wrap.Wrap
import kotlinx.browser.document
import kotlinx.dom.appendText

fun main() {
    document.body?.appendText("Hello, ${greet()}!,. ${enumValues<Test>()}")
}

fun greet() = "world"

enum class Test{
    a, b,c
}