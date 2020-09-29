package example.kt.armeria.spring

import com.linecorp.armeria.server.ServiceRequestContext
import example.hello.GreeterGrpcKt
import example.hello.Hello
import kotlinx.coroutines.delay
import mu.KotlinLogging

class GreeterImpl : GreeterGrpcKt.GreeterCoroutineImplBase() {
    override suspend fun hello(request: Hello.HelloRequest): Hello.HelloReply {
        log.info { "Hello" }
        delay(listOf(100, 1).random().toLong())
        ServiceRequestContext.current()
        return Hello.HelloReply.newBuilder()
            .setMessage("${request.message}, ${request.firstName} ${request.lastName}.")
            .build()
    }

    override suspend fun helloBlocking(request: Hello.HelloRequest): Hello.HelloReply {
        log.info { "Hello" }
        withBlockingContext {
            ServiceRequestContext.current()
            executeBlockingTask()
        }
        withDefaultContext {
            ServiceRequestContext.current()
            executeNonBlockingTask()
        }
        ServiceRequestContext.current()
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
