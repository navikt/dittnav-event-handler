package no.nav.personbruker.dittnav.eventhandler.oppgave

import no.nav.brukernotifikasjon.schemas.builders.OppgaveBuilder
import no.nav.brukernotifikasjon.schemas.builders.util.ValidationUtil
import no.nav.personbruker.dittnav.eventhandler.common.validation.zonedDateTimeToUTCLocalDate

fun createOppgaveEvent(oppgave: Oppgave): no.nav.brukernotifikasjon.schemas.Oppgave {
    val build = OppgaveBuilder()
            .withFodselsnummer(oppgave.fodselsnummer)
            .withGrupperingsId(oppgave.grupperingsId)
            .withSikkerhetsnivaa(oppgave.sikkerhetsnivaa)
            .withTekst(oppgave.tekst)
            .withTidspunkt(zonedDateTimeToUTCLocalDate(oppgave.eventTidspunkt))
            .withLink(ValidationUtil.validateLinkAndConvertToURL(oppgave.link))
    return build.build()
}
