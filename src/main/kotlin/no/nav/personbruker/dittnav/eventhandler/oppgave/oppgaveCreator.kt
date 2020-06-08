package no.nav.personbruker.dittnav.eventhandler.oppgave

import no.nav.personbruker.dittnav.eventhandler.common.validation.*

fun createOppgaveEvent(oppgave: Oppgave): no.nav.brukernotifikasjon.schemas.Oppgave {
    val build = no.nav.brukernotifikasjon.schemas.Oppgave.newBuilder()
            .setFodselsnummer(validateNonNullFieldMaxLength(oppgave.fodselsnummer, "fodselsnummer", 11))
            .setGrupperingsId(validateNonNullFieldMaxLength(oppgave.grupperingsId, "grupperingsId", 100))
            .setLink(validateMaxLength(oppgave.link, "link", 200))
            .setSikkerhetsnivaa(validateSikkerhetsnivaa(oppgave.sikkerhetsnivaa))
            .setTekst(validateNonNullFieldMaxLength(oppgave.tekst, "tekst", 500))
            .setTidspunkt(zonedDateTimeToEpochMilli(oppgave.eventTidspunkt, "eventTidspunkt"))
    return build.build()
}