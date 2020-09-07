package no.nav.personbruker.dittnav.eventhandler.statusOppdatering

import org.amshove.kluent.`should contain`
import org.junit.jupiter.api.Test

internal class StatusOppdateringTest {

    @Test
    fun `skal returnere maskerte data fra toString-metoden`() {
        val statusOppdatering = StatusOppdateringObjectMother.createStatusOppdatering(1, "dummyEventId1", "dummmyFnr1", "dummyStatusGlobal1", "dummyStatusIntern1", "dummySakstema1")
        val statusOppdateringAsString = statusOppdatering.toString()
        statusOppdateringAsString `should contain` "fodselsnummer=***"
        statusOppdateringAsString `should contain` "link=***"
        statusOppdateringAsString `should contain` "systembruker=***"
        statusOppdateringAsString `should contain` "statusGlobal=***"
        statusOppdateringAsString `should contain` "statusIntern=***"
        statusOppdateringAsString `should contain` "sakstema=***"
    }
}