package no.nav.personbruker.dittnav.eventaggregator.database.entity

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventaggregator.database.H2Database
import no.nav.personbruker.dittnav.eventhandler.database.entity.getOppgaveByAktorid
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object OppgaveQueriesTest : Spek({

    val database = H2Database()

    describe("Returnerer cachede Oppgave-eventer") {
        it("Finner cachede Oppgave-eventer for aktørID") {
            runBlocking {
                assertThat(database.dbQuery { getOppgaveByAktorid("12345") })
                        .hasSize(2)
            }

        }
        it("Returnerer tom liste hvis Oppgave-eventer for aktørID ikke finnes") {
            runBlocking {
                assertThat(database.dbQuery { getOppgaveByAktorid("finnesikke") })
                        .isEmpty()
            }
        }
        it("Returnerer tom liste hvis Oppgave-eventer hvis tom aktørID") {
            runBlocking {
                assertThat(database.dbQuery { getOppgaveByAktorid("") })
                        .isEmpty()
            }
        }
    }
})
