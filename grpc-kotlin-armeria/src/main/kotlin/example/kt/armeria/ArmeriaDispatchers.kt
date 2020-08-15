package example.kt.armeria

import com.linecorp.armeria.server.ServiceRequestContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext

object ArmeriaDispatchers {
    val nonBlocking
        get() = ServiceRequestContext.current().eventLoop().asCoroutineDispatcher()

    val blocking
        get() = ServiceRequestContext.current().blockingTaskExecutor().asCoroutineDispatcher()
}

suspend fun <T> withDefaultContext(block: suspend CoroutineScope.() -> T): T =
    withContext(ArmeriaDispatchers.nonBlocking, block)

suspend fun <T> withBlockingContext(block: suspend CoroutineScope.() -> T): T =
    withContext(ArmeriaDispatchers.blocking, block)
