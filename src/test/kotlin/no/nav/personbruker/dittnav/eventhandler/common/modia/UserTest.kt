package no.nav.personbruker.dittnav.eventhandler.common.modia

import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should not contain`
import org.junit.jupiter.api.Test

class UserTest {

    @Test
    fun `should return expected values`() {
        val expectedIdent = "12345"
        val userToFetchEventsFor = User(fodselsnummer = expectedIdent)
        userToFetchEventsFor.fodselsnummer `should be equal to` expectedIdent
    }

    @Test
    fun `should not include sensitive values in the output for the toString method`() {
        val userToFetchEventsFor = User("12345")
        val outputOfToString = userToFetchEventsFor.toString()
        outputOfToString `should not contain` (userToFetchEventsFor.fodselsnummer)
    }
}
