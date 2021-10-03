package io.fries.csa.core

import io.fries.csa.core.journey.JourneyQuery
import io.fries.csa.core.timetable.Connection
import io.fries.csa.core.timetable.Connections
import io.fries.csa.core.timetable.Stop

class EarliestArrival(private val connections: Connections) : CSA {

    override fun compute(query: JourneyQuery): Connections {
        val reachableConnections = findReachableConnections(query)
        return createEarliestArrivalJourney(query, reachableConnections)
    }

    private fun findReachableConnections(query: JourneyQuery): Map<Stop, Connection> {
        val reachableConnections = mutableMapOf<Stop, Connection>()

        var earliestArrival = Long.MAX_VALUE
        val earliestArrivalByStop = mutableMapOf(
            query.departure to query.departureTime.toEpochSecond()
        )

        for (connection in connections) {
            if (isReachableMoreQuickly(connection, earliestArrivalByStop)) {
                earliestArrivalByStop[connection.arrivalStop] = connection.arrivalTimestamp
                reachableConnections[connection.arrivalStop] = connection

                if (connection.arrivalStop == query.arrival) {
                    earliestArrival =
                        if (earliestArrival < connection.arrivalTimestamp) earliestArrival
                        else connection.arrivalTimestamp
                }
            } else if (connection.arrivalTimestamp > earliestArrival) {
                break
            }
        }

        return reachableConnections
    }

    private fun isReachableMoreQuickly(connection: Connection, earliestArrivalByStop: MutableMap<Stop, Long>) =
        isDepartureStopReachableBeforeConnectionDeparture(connection, earliestArrivalByStop)
            && isConnectionArrivalBeforeEarliestStopArrival(earliestArrivalByStop, connection)

    private fun isDepartureStopReachableBeforeConnectionDeparture(connection: Connection, earliestArrivalByStop: MutableMap<Stop, Long>): Boolean =
        earliestArrivalByStop[connection.departureStop]
            ?.let { it <= connection.departureTimestamp }
            ?: false

    private fun isConnectionArrivalBeforeEarliestStopArrival(earliestArrivalByStop: MutableMap<Stop, Long>, connection: Connection) =
        earliestArrivalByStop[connection.arrivalStop]
            ?.let { it > connection.arrivalTimestamp }
            ?: true

    private fun createEarliestArrivalJourney(query: JourneyQuery, reachableConnections: Map<Stop, Connection>): Connections {
        val route = mutableListOf<Connection>()
        var lastConnection = reachableConnections[query.arrival]

        while (lastConnection != null) {
            route += lastConnection
            lastConnection = reachableConnections[lastConnection.departureStop]
        }

        return Connections(route.reversed())
    }
}
