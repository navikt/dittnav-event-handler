package no.nav.personbruker.dittnav.eventhandler.statusoppdatering

import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Test

internal class StatusoppdateringTest {

    @Test
    fun `skal returnere maskerte data fra toString-metoden`() {
        val statusoppdatering = StatusoppdateringObjectMother.createStatusoppdatering(1, "dummyEventId1", "dummmyFnr1", "dummyStatusGlobal1", "dummyStatusIntern1", "dummySakstema1", "x-dittnav")
        val statusoppdateringAsString = statusoppdatering.toString()
        statusoppdateringAsString shouldContain "fodselsnummer=***"
        statusoppdateringAsString shouldContain "link=***"
        statusoppdateringAsString shouldContain "systembruker=x-dittnav"
    }
}