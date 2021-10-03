package io.fries.csa.core.timetable

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
                Timestamp(it[2].toLong()),
                Stop(it[1].toInt()),
                Timestamp(it[3].toLong())
            )
        }
}