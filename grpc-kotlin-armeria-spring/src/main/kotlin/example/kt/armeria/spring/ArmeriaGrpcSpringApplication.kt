package example.kt.armeria.spring

import brave.Tracing
import brave.handler.MutableSpan
import brave.handler.SpanHandler
import brave.propagation.TraceContext
import com.linecorp.armeria.common.brave.RequestContextCurrentTraceContext
import com.linecorp.armeria.common.grpc.GrpcMeterIdPrefixFunction
import com.linecorp.armeria.common.grpc.GrpcSerializationFormats
import com.linecorp.armeria.common.logging.LogLevel
import com.linecorp.armeria.common.metric.MoreMeters
import com.linecorp.armeria.server.ServiceRequestContext
import com.linecorp.armeria.server.brave.BraveService
import com.linecorp.armeria.server.grpc.GrpcService
import com.linecorp.armeria.server.logging.LoggingService
import com.linecorp.armeria.spring.ArmeriaServerConfigurator
import io.grpc.BindableService
import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.ServerInterceptors
import io.grpc.kotlin.CoroutineContextServerInterceptor
import io.grpc.protobuf.services.ProtoReflectionService
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig
import mu.KotlinLogging
import mu.withLoggingContext
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import kotlin.coroutines.CoroutineContext

fun main() {
    System.setProperty("kotlinx.coroutines.debug", "on")
    runApplication<ArmeriaGrpcSpringApplication>()
}

@SpringBootApplication
class ArmeriaGrpcSpringApplication {

    init {
        MoreMeters.setDistributionStatisticConfig(
            DistributionStatisticConfig.builder()
                // configures quantiles
                .percentiles(0.5, 0.75, 0.90, 0.95, 0.99)
                .build()
                .merge(MoreMeters.distributionStatisticConfig())
        )
    }

    @Bean
    fun tracing(): Tracing {
        return Tracing
            .newBuilder()
            .currentTraceContext(
                // https://armeria.dev/docs/advanced-zipkin
                RequestContextCurrentTraceContext.ofDefault()
            )
            .addSpanHandler(object : SpanHandler() {
                override fun end(context: TraceContext, span: MutableSpan?, cause: Cause?): Boolean {
                    withLoggingContext("traceId" to context.traceIdString()) {
                        log.info("span ends: $context")
                    }
                    return true
                }
            })
            .build()
    }

    @Bean
    fun meterIdPrefixFunction() =
        GrpcMeterIdPrefixFunction.of("armeria.server")

    @Bean
    fun myGrpcService(
        grpcServices: List<BindableService>,
        tracing: Tracing
    ) = ArmeriaServerConfigurator { serverBuilder ->
        serverBuilder
            .service(
                GrpcService.builder().apply {
                    grpcServices.forEach {
                        addService(
                            ServerInterceptors.intercept(
                                it,
                                ErrorHandlingServerInterceptor(),
                                ArmeriaRequestContextInterceptor
                            )
                        )
                    }
                }
                    .supportedSerializationFormats(
                        GrpcSerializationFormats.PROTO,
                        GrpcSerializationFormats.JSON,
                    )
                    // For https://armeria.dev/docs/server-docservice/
                    .enableUnframedRequests(true)
                    .build(),
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

        private object ArmeriaRequestContextInterceptor : CoroutineContextServerInterceptor() {
            override fun coroutineContext(
                call: ServerCall<*, *>,
                headers: Metadata
            ): CoroutineContext {
                val ctx = ServiceRequestContext.current()
                // To propagate armeria request context to non-armeria threads
                return ArmeriaRequestContext(ctx)
            }
        }
    }
}
