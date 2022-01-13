package no.nav.personbruker.dittnav.eventhandler.common.statistics

data class EventCountForProducer(
    val namespace: String,
    val appName: String,
    val count: Int
)
