package io.fries.csa.core.journey

import io.fries.csa.core.timetable.Stop
import java.time.ZonedDateTime

data class JourneyQuery(
    val departure: Stop,
    val arrival: Stop,
    val departureTime: ZonedDateTime
)