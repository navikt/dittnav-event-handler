package no.nav.personbruker.dittnav.eventhandler.beskjed

import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Test

class BeskjedTest {

    @Test
    fun `skal returnere maskerte data fra toString-metoden`() {
        val beskjed = BeskjedObjectMother.createBeskjed(eventId = "dummyEventId1", fodselsnummer = "dummmyFnr1")
        val beskjedAsString = beskjed.toString()
        beskjedAsString shouldContain  "fodselsnummer=***"
        beskjedAsString shouldContain "tekst=***"
        beskjedAsString shouldContain "link=***"
        beskjedAsString shouldContain "systembruker=x-dittnav"
    }
}
