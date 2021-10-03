package io.fries.csa.core

import io.fries.csa.core.timetable.Connections
import io.fries.csa.core.journey.JourneyQuery

fun interface CSA {
    fun compute(query: JourneyQuery): Connections
}