package io.fries.csa.core.timetable

data class Connection(
    val departureStop: Stop,
    val departureTime: Timestamp,
    val arrivalStop: Stop,
    val arrivalTime: Timestamp
)