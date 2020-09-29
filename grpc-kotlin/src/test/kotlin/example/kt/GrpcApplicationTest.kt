package example.kt

import example.hello.GreeterGrpcKt.GreeterCoroutineStub
import example.hello.Hello
import io.grpc.Channel
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class GrpcApplicationTest {

    @Test
    fun test() {
        val stub = GreeterCoroutineStub(channel)
        val jobs = (0..100).map {
            GlobalScope.async {
                val res = stub.hello(buildHello())
                assertThat(res.message).isEqualTo("Hello, Taro Yamada.")
            }
        }
        runBlocking {
            jobs.awaitAll()
        }
    }

    companion object {
        private val serverName = InProcessServerBuilder.generateName()

        private val server = InProcessServerBuilder
            .forName(serverName)
            .addService(GreeterImpl())
            .build()

        private lateinit var channel: Channel

        private fun buildHello() =
            Hello.HelloRequest.newBuilder()
                .setFirstName("Taro")
                .setLastName("Yamada")
                .setMessage("Hello")
                .build()

        @JvmStatic
        @BeforeAll
        fun setup() {
            server.start()
            channel = InProcessChannelBuilder
                .forName(serverName)
                .build()
        }

        @JvmStatic
        @AfterAll
        fun cleanup() {
            server.shutdown()
        }
    }
}
