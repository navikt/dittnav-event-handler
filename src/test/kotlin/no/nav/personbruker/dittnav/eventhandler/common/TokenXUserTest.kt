package no.nav.personbruker.dittnav.eventhandler.common

import org.amshove.kluent.*
import org.junit.jupiter.api.Test

internal class TokenXUserTest {

    @Test
    fun `should return expected values`() {
        val expectedIdent = "12345"
        val expectedInnloggingsnivaa = 4

        val innloggetbruker = TokenXUserObjectMother.createInnloggetBruker(expectedIdent, expectedInnloggingsnivaa)

        innloggetbruker.ident `should be equal to` expectedIdent
        innloggetbruker.loginLevel `should be equal to` expectedInnloggingsnivaa
        innloggetbruker.jwt.token.`should not be null or empty`()
    }

    @Test
    fun `should create authentication header`() {
        val innloggetBruker = TokenXUserObjectMother.createInnloggetBruker()

        val generatedAuthHeader = innloggetBruker.createAuthenticationHeader()

        generatedAuthHeader `should be equal to` "Bearer ${innloggetBruker.jwt.token}"
    }

    @Test
    fun `should not include sensitive values in the output for the toString method`() {
        val innloggetBruker = TokenXUserObjectMother.createInnloggetBruker()

        val outputOfToString = innloggetBruker.toString()

        outputOfToString `should contain`(innloggetBruker.loginLevel.toString())
        outputOfToString `should not contain`(innloggetBruker.ident)
        outputOfToString `should not contain`(innloggetBruker.jwt.token)
    }

}
