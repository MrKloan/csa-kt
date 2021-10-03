package io.fries.csa.core

import io.fries.csa.core.journey.JourneyQuery
import io.fries.csa.core.timetable.Connection
import io.fries.csa.core.timetable.Connections
import io.fries.csa.core.timetable.Stop
import io.fries.csa.core.timetable.Timestamp

class EarliestArrival(private val connections: Connections) : CSA {

    override fun compute(query: JourneyQuery): Connections {
        val reachableConnections = findReachableConnections(query)
        return createEarliestArrivalJourney(query, reachableConnections)
    }

    private fun findReachableConnections(query: JourneyQuery): Map<Stop, Connection> {
        val reachableConnections = mutableMapOf<Stop, Connection>()

        var earliestArrival = Timestamp.max()
        val earliestArrivalByStop = mutableMapOf(
            query.departure to query.departureTime
        )

        for (connection in connections.departingFor(query)) {
            if (isReachableMoreQuickly(connection, earliestArrivalByStop)) {
                earliestArrivalByStop[connection.arrivalStop] = connection.arrivalTime
                reachableConnections[connection.arrivalStop] = connection

                if (connection.arrivalStop == query.arrival) {
                    earliestArrival =
                        if (earliestArrival.before(connection.arrivalTime)) earliestArrival
                        else connection.arrivalTime
                }
            } else if (connection.arrivalTime.after(earliestArrival)) {
                break
            }
        }

        return reachableConnections
    }

    private fun isReachableMoreQuickly(connection: Connection, earliestArrivalByStop: MutableMap<Stop, Timestamp>) =
        isDepartureStopReachableBeforeConnectionDeparture(connection, earliestArrivalByStop)
            && isConnectionArrivalBeforeEarliestStopArrival(earliestArrivalByStop, connection)

    private fun isDepartureStopReachableBeforeConnectionDeparture(connection: Connection, earliestArrivalByStop: MutableMap<Stop, Timestamp>): Boolean =
        earliestArrivalByStop[connection.departureStop]
            ?.beforeOrEqualTo(connection.departureTime)
            ?: false

    private fun isConnectionArrivalBeforeEarliestStopArrival(earliestArrivalByStop: MutableMap<Stop, Timestamp>, connection: Connection) =
        earliestArrivalByStop[connection.arrivalStop]
            ?.after(connection.arrivalTime)
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
