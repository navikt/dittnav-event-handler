package no.nav.personbruker.dittnav.eventhandler.beskjed

import Beskjed
import no.nav.personbruker.dittnav.eventhandler.common.validation.*

fun createBeskjedEvent(beskjed: Beskjed): no.nav.brukernotifikasjon.schemas.Beskjed {
    val build = no.nav.brukernotifikasjon.schemas.Beskjed.newBuilder()
            .setTidspunkt(zonedDateTimeToEpochMilli(beskjed.eventTidspunkt, "eventTidspunkt"))
            .setSynligFremTil(UTCDateToTimestampOrNull(beskjed.synligFremTil))
            .setFodselsnummer(validateNonNullFieldMaxLength(beskjed.fodselsnummer, "fodselsnummer", 11))
            .setGrupperingsId(validateNonNullFieldMaxLength(beskjed.grupperingsId, "grupperingsId", 100))
            .setTekst(validateNonNullFieldMaxLength(beskjed.tekst, "tekst", 500))
            .setLink(validateMaxLength(beskjed.link, "link", 200))
            .setSikkerhetsnivaa(validateSikkerhetsnivaa(beskjed.sikkerhetsnivaa))
    return build.build()
}

