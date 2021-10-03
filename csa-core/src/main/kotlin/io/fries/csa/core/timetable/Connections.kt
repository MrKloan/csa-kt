package io.fries.csa.core.timetable

data class Connections(val connections: List<Connection> = listOf()) : List<Connection> by connections {

    init {
        require(connections == connections.sortedBy { it.departureTime }) {
            "connections should be sorted by departure time"
        }
    }
}