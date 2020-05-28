package no.nav.personbruker.dittnav.eventhandler.done

import no.nav.brukernotifikasjon.schemas.Nokkel
import java.time.Instant

fun createDoneEvent(fodselsnummer: String, grupperingsId: String): no.nav.brukernotifikasjon.schemas.Done {
    val nowInMs = Instant.now().toEpochMilli()
    val build = no.nav.brukernotifikasjon.schemas.Done.newBuilder()
            .setFodselsnummer(fodselsnummer)
            .setTidspunkt(nowInMs)
            .setGrupperingsId(grupperingsId)
    return build.build()
}

fun createKeyForEvent(eventId: String, producer: String): Nokkel {
    return Nokkel.newBuilder()
            .setEventId(eventId)
            .setSystembruker(producer)
            .build()
}
