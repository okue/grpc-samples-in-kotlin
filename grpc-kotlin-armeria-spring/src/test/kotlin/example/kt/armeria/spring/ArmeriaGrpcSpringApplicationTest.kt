package example.kt.armeria.spring

import com.linecorp.armeria.client.Clients
import com.linecorp.armeria.server.Server
import example.kt.proto.GreeterGrpcKt
import example.kt.proto.Hello
import io.grpc.StatusException
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig

@ActiveProfiles("test")
@SpringBootTest
@SpringJUnitConfig
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class ArmeriaGrpcSpringApplicationTest(
    private val server: Server
) {

    @Test
    fun test() {
        val stub = getStub(server.activeLocalPort())
        val jobs = (0..10).map { c ->
            GlobalScope.async {
                val res = stub.hello(buildHello(c, "Taro"))
                assertThat(res.message).isEqualTo("$c: Hello, Taro Yamada.")
            }
        }
        runBlocking {
            jobs.awaitAll()
        }
    }

    @Test
    fun fooErrorHandle() {
        val stub = getStub(server.activeLocalPort())
        try {
            runBlocking { stub.helloError(buildHello(num = 1, firstName = "Kojiro")) }
        } catch (e: StatusException) {
            log.info("Error -> $e")
            e.trailers?.get(FooErrorException.metadataKey)?.let {
                log.info("FooError sent via trailers -> $it")
            }
        }
    }

    companion object {
        private val log = KotlinLogging.logger {}

        private fun buildHello(num: Int, firstName: String) =
            Hello.HelloRequest.newBuilder()
                .setFirstName(firstName)
                .setLastName("Yamada")
                .setMessage("$num: Hello")
                .build()

        private fun getStub(port: Int): GreeterGrpcKt.GreeterCoroutineStub {
            return Clients
                .builder("gproto+http://127.0.0.1:$port")
                .decorator { delegate, ctx, req ->
                    ctx.log().whenRequestComplete().thenAccept {
                        log.info("==> Req: ${it.fullName()}")
                    }
                    ctx.log().whenComplete().thenAccept {
                        log.info("<== Res: ${it.fullName()}")
                    }
                    delegate.execute(ctx, req)
                }
                .build(GreeterGrpcKt.GreeterCoroutineStub::class.java)
        }
    }
}
