package no.nav.personbruker.dittnav.eventhandler.common

import io.mockk.coEvery
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test

internal class InnloggetBrukerTest {

    val innloggetBruker = InnloggetBrukerObjectMother.createInnloggetBruker()

    @Test
    fun `should return string with ident from pid token claim`() {
        val expectedIdent = "12345"
        val subClaimThatIsNotAnIdent ="6b06bb69-4c9c-46cd-9c5b-dda8fd5ee1be"

        coEvery { innloggetBruker.token.jwtTokenClaims.getStringClaim("sub")} returns subClaimThatIsNotAnIdent
        coEvery { innloggetBruker.token.jwtTokenClaims.getStringClaim("pid")} returns expectedIdent

        runBlocking {
            val actualIdent = innloggetBruker.getIdent()
            actualIdent `should be equal to` expectedIdent
        }
    }

    @Test
    fun `should return string with ident from sub token claim, valid length`() {
        val expectedIdent = "12345678901"

        coEvery { innloggetBruker.token.jwtTokenClaims.getStringClaim("sub")} returns expectedIdent

        runBlocking {
            val actualIdent = innloggetBruker.getIdent()
            actualIdent `should be equal to` expectedIdent
        }
    }

    @Test
    fun `should return string with ident from sub token claim, minimal length`() {
        val expectedIdent = "1"

        coEvery { innloggetBruker.token.jwtTokenClaims.getStringClaim("sub")} returns expectedIdent

        runBlocking {
            val actualIdent = innloggetBruker.getIdent()
            actualIdent `should be equal to` expectedIdent
        }
    }

}
