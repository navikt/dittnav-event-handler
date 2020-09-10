package no.nav.personbruker.dittnav.eventhandler.statusoppdatering

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.InnloggetBrukerObjectMother
import no.nav.personbruker.dittnav.eventhandler.common.database.H2Database
import no.nav.personbruker.dittnav.eventhandler.common.database.createProdusent
import no.nav.personbruker.dittnav.eventhandler.common.database.deleteProdusent
import org.amshove.kluent.`should be empty`
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class StatusoppdateringTestQueries {

    private val database = H2Database()
    private val bruker = InnloggetBrukerObjectMother.createInnloggetBruker("12345")
    private val statusoppdateringEvents = StatusoppdateringObjectMother.getStatusoppdateringEvents(bruker)

    @BeforeEach
    fun `populer testdata`() {
        runBlocking {
            database.dbQuery { createStatusoppdatering(statusoppdateringEvents) }
            database.dbQuery { createProdusent(systembruker = "x-dittnav", produsentnavn = "dittnav") }
        }
    }

    @AfterEach
    fun `slett testdata`() {
        runBlocking {
            database.dbQuery { deleteStatusoppdatering(statusoppdateringEvents) }
            database.dbQuery { deleteProdusent(systembruker = "x-dittnav") }
        }
    }

    @Test
    fun `Finn alle cachede Statusoppdatering-eventer for fodselsnummer`() {
        runBlocking {
            database.dbQuery { getAllStatusoppdateringForInnloggetBruker(bruker) }.size `should be equal to` 4
        }
    }

    @Test
    fun `Finn alle cachede Statusoppdatering-eventer`() {
        val statusoppdateringMedNyBruker = StatusoppdateringObjectMother.createStatusoppdateringWithFodselsnummer(id = 5, fodselsnummer = "123")

        runBlocking {
            database.dbQuery { createStatusoppdatering(listOf(statusoppdateringMedNyBruker)) }
            database.dbQuery { getAllStatusoppdateringEvents() }.size `should be equal to` 5
        }
    }

    @Test
    fun `Returnerer tom liste hvis Statusoppdatering-eventer for fodselsnummer ikke finnes`() {
        val brukerSomIkkeFinnes = InnloggetBrukerObjectMother.createInnloggetBruker("0")

        runBlocking {
            database.dbQuery { getAllStatusoppdateringForInnloggetBruker(brukerSomIkkeFinnes) }.`should be empty`()
        }
    }

    @Test
    fun `Returnerer tom liste hvis fodselsnummer er tomt`() {
        val fodselsnummerMangler = InnloggetBrukerObjectMother.createInnloggetBruker("")

        runBlocking {
            database.dbQuery { getAllStatusoppdateringForInnloggetBruker(fodselsnummerMangler) }.`should be empty`()
        }
    }

    @Test
    fun `Returnerer lesbart navn for produsent som kan eksponeres for alle eventer`() {
        runBlocking {
            val statusoppdatering = database.dbQuery { getAllStatusoppdateringForInnloggetBruker(bruker) }.first()
            statusoppdatering.produsent `should be equal to` "dittnav"
        }
    }

    @Test
    fun `Returnerer tom streng for produsent hvis eventet er produsert av systembruker vi ikke har i systembruker-tabellen`() {
        var statusoppdateringMedAnnenProdusent =
                StatusoppdateringObjectMother.createStatusoppdateringWithSystembruker(id = 6, systembruker = "ukjent-systembruker")

        val statusoppdatering = runBlocking {
            database.dbQuery { createStatusoppdatering(listOf(statusoppdateringMedAnnenProdusent)) }
            database.dbQuery { getAllStatusoppdateringForInnloggetBruker(InnloggetBrukerObjectMother.createInnloggetBruker("112233")) }.first()
        }
        statusoppdatering.produsent `should be equal to` ""

        runBlocking {
            database.dbQuery { deleteStatusoppdatering(listOf(statusoppdateringMedAnnenProdusent)) }
        }
    }

}

