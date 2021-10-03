package io.fries.csa.core.benchmark

import org.junit.jupiter.api.extension.ExtendWith
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.*

@Target(TYPE, FUNCTION, ANNOTATION_CLASS)
@Retention(RUNTIME)
@ExtendWith(BenchmarkExtension::class)
annotation class Benchmark
