package no.nav.personbruker.dittnav.eventhandler.varsel;

import io.ktor.client.request.get
import io.ktor.server.testing.testApplication
import org.junit.jupiter.api.Test


class VarselApiTest {

    @Test
    fun testGetFetchVarselOnbehalfofAktive() = testApplication {
        application {
        }
        client.get("/fetch/varsel/on-behalf-of/aktive").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testGetFetchVarselOnbehalfofInaktive() = testApplication {
        application {

        }
        client.get("/fetch/varsel/on-behalf-of/inaktive").apply {
            TODO("Please write your test here")
        }
    }
}