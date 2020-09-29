package example.kt.armeria

import com.linecorp.armeria.client.Clients
import com.linecorp.armeria.server.Server
import example.hello.GreeterGrpcKt
import example.hello.Hello
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class ArmeriaGrpcApplicationTest {
    @Test
    fun test() {
        val job = GlobalScope.async {
            val res = stub.hello(buildHello())
            assertThat(res.message).isEqualTo("Hello, Taro Yamada.")
        }
        runBlocking {
            job.await()
        }
    }

    companion object {
        private lateinit var stub: GreeterGrpcKt.GreeterCoroutineStub

        private lateinit var server: Server

        private fun buildHello() =
            Hello.HelloRequest.newBuilder()
                .setFirstName("Taro")
                .setLastName("Yamada")
                .setMessage("Hello")
                .build()

        private val log = KotlinLogging.logger {}

        @JvmStatic
        @BeforeAll
        fun setup() {
            server = newServer(mode = Mode.EVENT_LOOP)
            server.start().join()
            stub = Clients
                .builder("gproto+http://127.0.0.1:${server.activeLocalPort()}")
                .decorator { delegate, ctx, req ->
                    ctx.log().whenRequestComplete().thenAccept {
                        log.info("==> $it")
                    }
                    ctx.log().whenComplete().thenAccept {
                        log.info("<== $it")
                    }
                    delegate.execute(ctx, req)
                }
                .build(GreeterGrpcKt.GreeterCoroutineStub::class.java)
        }

        @JvmStatic
        @AfterAll
        fun cleanup() {
            server.stop().join()
        }
    }
}
