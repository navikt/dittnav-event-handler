package no.nav.personbruker.dittnav.eventhandler.beskjed

import org.amshove.kluent.`should contain`
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

class BeskjedTest {

    @Test
    fun `skal returnere maskerte data fra toString-metoden`() {
        val beskjed = BeskjedObjectMother.createBeskjed(eventId = "dummyEventId1", fodselsnummer = "dummmyFnr1")
        val beskjedAsString = beskjed.toString()
        beskjedAsString `should contain` "fodselsnummer=***"
        beskjedAsString `should contain` "tekst=***"
        beskjedAsString `should contain` "link=***"
        beskjedAsString `should contain` "systembruker=x-dittnav"
    }
}
