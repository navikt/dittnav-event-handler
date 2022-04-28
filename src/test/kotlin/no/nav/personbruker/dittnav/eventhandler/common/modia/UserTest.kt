package no.nav.personbruker.dittnav.eventhandler.common.modia

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotContain
import org.junit.jupiter.api.Test

class UserTest {

    @Test
    fun `should return expected values`() {
        val expectedIdent = "12345"
        val userToFetchEventsFor = User(fodselsnummer = expectedIdent)
        userToFetchEventsFor.fodselsnummer shouldBe expectedIdent
    }

    @Test
    fun `should not include sensitive values in the output for the toString method`() {
        val userToFetchEventsFor = User("12345")
        val outputOfToString = userToFetchEventsFor.toString()
        outputOfToString shouldNotContain (userToFetchEventsFor.fodselsnummer)
    }
}
