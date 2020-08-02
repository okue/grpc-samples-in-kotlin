package org.okue

import example.hello.GreeterGrpcKt
import example.hello.Hello
import kotlinx.coroutines.Dispatchers
import mu.KotlinLogging

// Dispatches coroutines to armeria event loop
class GreeterImplUsingEventLoop : GreeterGrpcKt.GreeterCoroutineImplBase(Dispatchers.Unconfined) {
    override suspend fun hello(request: Hello.HelloRequest): Hello.HelloReply = withDefaultContext {
        log.info { request }
        Hello.HelloReply.newBuilder()
            .setMessage("Hello, ${request.firstName} ${request.lastName}.")
            .build()
    }

    override suspend fun helloBlocking(request: Hello.HelloRequest): Hello.HelloReply = withDefaultContext {
        log.info { request }

        withBlockingContext {
            Thread.sleep(3000)
        }
        Hello.HelloReply.newBuilder()
            .setMessage("Hello, ${request.firstName} ${request.lastName}.")
            .build()
    }

    companion object {
        private val log = KotlinLogging.logger {}
    }
}
