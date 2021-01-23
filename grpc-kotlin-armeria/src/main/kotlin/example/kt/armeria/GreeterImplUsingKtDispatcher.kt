package example.kt.armeria

import example.kt.proto.GreeterGrpcKt
import example.kt.proto.Hello
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import mu.KotlinLogging

// Dispatches coroutines to Dispatchers.Default/IO
class GreeterImplUsingKtDispatcher : GreeterGrpcKt.GreeterCoroutineImplBase(Dispatchers.Default) {
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
