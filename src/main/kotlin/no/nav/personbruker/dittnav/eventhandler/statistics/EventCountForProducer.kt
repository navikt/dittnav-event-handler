package no.nav.personbruker.dittnav.eventhandler.statistics

import kotlinx.serialization.Serializable

@Serializable
data class EventCountForProducer(
    val namespace: String,
    val appName: String,
    val count: Int
)
