package no.nav.personbruker.dittnav.eventhandler.done

import no.nav.brukernotifikasjon.schemas.Nokkel

fun createKeyForEvent(eventId: String, producer: String): Nokkel {
    return Nokkel.newBuilder()
            .setEventId(eventId)
            .setSystembruker(producer)
            .build()
}