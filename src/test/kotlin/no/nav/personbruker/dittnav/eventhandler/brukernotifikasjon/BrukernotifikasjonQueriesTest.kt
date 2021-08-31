package no.nav.personbruker.dittnav.eventhandler.brukernotifikasjon

import Beskjed
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedObjectMother
import no.nav.personbruker.dittnav.eventhandler.beskjed.createBeskjed
import no.nav.personbruker.dittnav.eventhandler.beskjed.deleteBeskjed
import no.nav.personbruker.dittnav.eventhandler.common.TokenXUserObjectMother
import no.nav.personbruker.dittnav.eventhandler.common.database.H2Database
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.ZonedDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BrukernotifikasjonQueriesTest {

    private val database = H2Database()

    private val brukerUtenEventer = TokenXUserObjectMother.createInnloggetBruker("123")
    private val brukerMedEventer = TokenXUserObjectMother.createInnloggetBruker("456")

    private val inaktivBeskjed = BeskjedObjectMother.createBeskjed(id = 1, eventId = "12", fodselsnummer = brukerMedEventer.ident,
            synligFremTil = ZonedDateTime.now().plusHours(1), uid = "11", aktiv = false)
    private val aktivBeskjed1 = BeskjedObjectMother.createBeskjed(id = 2, eventId = "34", fodselsnummer = brukerMedEventer.ident,
            synligFremTil = ZonedDateTime.now().plusHours(1), uid = "22", aktiv = true)
    private val aktivBeskjed2 = BeskjedObjectMother.createBeskjed(id = 3, eventId = "56", fodselsnummer = brukerMedEventer.ident,
            synligFremTil = ZonedDateTime.now().plusHours(1), uid = "33", aktiv = true)

    @BeforeAll
    fun `populer testdata`() {
        createBeskjed(listOf(aktivBeskjed1, aktivBeskjed2, inaktivBeskjed))
    }

    @AfterAll
    fun `slett testdata`() {
        deleteBeskjed(listOf(aktivBeskjed1, aktivBeskjed2, inaktivBeskjed))
    }

    @Test
    fun `Skal telle riktig for brukere uten eventer`() {
        runBlocking {
            database.dbQuery { getNumberOfBrukernotifikasjoner(brukerUtenEventer) } `should be equal to` 0
            database.dbQuery { getNumberOfBrukernotifikasjonerByActiveStatus(brukerUtenEventer, true) } `should be equal to` 0
            database.dbQuery { getNumberOfBrukernotifikasjonerByActiveStatus(brukerUtenEventer, false) } `should be equal to` 0
        }
    }

    @Test
    fun `Skal telle riktig totalantall av eventer`() {
        runBlocking {
            database.dbQuery { getNumberOfBrukernotifikasjoner(brukerMedEventer) } `should be equal to` 3
        }
    }

    @Test
    fun `Skal telle riktig antall aktive eventer`() {
        runBlocking {
            database.dbQuery { getNumberOfBrukernotifikasjonerByActiveStatus(brukerMedEventer, true) } `should be equal to` 2
        }
    }

    @Test
    fun `Skal telle riktig antall inaktive eventer`() {
        runBlocking {
            database.dbQuery { getNumberOfBrukernotifikasjonerByActiveStatus(brukerMedEventer, false) } `should be equal to` 1
        }
    }

    private fun createBeskjed(beskjeder: List<Beskjed>) {
        runBlocking {
            database.dbQuery { createBeskjed(beskjeder) }
        }
    }

    private fun deleteBeskjed(beskjeder: List<Beskjed>) {
        runBlocking {
            database.dbQuery { deleteBeskjed(beskjeder) }
        }
    }

}
