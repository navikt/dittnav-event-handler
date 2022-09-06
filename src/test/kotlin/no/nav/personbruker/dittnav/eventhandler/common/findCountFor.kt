package no.nav.personbruker.dittnav.eventhandler.common

import no.nav.personbruker.dittnav.eventhandler.statistics.EventCountForProducer

fun List<EventCountForProducer>.findCountFor(namespace: String, appName: String): Int {
    return find { eventCount ->
        eventCount.namespace == namespace && eventCount.appName == appName
    }?.count ?: 0
}
