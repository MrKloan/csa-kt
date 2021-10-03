package io.fries.csa.core.timetable

import java.time.ZonedDateTime

data class Connection(
    val departureStop: Stop,
    val departureTime: ZonedDateTime,
    val arrivalStop: Stop,
    val arrivalTime: ZonedDateTime
) {
    val departureTimestamp = departureTime.toEpochSecond()
    val arrivalTimestamp = arrivalTime.toEpochSecond()
}