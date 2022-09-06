package no.nav.personbruker.dittnav.eventhandler.common

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldHaveMinLength
import io.kotest.matchers.string.shouldNotContain
import org.junit.jupiter.api.Test

internal class TokenXUserTest {

    @Test
    fun `should return expected values`() {
        val expectedIdent = "12345"
        val expectedInnloggingsnivaa = 4

        val innloggetbruker = TokenXUserObjectMother.createInnloggetBruker(expectedIdent, expectedInnloggingsnivaa)

        innloggetbruker.ident shouldBe expectedIdent
        innloggetbruker.loginLevel shouldBe expectedInnloggingsnivaa
        innloggetbruker.jwt.token shouldHaveMinLength 1
    }

    @Test
    fun `should create authentication header`() {
        val innloggetBruker = TokenXUserObjectMother.createInnloggetBruker()

        val generatedAuthHeader = innloggetBruker.createAuthenticationHeader()

        generatedAuthHeader shouldBe "Bearer ${innloggetBruker.jwt.token}"
    }

    @Test
    fun `should not include sensitive values in the output for the toString method`() {
        val innloggetBruker = TokenXUserObjectMother.createInnloggetBruker()

        val outputOfToString = innloggetBruker.toString()

        outputOfToString shouldContain innloggetBruker.loginLevel.toString()
        outputOfToString shouldNotContain innloggetBruker.ident
        outputOfToString shouldNotContain innloggetBruker.jwt.token
    }
}
