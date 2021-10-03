package io.fries.csa.core.timetable

import java.time.ZonedDateTime

data class Timestamp(private val seconds: Long) {

    companion object {
        fun parse(formatted: String) = Timestamp(ZonedDateTime.parse(formatted).toEpochSecond())
        fun max() = Timestamp(Long.MAX_VALUE)
    }

    fun after(timestamp: Timestamp) = seconds > timestamp.seconds
    fun afterOrEqualTo(timestamp: Timestamp) = seconds >= timestamp.seconds

    fun before(timestamp: Timestamp) = seconds < timestamp.seconds
    fun beforeOrEqualTo(timestamp: Timestamp) = seconds <= timestamp.seconds
}