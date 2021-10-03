package io.fries.csa.core

import io.fries.csa.core.benchmark.Benchmark
import io.fries.csa.core.benchmark.BenchmarkExtension
import io.fries.csa.core.journey.JourneyQuery
import io.fries.csa.core.timetable.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

internal open class EarliestArrivalTest {

    @Nested
    inner class ComputeEarliestArrival {

        private lateinit var earliestArrival: EarliestArrival

        @BeforeEach
        internal fun setUp() {
            this.earliestArrival = EarliestArrival(
                Connections(
                    listOf(
                        Connection(Stop(1), Timestamp.parse("1970-01-01T02:00:00Z"), Stop(3), Timestamp.parse("1970-01-01T10:00:00Z")),
                        Connection(Stop(1), Timestamp.parse("1970-01-01T03:00:00Z"), Stop(2), Timestamp.parse("1970-01-01T07:00:00Z")),
                        Connection(Stop(2), Timestamp.parse("1970-01-01T04:00:00Z"), Stop(3), Timestamp.parse("1970-01-01T05:00:00Z")),
                        Connection(Stop(1), Timestamp.parse("1970-01-01T05:00:00Z"), Stop(3), Timestamp.parse("1970-01-01T10:00:00Z")),
                        Connection(Stop(2), Timestamp.parse("1970-01-01T08:00:00Z"), Stop(3), Timestamp.parse("1970-01-01T09:00:00Z"))
                    )
                )
            )
        }

        @Test
        internal fun should_compute_a_single_connection() {
            val query = JourneyQuery(
                departure = Stop(1),
                arrival = Stop(2),
                departureTime = Timestamp.parse("1970-01-01T03:00:00Z")
            )

            val connections = earliestArrival.compute(query)

            assertThat(connections).containsExactly(
                Connection(Stop(1), Timestamp.parse("1970-01-01T03:00:00Z"), Stop(2), Timestamp.parse("1970-01-01T07:00:00Z"))
            )
        }

        @Test
        internal fun should_compute_multiple_connections() {
            val query = JourneyQuery(
                departure = Stop(1),
                arrival = Stop(3),
                departureTime = Timestamp.parse("1970-01-01T03:00:00Z")
            )

            val connections = earliestArrival.compute(query)

            assertThat(connections).containsExactly(
                Connection(Stop(1), Timestamp.parse("1970-01-01T03:00:00Z"), Stop(2), Timestamp.parse("1970-01-01T07:00:00Z")),
                Connection(Stop(2), Timestamp.parse("1970-01-01T08:00:00Z"), Stop(3), Timestamp.parse("1970-01-01T09:00:00Z"))
            )
        }

        @Test
        internal fun should_compute_late_single_connection() {
            val query = JourneyQuery(
                departure = Stop(1),
                arrival = Stop(3),
                departureTime = Timestamp.parse("1970-01-01T04:00:00Z")
            )

            val connections = earliestArrival.compute(query)

            assertThat(connections).containsExactly(
                Connection(Stop(1), Timestamp.parse("1970-01-01T05:00:00Z"), Stop(3), Timestamp.parse("1970-01-01T10:00:00Z"))
            )
        }

        @Test
        internal fun should_compute_empty_connections_given_an_unknown_stop() {
            val query = JourneyQuery(
                departure = Stop(2),
                arrival = Stop(1),
                departureTime = Timestamp.parse("1970-01-01T01:00:00Z")
            )

            val connections = earliestArrival.compute(query)

            assertThat(connections).isEmpty()
        }
    }

    @Nested
    @ExtendWith(BenchmarkExtension::class)
    inner class AcceptanceTest {

        private lateinit var earliestArrival: EarliestArrival

        @BeforeEach
        internal fun setUp() {
            this.earliestArrival = EarliestArrival(
                ConnectionsFactory().readFrom("benchmark.txt")
            )
        }

        @Test
        @Benchmark(threshold = 50)
        internal fun should_compute_earliest_arrival_journey() {
            val query = JourneyQuery(
                departure = Stop(19930),
                arrival = Stop(18741),
                departureTime = Timestamp.parse("1970-01-01T10:00:00Z")
            )

            val connections = earliestArrival.compute(query)

            assertThat(connections).containsExactly(
                Connection(Stop(19930), Timestamp.parse("1970-01-01T10:00Z"), Stop(19931), Timestamp.parse("1970-01-01T10:02Z")),
                Connection(Stop(19931), Timestamp.parse("1970-01-01T10:02Z"), Stop(19932), Timestamp.parse("1970-01-01T10:04Z")),
                Connection(Stop(19932), Timestamp.parse("1970-01-01T10:05Z"), Stop(19933), Timestamp.parse("1970-01-01T10:07Z")),
                Connection(Stop(19933), Timestamp.parse("1970-01-01T10:07Z"), Stop(19870), Timestamp.parse("1970-01-01T10:10Z")),
                Connection(Stop(19870), Timestamp.parse("1970-01-01T10:11Z"), Stop(16651), Timestamp.parse("1970-01-01T10:28Z")),
                Connection(Stop(16651), Timestamp.parse("1970-01-01T10:29Z"), Stop(22870), Timestamp.parse("1970-01-01T10:31Z")),
                Connection(Stop(22870), Timestamp.parse("1970-01-01T10:32Z"), Stop(16650), Timestamp.parse("1970-01-01T10:34Z")),
                Connection(Stop(16650), Timestamp.parse("1970-01-01T10:35Z"), Stop(16608), Timestamp.parse("1970-01-01T10:38Z")),
                Connection(Stop(16608), Timestamp.parse("1970-01-01T10:39Z"), Stop(16649), Timestamp.parse("1970-01-01T10:42Z")),
                Connection(Stop(16649), Timestamp.parse("1970-01-01T10:42Z"), Stop(16648), Timestamp.parse("1970-01-01T10:46Z")),
                Connection(Stop(16648), Timestamp.parse("1970-01-01T11:03Z"), Stop(21607), Timestamp.parse("1970-01-01T11:49Z")),
                Connection(Stop(21607), Timestamp.parse("1970-01-01T11:51Z"), Stop(21575), Timestamp.parse("1970-01-01T12:47Z")),
                Connection(Stop(21575), Timestamp.parse("1970-01-01T13:08Z"), Stop(18610), Timestamp.parse("1970-01-01T13:35Z")),
                Connection(Stop(18610), Timestamp.parse("1970-01-01T13:37Z"), Stop(18705), Timestamp.parse("1970-01-01T13:50Z")),
                Connection(Stop(18705), Timestamp.parse("1970-01-01T14:00Z"), Stop(18756), Timestamp.parse("1970-01-01T14:27Z")),
                Connection(Stop(18756), Timestamp.parse("1970-01-01T14:35Z"), Stop(18741), Timestamp.parse("1970-01-01T14:39Z"))
            )
        }
    }
}