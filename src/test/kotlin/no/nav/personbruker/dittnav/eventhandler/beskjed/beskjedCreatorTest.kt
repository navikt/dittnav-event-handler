package no.nav.personbruker.dittnav.eventhandler.beskjed

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.done.createKeyForEvent
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test

class beskjedCreator {
    private val fodselsnummer = "123"
    private val eventId = "11"

    @Test
    fun `should create beskjed-event`() {
        val beskjed = BeskjedObjectMother.createBeskjed(1, eventId, fodselsnummer, null, "123", true)
        runBlocking {
            val beskjedEvent = createBeskjedEvent(beskjed)
            beskjedEvent.getFodselsnummer() `should be equal to` fodselsnummer
        }
    }
    @Test
    fun `should create beskjed-key`() {
        val beskjed = BeskjedObjectMother.createBeskjed(1, eventId, fodselsnummer, null, "123", true)
        runBlocking {
            val keyEvent = createKeyForEvent(beskjed.eventId, beskjed.systembruker)
            keyEvent.getEventId() `should be equal to` beskjed.eventId
            keyEvent.getSystembruker() `should be equal to` beskjed.systembruker
        }
    }
}
