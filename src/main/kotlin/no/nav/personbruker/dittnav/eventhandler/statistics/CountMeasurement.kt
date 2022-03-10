package no.nav.personbruker.dittnav.eventhandler.statistics

import kotlinx.serialization.Serializable

@Serializable
data class CountMeasurement(
    val count: Int
)
