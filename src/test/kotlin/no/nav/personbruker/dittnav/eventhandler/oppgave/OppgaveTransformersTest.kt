package no.nav.personbruker.dittnav.eventhandler.oppgave

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class OppgaveTransformersTest {

    @Test
    fun `skal transformere til DTO`() {
        val oppgave = OppgaveObjectMother.createOppgave()
        val oppgaveDTO = oppgave.toDTO()
        oppgaveDTO.fodselsnummer shouldBe oppgave.fodselsnummer
        oppgaveDTO.grupperingsId shouldBe oppgave.grupperingsId
        oppgaveDTO.eventId shouldBe oppgave.eventId
        oppgaveDTO.eventTidspunkt shouldBe oppgave.eventTidspunkt
        oppgaveDTO.produsent shouldBe oppgave.appnavn
        oppgaveDTO.sikkerhetsnivaa shouldBe oppgave.sikkerhetsnivaa
        oppgaveDTO.sistOppdatert shouldBe oppgave.sistOppdatert
        oppgaveDTO.tekst shouldBe oppgave.tekst
        oppgaveDTO.link shouldBe oppgave.link
        oppgaveDTO.aktiv shouldBe oppgave.aktiv
    }
}
