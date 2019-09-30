package no.nav.personbruker.dittnav.eventaggregator.database.entity

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventaggregator.database.H2Database
import no.nav.personbruker.dittnav.eventhandler.database.entity.oppgave.getOppgaveByAktorId
import org.junit.jupiter.api.Test
import org.amshove.kluent.*

class OppgaveQueriesTest {

    private val database = H2Database()

    @Test
    fun `Finner cachede Oppgave-eventer for aktørID`() {
        runBlocking {
            database.dbQuery { getOppgaveByAktorId("12345") }.size `should be equal to` 2
        }
    }

    @Test
    fun `Returnerer tom liste hvis Oppgave-eventer for aktørID ikke finnes`() {
        runBlocking {
            database.dbQuery { getOppgaveByAktorId("finnesikke") }.isEmpty()
        }
    }

    @Test
    fun `Returnerer tom liste hvis Oppgave-eventer hvis tom aktørID`() {
        runBlocking {
            database.dbQuery { getOppgaveByAktorId("") }.isEmpty()
        }
    }
}
