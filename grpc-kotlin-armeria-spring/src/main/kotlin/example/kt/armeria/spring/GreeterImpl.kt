package example.kt.armeria.spring

import com.linecorp.armeria.server.ServiceRequestContext
import example.kt.proto.GreeterGrpcKt
import example.kt.proto.Hello
import io.grpc.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.springframework.stereotype.Component

@Component
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
        throw when (request.firstName + request.lastName) {
            "" -> {
                Status.INVALID_ARGUMENT.withDescription("first name is empty!!").asRuntimeException()
            }
            "KojiroYamada" -> {
                FooErrorException(message = "Who are you?", reason = "invalid user")
            }
            else -> {
                RuntimeException("Unexpected")
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
