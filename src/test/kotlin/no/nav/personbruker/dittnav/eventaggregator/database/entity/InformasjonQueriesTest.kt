package no.nav.personbruker.dittnav.eventaggregator.database.entity

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventaggregator.database.H2Database
import no.nav.personbruker.dittnav.eventhandler.database.entity.informasjon.getInformasjonByAktorId
import org.junit.jupiter.api.Test
import org.amshove.kluent.*

class InformasjonQueriesTest {

    private val database = H2Database()

    @Test
    fun `Finner cachede Informasjon-eventer for aktørID`() {
        runBlocking {
            database.dbQuery { getInformasjonByAktorId("12345") }.size `should be equal to` 2
        }
    }

    @Test
    fun `Returnerer tom liste hvis Informasjon-eventer for aktørID ikke finnes`() {
        runBlocking {
            database.dbQuery { getInformasjonByAktorId("finnesikke") }.`should be empty`()
        }
    }


    @Test
    fun `Returnerer tom liste hvis Informasjon-eventer hvis tom aktørID`() {
        runBlocking {
            database.dbQuery { getInformasjonByAktorId("") }.`should be empty`()
        }
    }
}
