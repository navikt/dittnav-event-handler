package no.nav.personbruker.dittnav.eventhandler.statusOppdatering

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

class StatusOppdateringQueriesTest {

    private val database = H2Database()

    private val bruker = InnloggetBrukerObjectMother.createInnloggetBruker("12345")
    private val dummyStatusGlobal = "dummyStatusGlobal"
    private val dummyStatusIntern = "dummyStatusIntern"
    private val dummySakstema = "dummySakstema"

    private val statusOppdatering1 = StatusOppdateringObjectMother.createStatusOppdatering(id = 1, eventId = "1", fodselsnummer = "12345", statusGlobal = "$dummyStatusGlobal+1", statusIntern = "$dummyStatusIntern+1", sakstema = "$dummySakstema+1")
    private val statusOppdatering2 = StatusOppdateringObjectMother.createStatusOppdatering(id = 2, eventId = "12", fodselsnummer = "12345", statusGlobal = "$dummyStatusGlobal+2", statusIntern = "$dummyStatusIntern+2", sakstema = "$dummySakstema+2")
    private val statusOppdatering3 = StatusOppdateringObjectMother.createStatusOppdatering(id = 3, eventId = "123", fodselsnummer = "12345", statusGlobal = "$dummyStatusGlobal+3", statusIntern = "$dummyStatusIntern+3", sakstema = "$dummySakstema+3")
    private val statusOppdatering4 = StatusOppdateringObjectMother.createStatusOppdatering(id = 4, eventId = "1234", fodselsnummer = "2345", statusGlobal = "$dummyStatusGlobal+4", statusIntern = "$dummyStatusIntern+4", sakstema = "$dummySakstema+4")

    @BeforeEach
    fun `populer testdata`() {
        createStatusOppdatering(listOf(statusOppdatering1, statusOppdatering2, statusOppdatering3, statusOppdatering4))
        createSystembruker(systembruker = "x-dittnav", produsentnavn = "dittnav")
    }

    @AfterEach
    fun `slett testdata`() {
        deleteStatusOppdatering(listOf(statusOppdatering1, statusOppdatering2, statusOppdatering3, statusOppdatering4))
        deleteSystembruker(systembruker = "x-dittnav")
    }

    @Test
    fun `Finn alle cachede StatusOppdatering-eventer for fodselsnummer`() {

        runBlocking {
            database.dbQuery {
                getAllStatusOppdateringForInnloggetBruker(bruker)
            }.size `should be equal to` 3
        }
    }

    @Test
    fun `Finn alle cachede StatusOppdatering-eventer`() {
        runBlocking {
            database.dbQuery {
                getAllStatusOppdateringEvents()
            }.size `should be equal to` 4
        }
    }

    @Test
    fun `Returnerer tom liste hvis StatusOppdatering-eventer for fodselsnummer ikke finnes`() {
        val brukerSomIkkeFinnes = InnloggetBrukerObjectMother.createInnloggetBruker("0")
        runBlocking {
            database.dbQuery { getAllStatusOppdateringForInnloggetBruker(brukerSomIkkeFinnes) }.`should be empty`()
        }
    }

    @Test
    fun `Returnerer tom liste hvis fodselsnummer er tomt`() {
        val fodselsnummerMangler = InnloggetBrukerObjectMother.createInnloggetBruker("")
        runBlocking {
            database.dbQuery { getAllStatusOppdateringForInnloggetBruker(fodselsnummerMangler) }.`should be empty`()
        }
    }

    @Test
    fun `Returnerer lesbart navn for produsent som kan eksponeres for alle eventer`() {
        runBlocking {
            val statusOppdatering = database.dbQuery { getAllStatusOppdateringForInnloggetBruker(bruker) }.first()
            statusOppdatering.produsent `should be equal to` "dittnav"
        }
    }

    @Test
    fun `Returnerer tom streng for produsent hvis eventet er produsert av systembruker vi ikke har i systembruker-tabellen`() {
        var statusOppdateringMedAnnenProdusent = StatusOppdateringObjectMother.createStatusOppdateringWithSystembruker(id = 5, systembruker = "ukjent-systembruker")
        createStatusOppdatering(listOf(statusOppdateringMedAnnenProdusent))
        val statusOppdatering = runBlocking {
            database.dbQuery {
                getAllStatusOppdateringForInnloggetBruker(InnloggetBrukerObjectMother.createInnloggetBruker("112233"))
            }.first()
        }
        statusOppdatering.produsent `should be equal to` ""
        deleteStatusOppdatering(listOf(statusOppdateringMedAnnenProdusent))
    }

    private fun createStatusOppdatering(statusOppdateringer: List<StatusOppdatering>) {
        runBlocking {
            database.dbQuery { createStatusOppdatering(statusOppdateringer) }
        }
    }

    private fun deleteStatusOppdatering(statusOppdateringer: List<StatusOppdatering>) {
        runBlocking {
            database.dbQuery { deleteStatusOppdatering(statusOppdateringer) }
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

