package example.kt

import example.hello.GreeterGrpcKt
import example.hello.Hello
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KotlinLogging

class GreeterImpl : GreeterGrpcKt.GreeterCoroutineImplBase() {
    override suspend fun hello(request: Hello.HelloRequest): Hello.HelloReply {
        log.info { "Hi, ${request.firstName}" }
        return Hello.HelloReply.newBuilder()
            .setMessage("Hello, ${request.firstName} ${request.lastName}.")
            .build()
    }

    override suspend fun helloBlocking(request: Hello.HelloRequest): Hello.HelloReply {
        log.info { "Hi, ${request.firstName}" }

        withContext(Dispatchers.IO) {
            Thread.sleep(3000)
        }
        return Hello.HelloReply.newBuilder()
            .setMessage("Hello, ${request.firstName} ${request.lastName}.")
            .build()
    }

    companion object {
        private val log = KotlinLogging.logger {}
    }
}
