package no.nav.personbruker.dittnav.eventhandler.statusoppdatering

import org.amshove.kluent.`should contain`
import org.junit.jupiter.api.Test

internal class StatusoppdateringTest {

    @Test
    fun `skal returnere maskerte data fra toString-metoden`() {
        val statusoppdatering = StatusoppdateringObjectMother.createStatusoppdatering(1, "dummyEventId1", "dummmyFnr1", "dummyStatusGlobal1", "dummyStatusIntern1", "dummySakstema1")
        val statusoppdateringAsString = statusoppdatering.toString()
        statusoppdateringAsString `should contain` "fodselsnummer=***"
        statusoppdateringAsString `should contain` "link=***"
        statusoppdateringAsString `should contain` "systembruker=***"
        statusoppdateringAsString `should contain` "statusGlobal=***"
        statusoppdateringAsString `should contain` "statusIntern=***"
        statusoppdateringAsString `should contain` "sakstema=***"
    }
}