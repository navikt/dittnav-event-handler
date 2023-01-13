package no.nav.personbruker.dittnav.eventhandler.oppgave

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Test

class OppgaveTest {

    @Test
    fun `skal returnere maskerte data fra toString-metoden`() {
        val oppgave = OppgaveObjectMother.createOppgave(fristUtløpt = null)
        val oppgaveAsString = oppgave.toString()
        oppgaveAsString shouldContain "fodselsnummer=***"
        oppgaveAsString shouldContain "tekst=***"
        oppgaveAsString shouldContain "link=***"
        oppgaveAsString shouldContain "systembruker=x-dittnav"
    }
    @Test
    fun `skal transformere til DTO`() {
        val oppgave = OppgaveObjectMother.createOppgave(fristUtløpt = null)
        val oppgaveDTO = oppgave.toDTO()
        oppgaveDTO.fodselsnummer shouldBe oppgave.fodselsnummer
        oppgaveDTO.grupperingsId shouldBe oppgave.grupperingsId
        oppgaveDTO.eventId shouldBe oppgave.eventId
        oppgaveDTO.produsent shouldBe oppgave.appnavn
        oppgaveDTO.sikkerhetsnivaa shouldBe oppgave.sikkerhetsnivaa
        oppgaveDTO.sistOppdatert shouldBe oppgave.sistOppdatert
        oppgaveDTO.tekst shouldBe oppgave.tekst
        oppgaveDTO.link shouldBe oppgave.link
        oppgaveDTO.aktiv shouldBe oppgave.aktiv
        oppgaveDTO.forstBehandlet shouldBe oppgave.forstBehandlet
    }
}
