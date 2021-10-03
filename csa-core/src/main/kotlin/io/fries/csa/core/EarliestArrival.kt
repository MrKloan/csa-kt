package io.fries.csa.core

import io.fries.csa.core.timetable.Connections
import io.fries.csa.core.journey.JourneyQuery

class EarliestArrival(private val connections: Connections) : CSA {

    override fun compute(query: JourneyQuery): Connections {
        TODO("Not yet implemented")
    }
}