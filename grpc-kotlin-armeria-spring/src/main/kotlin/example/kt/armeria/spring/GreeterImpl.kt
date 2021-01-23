package example.kt.armeria.spring

import com.linecorp.armeria.server.ServiceRequestContext
import example.hello.GreeterGrpcKt
import example.hello.Hello
import io.grpc.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
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
            ensureArmeriaRequestContext()
            log.info("Start non-blocking task")
            Thread.sleep(1000)
            log.info("Done")
        }
        withDefaultContext {
            ensureArmeriaRequestContext()
            log.info("Start non-blocking task")
            delay(1000)
            log.info("Done")
        }
        ensureArmeriaRequestContext()
        withContext(Dispatchers.IO) {
            ensureArmeriaRequestContext()
            log.info("Start non-blocking task")
            delay(1000)
            log.info("Done")
        }
        ensureArmeriaRequestContext()
        return Hello.HelloReply.newBuilder()
            .setMessage("Hello, ${request.firstName} ${request.lastName}.")
            .build()
    }

    override suspend fun helloError(request: Hello.HelloRequest): Hello.HelloReply {
        throw when (request.firstName) {
            "" -> {
                Status.INVALID_ARGUMENT.withDescription("first name is empty!!").asRuntimeException()
            }
            "Nishiyama" -> {
                RuntimeException("Who is Nishiyama!")
            }
            "Yamada" -> {
                RuntimeException("Who is Yamada!")
            }
            else -> {
                Status.PERMISSION_DENIED.withDescription("Unexpected User").asRuntimeException()
            }
        }
    }

    private fun ensureArmeriaRequestContext() {
        ServiceRequestContext.current()
    }

    companion object {
        private val log = KotlinLogging.logger {}
    }
}
