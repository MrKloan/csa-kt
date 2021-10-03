package io.fries.csa.core.journey

import io.fries.csa.core.timetable.Stop
import io.fries.csa.core.timetable.Timestamp

data class JourneyQuery(
    val departure: Stop,
    val arrival: Stop,
    val departureTime: Timestamp
)