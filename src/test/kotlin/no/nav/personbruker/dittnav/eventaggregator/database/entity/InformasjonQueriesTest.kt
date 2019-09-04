package no.nav.personbruker.dittnav.eventaggregator.database.entity

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventaggregator.database.H2Database
import no.nav.personbruker.dittnav.eventhandler.database.entity.getInformasjonByAktorid
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object InformasjonQueriesTest : Spek({

    val database = H2Database()

    describe("Returnerer cachede Informasjon-eventer") {
        it("Finner cachede Informasjon-eventer for aktørID") {
            runBlocking {
                assertThat(database.dbQuery { getInformasjonByAktorid("12345") })
                        .hasSize(2)
            }

        }
        it("Returnerer tom liste hvis Informasjon-eventer for aktørID ikke finnes") {
            runBlocking {
                assertThat(database.dbQuery { getInformasjonByAktorid("finnesikke") })
                        .isEmpty()
            }
        }
        it("Returnerer tom liste hvis Informasjon-eventer hvis tom aktørID") {
            runBlocking {
                assertThat(database.dbQuery { getInformasjonByAktorid("") })
                        .isEmpty()
            }
        }
    }
})