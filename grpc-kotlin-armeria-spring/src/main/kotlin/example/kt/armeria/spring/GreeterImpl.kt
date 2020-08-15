package example.kt.armeria.spring

import example.hello.GreeterGrpcKt
import example.hello.Hello
import kotlinx.coroutines.delay
import mu.KotlinLogging

class GreeterImpl : GreeterGrpcKt.GreeterCoroutineImplBase() {
    override suspend fun hello(request: Hello.HelloRequest): Hello.HelloReply {
        return Hello.HelloReply.newBuilder()
            .setMessage("Hello, ${request.firstName} ${request.lastName}.")
            .build()
    }

    override suspend fun helloBlocking(request: Hello.HelloRequest): Hello.HelloReply {
        withBlockingContext {
            executeBlockingTask()
        }
        withDefaultContext {
            executeNonBlockingTask()
        }
        return Hello.HelloReply.newBuilder()
            .setMessage("Hello, ${request.firstName} ${request.lastName}.")
            .build()
    }

    private suspend fun executeNonBlockingTask() {
        log.info("Start non-blocking task")
        delay(2000)
        log.info("Done")
    }

    private fun executeBlockingTask() {
        log.info("Start blocking task")
        Thread.sleep(2000)
        log.info("Done")
    }

    companion object {
        private val log = KotlinLogging.logger {}
    }
}
