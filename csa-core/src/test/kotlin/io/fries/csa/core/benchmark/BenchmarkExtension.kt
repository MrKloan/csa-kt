package io.fries.csa.core.benchmark

import org.junit.jupiter.api.extension.AfterTestExecutionCallback
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.fail
import java.time.Duration
import java.time.ZonedDateTime

internal class BenchmarkExtension : BeforeTestExecutionCallback, AfterTestExecutionCallback {

    companion object {
        private val NAMESPACE = ExtensionContext.Namespace.create(this::class.java.canonicalName)
    }

    override fun beforeTestExecution(context: ExtensionContext) {
        findBenchmarkFrom(context) ?: return
        context.getStore(NAMESPACE).put(context.testMethod, ZonedDateTime.now())
    }

    override fun afterTestExecution(context: ExtensionContext) {
        val threshold = findBenchmarkFrom(context)
            ?.let { Duration.ofMillis(it.threshold) }
            ?: return

        val launchTime = context.getStore(NAMESPACE).get(context.testMethod, ZonedDateTime::class.java)
        val duration = Duration.between(launchTime, ZonedDateTime.now())

        if (duration > threshold) {
            fail {
                "'${context.displayName}' took ${duration.toMillis()} ms., " +
                    "which is greater than the threshold of ${threshold.toMillis()} ms."
            }
        }

        val message = "'${context.displayName}' took ${duration.toMillis()} ms."
        println(message)
    }

    private fun findBenchmarkFrom(context: ExtensionContext): Benchmark? = context.element
        .map { it.getAnnotation(Benchmark::class.java) }
        .orElse(null)
}
