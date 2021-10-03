package io.fries.csa.core

import io.fries.csa.core.journey.JourneyQuery
import io.fries.csa.core.timetable.Connection
import io.fries.csa.core.timetable.Connections
import io.fries.csa.core.timetable.Stop
import java.time.Instant
import java.time.ZonedDateTime

class EarliestArrival(private val connections: Connections) : CSA {

    override fun compute(query: JourneyQuery): Connections {
        val reachableConnections = findReachableConnections(query)
        return createEarliestArrivalJourney(query, reachableConnections)
    }

    private fun findReachableConnections(query: JourneyQuery): Map<Stop, Connection> {
        val reachableConnections = mutableMapOf<Stop, Connection>()

        var earliestArrival = ZonedDateTime.parse(Instant.ofEpochMilli(Long.MAX_VALUE).toString())
        val earliestArrivalByStop = mutableMapOf(
            query.departure to query.departureTime
        )

        for (connection in connections) {
            if (isReachableMoreQuickly(connection, earliestArrivalByStop)) {
                earliestArrivalByStop[connection.arrivalStop] = connection.arrivalTime
                reachableConnections[connection.arrivalStop] = connection

                if (connection.arrivalStop == query.arrival) {
                    earliestArrival =
                        if (earliestArrival.isBefore(connection.arrivalTime)) earliestArrival
                        else connection.arrivalTime
                }
            } else if (connection.arrivalTime.isAfter(earliestArrival)) {
                break
            }
        }

        return reachableConnections
    }

    private fun isReachableMoreQuickly(connection: Connection, earliestArrivalByStop: MutableMap<Stop, ZonedDateTime>) =
        isDepartureStopReachableBeforeConnectionDeparture(connection, earliestArrivalByStop)
            && isConnectionArrivalBeforeEarliestStopArrival(earliestArrivalByStop, connection)

    private fun isDepartureStopReachableBeforeConnectionDeparture(connection: Connection, earliestArrivalByStop: MutableMap<Stop, ZonedDateTime>) =
        earliestArrivalByStop[connection.departureStop]?.isBefore(connection.departureTime) == true
            || earliestArrivalByStop[connection.departureStop]?.isEqual(connection.departureTime) == true

    private fun isConnectionArrivalBeforeEarliestStopArrival(earliestArrivalByStop: MutableMap<Stop, ZonedDateTime>, connection: Connection) =
        earliestArrivalByStop[connection.arrivalStop]?.isAfter(connection.arrivalTime) ?: true

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
