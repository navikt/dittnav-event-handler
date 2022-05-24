package no.nav.personbruker.dittnav.eventhandler.done

import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

class DoneTest {

    @Test
    fun `skal returnere maskerte data fra toString-metoden`() {
        val beskjed = DoneObjectMother.createDone(systembruker = "x-dittnav", eventTidspunkt = ZonedDateTime.now(), fodselsnummer = "123", eventId = "456", grupperingsId = "1", forstBehandlet = ZonedDateTime.now())
        val beskjedAsString = beskjed.toString()
        beskjedAsString shouldContain "fodselsnummer=***"
    }
}
