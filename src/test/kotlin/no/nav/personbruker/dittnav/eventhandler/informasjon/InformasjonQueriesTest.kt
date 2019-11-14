package no.nav.personbruker.dittnav.eventhandler.informasjon

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.database.H2Database
import org.junit.jupiter.api.Test
import org.amshove.kluent.*

class InformasjonQueriesTest {

    private val database = H2Database()

    @Test
    fun `Finn alle cachede Informasjon-eventer for aktorID`() {
        runBlocking {
            database.dbQuery { getAllInformasjonByAktorId("12345") }.size `should be equal to` 3
        }
    }

    @Test
    fun `Finner kun aktive cachede Informasjon-eventer for aktorID`() {
        runBlocking {
            database.dbQuery { getActiveInformasjonByAktorId("12345") }.size `should be equal to` 2
        }
    }

    @Test
    fun `Returnerer tom liste hvis Informasjon-eventer for aktorID ikke finnes`() {
        runBlocking {
            database.dbQuery { getActiveInformasjonByAktorId("finnesikke") }.`should be empty`()
        }
    }


    @Test
    fun `Returnerer tom liste hvis Informasjon-eventer hvis tom aktorID`() {
        runBlocking {
            database.dbQuery { getActiveInformasjonByAktorId("") }.`should be empty`()
        }
    }
}
