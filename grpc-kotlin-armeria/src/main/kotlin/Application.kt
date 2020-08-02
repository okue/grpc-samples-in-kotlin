package org.okue

import com.linecorp.armeria.common.grpc.GrpcSerializationFormats
import com.linecorp.armeria.server.Server
import com.linecorp.armeria.server.docs.DocService
import com.linecorp.armeria.server.grpc.GrpcService
import io.grpc.protobuf.services.ProtoReflectionService
import mu.KotlinLogging
import kotlin.concurrent.thread

fun main(args: Array<String>) {
    val server = when {
        args.isNotEmpty() && args[0].startsWith("co") -> newServer(Mode.COROUTINE_DISPATCHERS)
        else -> newServer(Mode.EVENT_LOOP)
    }

    Runtime.getRuntime().addShutdownHook(thread(start = false) {
        server.stop().join()
        log.info("Server has been stopped.")
    })

    server.start().join()
    log.info("Doc service at http://127.0.0.1:8080/docs")
}

enum class Mode {
    EVENT_LOOP, COROUTINE_DISPATCHERS
}

private fun newServer(mode: Mode): Server {
    val grpcService = GrpcService.builder()
        .apply {
            if (mode == Mode.EVENT_LOOP)
                addService(GreeterImplUsingEventLoop())
            else
                addService(GreeterImplUsingKtDispatcher())
        }
        .addService(ProtoReflectionService.newInstance())
        .supportedSerializationFormats(GrpcSerializationFormats.values())
        .enableUnframedRequests(true)
        .build()

    return Server.builder()
        .http(8080)
        .service(grpcService)
        .serviceUnder("/docs", DocService())
        .build()
}

private val log = KotlinLogging.logger {}
