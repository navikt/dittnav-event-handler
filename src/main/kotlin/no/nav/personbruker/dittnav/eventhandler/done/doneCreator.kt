package no.nav.personbruker.dittnav.eventhandler.done

import no.nav.brukernotifikasjon.schemas.input.DoneInput
import no.nav.brukernotifikasjon.schemas.input.NokkelInput
import java.time.ZoneOffset
import java.time.ZonedDateTime

fun createDoneEvent(sistOppdatert: ZonedDateTime = ZonedDateTime.now()): DoneInput {
    return DoneInput.newBuilder()
        .setTidspunkt(sistOppdatert.toInstant().toEpochMilli())
        .build()
}

fun createKeyForEvent(eventId: String, grupperingsId: String, fodselsnummer: String, namespace: String, appnavn: String): NokkelInput {
    return NokkelInput.newBuilder()
        .setEventId(eventId)
        .setGrupperingsId(grupperingsId)
        .setFodselsnummer(fodselsnummer)
        .setNamespace(namespace)
        .setAppnavn(appnavn)
        .build()
}
