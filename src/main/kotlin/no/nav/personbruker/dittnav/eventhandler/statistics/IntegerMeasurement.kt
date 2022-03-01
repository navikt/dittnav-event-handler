package no.nav.personbruker.dittnav.eventhandler.statistics

import kotlinx.serialization.Serializable

@Serializable
data class IntegerMeasurement (
    val min: Int,
    val max: Int,
    val avg: Double,
    val percentile25: Int,
    val percentile50: Int,
    val percentile75: Int,
    val percentile90: Int,
    val percentile99: Int
)

typealias EventsPerUser = IntegerMeasurement
typealias ActiveEventsPerUser = IntegerMeasurement
typealias EventsPerGroupId = IntegerMeasurement
typealias GroupIdsPerUser = IntegerMeasurement
typealias ExpiredEventsPerUser = IntegerMeasurement
typealias VisibleEventsPerUser = IntegerMeasurement
typealias EventTextLength = IntegerMeasurement
