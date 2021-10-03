package io.fries.csa.core.benchmark

import org.junit.jupiter.api.extension.AfterTestExecutionCallback
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.platform.commons.support.AnnotationSupport
import java.time.Duration
import java.time.ZonedDateTime

internal class BenchmarkExtension : BeforeTestExecutionCallback, AfterTestExecutionCallback {

    companion object {
        private val NAMESPACE = ExtensionContext.Namespace.create(this::class.java.canonicalName)
    }

    override fun beforeTestExecution(context: ExtensionContext) {
        if (!shouldBeBenchmarked(context)) {
            return
        }

        storeNowAsLaunchTime(context)
    }

    override fun afterTestExecution(context: ExtensionContext) {
        if (!shouldBeBenchmarked(context)) {
            return
        }

        val launchTime = loadLaunchTime(context)
        val duration = Duration.between(launchTime, ZonedDateTime.now())
        report(context, duration)
    }

    private fun shouldBeBenchmarked(context: ExtensionContext): Boolean {
        return context.element
            .map { AnnotationSupport.isAnnotated(it, Benchmark::class.java) }
            .orElse(false)
    }

    private fun storeNowAsLaunchTime(context: ExtensionContext) {
        context.getStore(NAMESPACE).put(context.testMethod, ZonedDateTime.now())
    }

    private fun loadLaunchTime(context: ExtensionContext): ZonedDateTime {
        return context.getStore(NAMESPACE).get(context.testMethod, ZonedDateTime::class.java)
    }

    private fun report(context: ExtensionContext, duration: Duration) {
        val message = "'${context.displayName}' took ${duration.toMillis()} ms."
        println(message)
        context.publishReportEntry("Benchmark", message)
    }
}
