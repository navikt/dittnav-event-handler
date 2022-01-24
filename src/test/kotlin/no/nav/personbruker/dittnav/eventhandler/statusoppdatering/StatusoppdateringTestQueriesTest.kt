package no.nav.personbruker.dittnav.eventhandler.statusoppdatering

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.TokenXUserObjectMother
import no.nav.personbruker.dittnav.eventhandler.common.database.H2Database
import no.nav.personbruker.dittnav.eventhandler.common.database.createProdusent
import no.nav.personbruker.dittnav.eventhandler.common.database.deleteProdusent
import org.amshove.kluent.`should be empty`
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class StatusoppdateringTestQueriesTest {

    private val database = H2Database()
    private val bruker = TokenXUserObjectMother.createInnloggetBruker("12345")
    private val statusoppdateringEvents = StatusoppdateringObjectMother.getStatusoppdateringEvents(bruker)
    private val grupperingsid = "100${bruker.ident}"
    private val produsent = "x-dittnav-produsent"

    @BeforeEach
    fun `populer testdata`() {
        runBlocking {
            database.dbQuery { createStatusoppdatering(statusoppdateringEvents) }
            database.dbQuery { createProdusent(systembruker = "x-dittnav", produsentnavn = "x-dittnav-produsent") }
            database.dbQuery { createProdusent(systembruker = "y-dittnav", produsentnavn = "y-dittnav-produsent") }
        }
    }

    @AfterEach
    fun `slett testdata`() {
        runBlocking {
            database.dbQuery { deleteStatusoppdatering(statusoppdateringEvents) }
            database.dbQuery { deleteProdusent(systembruker = "x-dittnav") }
            database.dbQuery { deleteProdusent(systembruker = "y-dittnav") }
        }
    }

    @Test
    fun `Finn alle cachede Statusoppdatering-eventer for fodselsnummer`() {
        runBlocking {
            database.dbQuery {
                getAllGroupedStatusoppdateringEventsByIds(bruker, grupperingsid, produsent)
            }.size `should be equal to` 3
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
        val brukerSomIkkeFinnes = TokenXUserObjectMother.createInnloggetBruker("0")
        val grupperingsid = "100${brukerSomIkkeFinnes.ident}"

        runBlocking {
            database.dbQuery {
                getAllGroupedStatusoppdateringEventsByIds(brukerSomIkkeFinnes, grupperingsid, produsent)
            }.`should be empty`()
        }
    }

    @Test
    fun `Returnerer tom liste hvis fodselsnummer er tomt`() {
        val fodselsnummerMangler = TokenXUserObjectMother.createInnloggetBruker("")
        val grupperingsid = "100${fodselsnummerMangler.ident}"

        runBlocking {
            database.dbQuery {
                getAllGroupedStatusoppdateringEventsByIds(fodselsnummerMangler, grupperingsid, produsent)
            }.`should be empty`()
        }
    }

    @Test
    fun `Returnerer lesbart navn for produsent som kan eksponeres for alle eventer`() {
        runBlocking {
            val statusoppdatering = database.dbQuery {
                getAllGroupedStatusoppdateringEventsByIds(bruker, grupperingsid, produsent) }.first()
            statusoppdatering.produsent `should be equal to` "x-dittnav-produsent"
        }
    }

    @Test
    fun `Returnerer en tom liste hvis produsent ikke matcher statusoppdatering-eventet`() {
        val noMatchProdusent = "dummyProdusent"
        runBlocking {
            database.dbQuery {
                getAllGroupedStatusoppdateringEventsByIds(bruker, grupperingsid, noMatchProdusent)
            }.`should be empty`()
        }
    }

    @Test
    fun `Returnerer en tom liste hvis grupperingsid ikke matcher oppgave-eventet`() {
        val noMatchGrupperingsid = "dummyGrupperingsid"
        runBlocking {
            database.dbQuery {
                getAllGroupedStatusoppdateringEventsByIds(bruker, noMatchGrupperingsid, produsent)
            }.`should be empty`()
        }
    }

    @Test
    fun `Returnerer en liste av alle grupperte statusoppdaterings-eventer basert paa systembruker`() {
        runBlocking {
            val groupedEventsBySystemuser = database.dbQuery { getAllGroupedStatusoppdateringEventsBySystemuser() }

            groupedEventsBySystemuser.size `should be equal to` 2
        }
    }

    @Test
    fun `Returnerer en liste av alle grupperte statusoppdaterings-eventer basert paa produsent`() {
        runBlocking {
            val groupedEventsBySystemuser = database.dbQuery { getAllGroupedStatusoppdateringEventsByProducer() }

            groupedEventsBySystemuser.size `should be equal to` 2
        }
    }

}
