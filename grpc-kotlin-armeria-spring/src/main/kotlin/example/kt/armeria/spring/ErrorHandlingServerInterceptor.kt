package example.kt.armeria.spring

import example.kt.proto.Error
import io.grpc.ForwardingServerCall
import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.ServerCallHandler
import io.grpc.ServerInterceptor
import io.grpc.Status
import io.grpc.protobuf.ProtoUtils
import mu.KotlinLogging

// See:
// https://github.com/saturnism/grpc-by-example-java/blob/master/error-handling-example/error-server/src/main/java/com/example/grpc/server/UnknownStatusDescriptionInterceptor.java
// https://github.com/grpc/grpc-kotlin/issues/141#issuecomment-726829195
class ErrorHandlingServerInterceptor : ServerInterceptor {

    override fun <ReqT : Any, RespT : Any> interceptCall(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        next: ServerCallHandler<ReqT, RespT>
    ): ServerCall.Listener<ReqT> {
        return next.startCall(errorHandlingServerCall(call), headers)
    }

    private fun <ReqT, RespT> errorHandlingServerCall(call: ServerCall<ReqT, RespT>) =
        object : ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
            override fun close(status: Status, trailers: Metadata) {
                if (status.code != Status.Code.UNKNOWN) {
                    super.close(status, trailers)
                    return
                }
                val (newStatus, newTrailers) = errorHandle(status, trailers)
                super.close(newStatus, newTrailers)
            }
        }

    companion object {
        private val log = KotlinLogging.logger {}

        private fun errorHandle(
            status: Status,
            trailers: Metadata
        ): Pair<Status, Metadata> = when (val cause = status.cause) {
            is FooErrorException -> {
                val newStatus = Status.PERMISSION_DENIED
                    .withDescription("Foo error by ${cause.reason}")
                    .withCause(cause)
                trailers.put(FooErrorException.metadataKey, cause.toProto)
                newStatus to trailers
            }
            else -> {
                log.error("Unexpected error!! -> $cause")
                Status.INTERNAL to trailers
            }
        }
    }
}

class FooErrorException(
    override val message: String,
    val reason: String
) : RuntimeException(message) {

    val toProto: Error.FooError =
        Error.FooError
            .newBuilder()
            .setMessage(message)
            .setReason(reason)
            .build()

    companion object {
        val metadataKey: Metadata.Key<Error.FooError> = ProtoUtils.keyForProto(Error.FooError.getDefaultInstance())
    }
}
