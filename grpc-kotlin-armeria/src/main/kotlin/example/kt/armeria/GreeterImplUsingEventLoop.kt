package example.kt.armeria

import example.hello.GreeterGrpcKt
import example.hello.Hello
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import mu.KotlinLogging

// Dispatches coroutines to armeria event loop
class GreeterImplUsingEventLoop : GreeterGrpcKt.GreeterCoroutineImplBase(Dispatchers.Unconfined) {
    override suspend fun hello(request: Hello.HelloRequest): Hello.HelloReply {
        log.info { "Hi, ${request.firstName}" }
        delay(100)
        return Hello.HelloReply.newBuilder()
            .setMessage("Hello, ${request.firstName} ${request.lastName}.")
            .build()
    }

    override suspend fun helloBlocking(request: Hello.HelloRequest): Hello.HelloReply {
        log.info { "Hi, ${request.firstName}" }

        withBlockingContext {
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
