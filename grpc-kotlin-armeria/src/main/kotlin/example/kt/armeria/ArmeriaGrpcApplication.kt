package example.kt.armeria

import com.linecorp.armeria.common.grpc.GrpcSerializationFormats
import com.linecorp.armeria.server.Server
import com.linecorp.armeria.server.ServerBuilder
import com.linecorp.armeria.server.docs.DocService
import com.linecorp.armeria.server.grpc.GrpcService
import com.linecorp.armeria.server.healthcheck.HealthCheckService
import io.grpc.BindableService
import io.grpc.protobuf.services.ProtoReflectionService
import mu.KotlinLogging
import kotlin.concurrent.thread

fun main(args: Array<String>) {
    System.setProperty("kotlinx.coroutines.debug", "on")
    val server = when {
        args.isNotEmpty() && args[0].startsWith("co") -> newServer(
            Mode.COROUTINE_DISPATCHERS
        )
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

internal fun newServer(mode: Mode): Server {
    log.info("with mode=$mode")
    return Server.builder()
        .http(8080)
        .myGrpcService(
            if (mode == Mode.EVENT_LOOP)
                GreeterImplUsingEventLoop()
            else
                GreeterImplUsingKtDispatcher()
        )
        .service(
            GrpcService
                .builder()
                .addService(ProtoReflectionService.newInstance())
                .build()
        )
        .serviceUnder("/docs", DocService())
        .serviceUnder("/health", HealthCheckService.builder().build())
        .build()
}

private fun ServerBuilder.myGrpcService(bindableService: BindableService): ServerBuilder {
    return this.service(
        GrpcService
            .builder()
            .addService(bindableService)
            .supportedSerializationFormats(GrpcSerializationFormats.values())
            .enableUnframedRequests(true)
            .build()
    )
}

private val log = KotlinLogging.logger {}
