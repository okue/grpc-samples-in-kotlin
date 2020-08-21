package example.kt.armeria.spring

import com.linecorp.armeria.client.Clients
import example.hello.GreeterGrpcKt
import example.hello.Hello
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ArmeriaGrpcSpringApplicationTest {
    @Test
    fun test() {
        val stub = getStub()
        val jobs = (0..10).map {
            GlobalScope.launch {
                val res = stub.hello(buildHello())
                assertThat(res.message).isEqualTo("Hello, Taro Yamada.")
            }
        }
        runBlocking {
            jobs.joinAll()
        }
    }

    companion object {
        private val log = KotlinLogging.logger {}

        private fun buildHello() =
            Hello.HelloRequest.newBuilder()
                .setFirstName("Taro")
                .setLastName("Yamada")
                .setMessage("Hello")
                .build()

        private fun getStub(): GreeterGrpcKt.GreeterCoroutineStub {
            return Clients
                .builder("gproto+http://127.0.0.1:8080")
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
    }
}
