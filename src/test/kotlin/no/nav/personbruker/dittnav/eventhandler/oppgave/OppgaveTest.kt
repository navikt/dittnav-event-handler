package no.nav.personbruker.dittnav.eventhandler.oppgave

import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Test

class OppgaveTest {

    @Test
    fun `skal returnere maskerte data fra toString-metoden`() {
        val oppgave = OppgaveObjectMother.createOppgave()
        val oppgaveAsString = oppgave.toString()
        oppgaveAsString shouldContain "fodselsnummer=***"
        oppgaveAsString shouldContain "tekst=***"
        oppgaveAsString shouldContain "link=***"
        oppgaveAsString shouldContain "systembruker=x-dittnav"
    }
}
