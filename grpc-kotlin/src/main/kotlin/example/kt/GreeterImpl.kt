package example.kt

import example.hello.GreeterGrpcKt
import example.hello.Hello
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import mu.KotlinLogging

class GreeterImpl : GreeterGrpcKt.GreeterCoroutineImplBase(Dispatchers.Unconfined) {
    override suspend fun hello(request: Hello.HelloRequest): Hello.HelloReply {
        log.info { "Hi, ${request.firstName}" }
        delay(100)
        return Hello.HelloReply.newBuilder()
            .setMessage("${request.message}, ${request.firstName} ${request.lastName}.")
            .build()
    }

    override suspend fun helloBlocking(request: Hello.HelloRequest): Hello.HelloReply {
        log.info { "Hi, ${request.firstName}" }

        withContext(Dispatchers.IO) {
            Thread.sleep(1000)
        }
        return Hello.HelloReply.newBuilder()
            .setMessage("${request.message}, ${request.firstName} ${request.lastName}.")
            .build()
    }

    companion object {
        private val log = KotlinLogging.logger {}
    }
}
