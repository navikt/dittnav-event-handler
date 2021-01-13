package no.nav.personbruker.dittnav.eventhandler.backup

import no.nav.brukernotifikasjon.schemas.builders.OppgaveBuilder
import no.nav.brukernotifikasjon.schemas.builders.util.ValidationUtil
import no.nav.personbruker.dittnav.eventhandler.oppgave.Oppgave

fun createOppgaveEvent(oppgave: Oppgave): no.nav.brukernotifikasjon.schemas.Oppgave {
    val build = OppgaveBuilder()
            .withFodselsnummer(oppgave.fodselsnummer)
            .withGrupperingsId(oppgave.grupperingsId)
            .withSikkerhetsnivaa(oppgave.sikkerhetsnivaa)
            .withTekst(oppgave.tekst)
            .withTidspunkt(oppgave.eventTidspunkt.toLocalDateTime())
            .withLink(ValidationUtil.validateLinkAndConvertToURL(oppgave.link))
    return build.build()
}
