package no.nav.personbruker.dittnav.eventhandler.done

import no.nav.brukernotifikasjon.schemas.Nokkel
import no.nav.brukernotifikasjon.schemas.builders.DoneBuilder
import no.nav.brukernotifikasjon.schemas.builders.NokkelBuilder
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime

fun createDoneEvent(fodselsnummer: String, grupperingsId: String): no.nav.brukernotifikasjon.schemas.Done {
    val now = LocalDateTime.now()
    val build = DoneBuilder()
            .withFodselsnummer(fodselsnummer)
            .withTidspunkt(now)
            .withGrupperingsId(grupperingsId)
    return build.build()
}

fun createBackupDoneEvent(fodselsnummer: String, grupperingsId: String, sistOppdatert: ZonedDateTime): no.nav.brukernotifikasjon.schemas.Done {
    val build = DoneBuilder()
            .withFodselsnummer(fodselsnummer)
            .withTidspunkt(LocalDateTime.ofInstant(Instant.ofEpochMilli(sistOppdatert.toEpochSecond()), ZoneOffset.UTC))
            .withGrupperingsId(grupperingsId)
    return build.build()
}

fun createKeyForEvent(eventId: String, systembruker: String): Nokkel {
    return NokkelBuilder()
            .withEventId(eventId)
            .withSystembruker(systembruker)
            .build()
}
