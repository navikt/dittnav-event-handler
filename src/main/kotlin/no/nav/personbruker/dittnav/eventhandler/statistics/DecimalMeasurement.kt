package no.nav.personbruker.dittnav.eventhandler.statistics

import kotlinx.serialization.Serializable

@Serializable
data class DecimalMeasurement(
    val min: Double,
    val max: Double,
    val avg: Double,
    val percentile25: Double,
    val percentile50: Double,
    val percentile75: Double,
    val percentile90: Double,
    val percentile99: Double
)

typealias EventActiveRatePerUser = DecimalMeasurement
