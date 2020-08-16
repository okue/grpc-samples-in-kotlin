package example.kt.armeria.spring.logging

import brave.propagation.TraceContext
import java.util.function.Function

// https://line.github.io/armeria/docs/advanced-logging/#using-an-alternative-string-converter-for-a-custom-attribute
class TraceIdStringifier : Function<TraceContext, String> {
    override fun apply(t: TraceContext): String = t.traceIdString()
}

class SpanIdStringifier : Function<TraceContext, String> {
    override fun apply(t: TraceContext): String = t.spanIdString()
}

class ParentIdStringifier : Function<TraceContext, String?> {
    override fun apply(t: TraceContext): String? = t.parentIdString()
}

class SampledStringifier : Function<TraceContext, String?> {
    override fun apply(t: TraceContext): String? = t.sampled()?.toString()
}
