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

class StatusoppdateringQueriesTest {

    private val database = H2Database()

    private val bruker = InnloggetBrukerObjectMother.createInnloggetBruker("12345")
    private val dummyStatusGlobal = "dummyStatusGlobal"
    private val dummyStatusIntern = "dummyStatusIntern"
    private val dummySakstema = "dummySakstema"

    private val statusoppdatering1 = StatusoppdateringObjectMother.createStatusoppdatering(id = 1, eventId = "1", fodselsnummer = "12345", statusGlobal = "$dummyStatusGlobal+1", statusIntern = "$dummyStatusIntern+1", sakstema = "$dummySakstema+1")
    private val statusoppdatering2 = StatusoppdateringObjectMother.createStatusoppdatering(id = 2, eventId = "12", fodselsnummer = "12345", statusGlobal = "$dummyStatusGlobal+2", statusIntern = "$dummyStatusIntern+2", sakstema = "$dummySakstema+2")
    private val statusoppdatering3 = StatusoppdateringObjectMother.createStatusoppdatering(id = 3, eventId = "123", fodselsnummer = "12345", statusGlobal = "$dummyStatusGlobal+3", statusIntern = "$dummyStatusIntern+3", sakstema = "$dummySakstema+3")
    private val statusoppdatering4 = StatusoppdateringObjectMother.createStatusoppdatering(id = 4, eventId = "1234", fodselsnummer = "2345", statusGlobal = "$dummyStatusGlobal+4", statusIntern = "$dummyStatusIntern+4", sakstema = "$dummySakstema+4")

    @BeforeEach
    fun `populer testdata`() {
        createStatusoppdatering(listOf(statusoppdatering1, statusoppdatering2, statusoppdatering3, statusoppdatering4))
        createSystembruker(systembruker = "x-dittnav", produsentnavn = "dittnav")
    }

    @AfterEach
    fun `slett testdata`() {
        deleteStatusoppdatering(listOf(statusoppdatering1, statusoppdatering2, statusoppdatering3, statusoppdatering4))
        deleteSystembruker(systembruker = "x-dittnav")
    }

    @Test
    fun `Finn alle cachede Statusoppdatering-eventer for fodselsnummer`() {

        runBlocking {
            database.dbQuery {
                getAllStatusoppdateringForInnloggetBruker(bruker)
            }.size `should be equal to` 3
        }
    }

    @Test
    fun `Finn alle cachede Statusoppdatering-eventer`() {
        runBlocking {
            database.dbQuery {
                getAllStatusoppdateringEvents()
            }.size `should be equal to` 4
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
        var statusoppdateringMedAnnenProdusent = StatusoppdateringObjectMother.createStatusoppdateringWithSystembruker(id = 5, systembruker = "ukjent-systembruker")
        createStatusoppdatering(listOf(statusoppdateringMedAnnenProdusent))
        val statusoppdatering = runBlocking {
            database.dbQuery {
                getAllStatusoppdateringForInnloggetBruker(InnloggetBrukerObjectMother.createInnloggetBruker("112233"))
            }.first()
        }
        statusoppdatering.produsent `should be equal to` ""
        deleteStatusoppdatering(listOf(statusoppdateringMedAnnenProdusent))
    }

    private fun createStatusoppdatering(statusoppdateringer: List<Statusoppdatering>) {
        runBlocking {
            database.dbQuery { createStatusoppdatering(statusoppdateringer) }
        }
    }

    private fun deleteStatusoppdatering(statusoppdateringer: List<Statusoppdatering>) {
        runBlocking {
            database.dbQuery { deleteStatusoppdatering(statusoppdateringer) }
        }
    }

    private fun createSystembruker(systembruker: String, produsentnavn: String) {
        runBlocking {
            database.dbQuery { createProdusent(systembruker, produsentnavn) }
        }
    }

    private fun deleteSystembruker(systembruker: String) {
        runBlocking {
            database.dbQuery { deleteProdusent(systembruker) }
        }
    }
}
