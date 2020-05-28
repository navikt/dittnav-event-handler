package no.nav.personbruker.dittnav.eventhandler.beskjed

import Beskjed
import java.time.ZonedDateTime

fun createBeskjedEvent(beskjed: Beskjed): no.nav.brukernotifikasjon.schemas.Beskjed {
    val build = no.nav.brukernotifikasjon.schemas.Beskjed.newBuilder()
            .setTidspunkt(beskjed.eventTidspunkt.toEpochSecond())
            .setSynligFremTil(UTCDateOrNullToTimestamp(beskjed.synligFremTil))
            .setFodselsnummer(beskjed.fodselsnummer)
            .setGrupperingsId(beskjed.grupperingsId)
            .setTekst(beskjed.tekst)
            .setLink(beskjed.link)
            .setSikkerhetsnivaa(beskjed.sikkerhetsnivaa)
    return build.build()
}

fun UTCDateOrNullToTimestamp(date: ZonedDateTime?): Long? {
    return date?.let { datetime -> date.toEpochSecond() }
}