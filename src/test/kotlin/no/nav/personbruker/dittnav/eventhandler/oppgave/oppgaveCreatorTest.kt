package no.nav.personbruker.dittnav.eventhandler.oppgave

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.done.createKeyForEvent
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test

class beskjedCreator {
    private val fodselsnummer = "123"
    private val eventId = "11"

    @Test
    fun `should create oppgave-event`() {
        val oppgave = OppgaveObjectMother.createOppgave(1, eventId, fodselsnummer, true)
        runBlocking {
            val oppgaveEvent = createOppgaveEvent(oppgave)
            oppgaveEvent.getFodselsnummer() `should be equal to` fodselsnummer
        }
    }

    @Test
    fun `should create oppgave-key`() {
        val beskjed = OppgaveObjectMother.createOppgave(1, eventId, fodselsnummer, true)
        runBlocking {
            val keyEvent = createKeyForEvent(beskjed.eventId, beskjed.systembruker)
            keyEvent.getEventId() `should be equal to` beskjed.eventId
            keyEvent.getSystembruker() `should be equal to` beskjed.systembruker
        }
    }
}