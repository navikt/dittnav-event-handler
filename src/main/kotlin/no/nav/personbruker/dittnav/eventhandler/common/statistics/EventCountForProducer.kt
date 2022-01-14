package no.nav.personbruker.dittnav.eventhandler.common.statistics

import kotlinx.serialization.Serializable

@Serializable
data class EventCountForProducer(
    val namespace: String,
    val appName: String,
    val count: Int
)
