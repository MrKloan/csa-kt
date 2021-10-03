package io.fries.csa.core.timetable

import io.fries.csa.core.journey.JourneyQuery

data class Connections(private val connections: List<Connection> = listOf()) : List<Connection> by connections {

    fun departingFor(query: JourneyQuery): Connections = Connections(connections.subList(
        connections.indexOfFirst { it.departureTime.afterOrEqualTo(query.departureTime) },
        connections.size
    ))
}