package no.nav.personbruker.dittnav.eventhandler.done

import no.nav.brukernotifikasjon.schemas.builders.DoneInputBuilder
import no.nav.brukernotifikasjon.schemas.builders.NokkelInputBuilder
import no.nav.brukernotifikasjon.schemas.input.DoneInput
import no.nav.brukernotifikasjon.schemas.input.NokkelInput
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime

fun createDoneEvent(sistOppdatert: ZonedDateTime = ZonedDateTime.now()): DoneInput {
    return DoneInputBuilder()
        .withTidspunkt(LocalDateTime.ofInstant(Instant.ofEpochMilli(sistOppdatert.toEpochSecond()), ZoneOffset.UTC))
        .build()
}

fun createKeyForEvent(eventId: String, grupperingsId: String, fodselsnummer: String, namespace: String, appnavn: String): NokkelInput {
    return NokkelInputBuilder()
        .withEventId(eventId)
        .withGrupperingsId(grupperingsId)
        .withFodselsnummer(fodselsnummer)
        .withNamespace(namespace)
        .withAppnavn(appnavn)
        .build()
}
