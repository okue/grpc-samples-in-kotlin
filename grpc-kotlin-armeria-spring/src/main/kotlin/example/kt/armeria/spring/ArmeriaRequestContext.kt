package example.kt.armeria.spring

import com.linecorp.armeria.common.util.SafeCloseable
import com.linecorp.armeria.server.ServiceRequestContext
import kotlinx.coroutines.ThreadContextElement
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

class ArmeriaRequestContext(
    private val requestContext: ServiceRequestContext
) : ThreadContextElement<SafeCloseable>, AbstractCoroutineContextElement(Key) {

    companion object Key : CoroutineContext.Key<ArmeriaRequestContext>

    override fun updateThreadContext(context: CoroutineContext): SafeCloseable {
        return requestContext.push()
    }

    override fun restoreThreadContext(context: CoroutineContext, oldState: SafeCloseable) {
        oldState.close()
    }
}
