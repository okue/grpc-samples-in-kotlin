package example.kt.armeria.spring

import brave.Tracing
import brave.handler.MutableSpan
import brave.handler.SpanHandler
import brave.propagation.TraceContext
import com.linecorp.armeria.common.brave.RequestContextCurrentTraceContext
import com.linecorp.armeria.common.grpc.GrpcSerializationFormats
import com.linecorp.armeria.common.logging.LogLevel
import com.linecorp.armeria.common.metric.MeterIdPrefixFunction
import com.linecorp.armeria.server.ServiceRequestContext
import com.linecorp.armeria.server.brave.BraveService
import com.linecorp.armeria.server.grpc.GrpcService
import com.linecorp.armeria.server.logging.LoggingService
import com.linecorp.armeria.server.metric.MetricCollectingService
import com.linecorp.armeria.spring.ArmeriaServerConfigurator
import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.ServerInterceptors
import io.grpc.kotlin.CoroutineContextServerInterceptor
import io.grpc.protobuf.services.ProtoReflectionService
import kotlinx.coroutines.Dispatchers
import mu.KotlinLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import kotlin.coroutines.CoroutineContext

fun main(args: Array<String>) {
    System.setProperty("kotlinx.coroutines.debug", "on")
    runApplication<ArmeriaGrpcSpringApplication>()
}

@SpringBootApplication
class ArmeriaGrpcSpringApplication {
    @Bean
    fun tracing(): Tracing {
        return Tracing
            .newBuilder()
            .currentTraceContext(
                // https://armeria.dev/docs/advanced-zipkin
                RequestContextCurrentTraceContext.ofDefault()
            )
            .addSpanHandler(object : SpanHandler() {
                override fun end(context: TraceContext?, span: MutableSpan?, cause: Cause?): Boolean {
                    log.info("span ends: $context")
                    return true
                }
            })
            .build()
    }

    @Bean
    fun myGrpcService(tracing: Tracing) = ArmeriaServerConfigurator { serverBuilder ->
        serverBuilder
            .service(
                GrpcService.builder()
                    .addService(
                        ServerInterceptors.intercept(
                            GreeterImpl(),
                            object : CoroutineContextServerInterceptor() {
                                override fun coroutineContext(
                                    call: ServerCall<*, *>,
                                    headers: Metadata
                                ): CoroutineContext {
                                    return Dispatchers.Unconfined +
                                            ArmeriaRequestContext(ServiceRequestContext.current())
                                }
                            }
                        )
                    )
                    .supportedSerializationFormats(GrpcSerializationFormats.values())
                    .enableUnframedRequests(true)
                    .build(),
                MetricCollectingService.newDecorator(
                    MeterIdPrefixFunction.ofDefault("armeria.client.grpc")
                ),
                BraveService.newDecorator(tracing),
                LoggingService.builder()
                    .requestLogLevel(LogLevel.INFO)
                    .successfulResponseLogLevel(LogLevel.INFO)
                    .failureResponseLogLevel(LogLevel.WARN)
                    .newDecorator()
            )
            .service(
                GrpcService
                    .builder()
                    .addService(ProtoReflectionService.newInstance())
                    .build()
            )
    }

    companion object {
        private val log = KotlinLogging.logger {}
    }
}
