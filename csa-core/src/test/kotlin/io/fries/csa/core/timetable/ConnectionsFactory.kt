package io.fries.csa.core.timetable

import java.time.Instant
import java.time.ZonedDateTime

class ConnectionsFactory {

    fun readFrom(filePath: String): Connections {
        return ConnectionsFactory::class.java.classLoader.getResourceAsStream(filePath)
            ?.bufferedReader()
            ?.useLines { lines -> toConnections(lines) }
            ?: Connections()
    }

    private fun toConnections(lines: Sequence<String>) = Connections(
            lines.toList()
                    .map { line -> toConnection(line) }
    )

    private fun toConnection(line: String): Connection = line
        .split(" ")
        .let {
            Connection(
                    Stop(it[0].toInt()),
                    ZonedDateTime.parse(Instant.ofEpochSecond(it[2].toLong()).toString()),
                    Stop(it[1].toInt()),
                    ZonedDateTime.parse(Instant.ofEpochSecond(it[3].toLong()).toString())
            )
        }
}