import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import mqtt.Broker

actual fun currentTimeMillis(): Long {
    return System.currentTimeMillis()
}

actual fun runCoroutine(block: suspend CoroutineScope.() -> Unit) {
    runBlocking {
        block()
    }
}

fun main() {
    Broker().listen()
}