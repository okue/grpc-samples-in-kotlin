package example.kt

import io.grpc.ServerBuilder
import mu.KotlinLogging

fun main() {
    log.info("Starting...")
    val server = ServerBuilder
        .forPort(8080)
        .addService(GreeterImpl())
        .build()
    log.info("http://localhost:8080")

    Runtime.getRuntime().addShutdownHook(
        Thread {
            server.shutdown()
            log.info("shutdown...")
        }
    )

    server.start()
    server.awaitTermination()
}

private val log = KotlinLogging.logger {}
