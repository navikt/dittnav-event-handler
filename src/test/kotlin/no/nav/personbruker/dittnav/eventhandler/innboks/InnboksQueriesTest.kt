package no.nav.personbruker.dittnav.eventhandler.innboks

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.database.H2Database
import no.nav.personbruker.dittnav.eventhandler.common.database.createProdusent
import no.nav.personbruker.dittnav.eventhandler.common.database.deleteProdusent
import no.nav.personbruker.dittnav.eventhandler.common.findCountFor
import org.amshove.kluent.`should be empty`
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InnboksQueriesTest {

    private val database = H2Database()

    private val fodselsnummer1 = "12345"
    private val fodselsnummer2 = "67890"
    private val produsent = "x-dittnav-produsent"
    private val grupperingsid = "100${fodselsnummer1}"

    private val innboks1 = InnboksObjectMother.createInnboks(id = 1, eventId = "123", fodselsnummer = fodselsnummer1, aktiv = true, systembruker = "x-dittnav", namespace = "dummyNamespace", appnavn = "x-dittnav")
    private val innboks2 = InnboksObjectMother.createInnboks(id = 2, eventId = "345", fodselsnummer = fodselsnummer1, aktiv = true, systembruker = "x-dittnav", namespace = "dummyNamespace", appnavn = "x-dittnav")
    private val innboks3 = InnboksObjectMother.createInnboks(id = 3, eventId = "567", fodselsnummer = fodselsnummer2, aktiv = true, systembruker = "y-dittnav", namespace = "dummyNamespace", appnavn = "y-dittnav")
    private val innboks4 = InnboksObjectMother.createInnboks(id = 4, eventId = "789", fodselsnummer = fodselsnummer2, aktiv = false, systembruker = "x-dittnav", namespace = "dummyNamespace", appnavn = "x-dittnav")

    @BeforeAll
    fun `populer test-data`() {
        createInnboks(listOf(innboks1, innboks2, innboks3, innboks4))
        createSystembruker(systembruker = "x-dittnav", produsentnavn = "x-dittnav-produsent")
        createSystembruker(systembruker = "y-dittnav", produsentnavn = "y-dittnav-produsent")
    }

    @AfterAll
    fun `slett Innboks-eventer fra tabellen`() {
        deleteInnboks(listOf(innboks1, innboks2, innboks3, innboks4))
        deleteSystembruker(systembruker = "x-dittnav")
        deleteSystembruker(systembruker = "y-dittnav")
    }

    @Test
    fun `Finn alle cachede Innboks-eventer for fodselsnummer`() {
        runBlocking {
            database.dbQuery { getAllInnboksForInnloggetBruker(fodselsnummer1) }.size `should be equal to` 2
            database.dbQuery { getAllInnboksForInnloggetBruker(fodselsnummer2) }.size `should be equal to` 2
        }
    }

    @Test
    fun `Finn kun aktive cachede Innboks-eventer for fodselsnummer`() {
        runBlocking {
            database.dbQuery { getAktivInnboksForInnloggetBruker(fodselsnummer1) }.size `should be equal to` 2
            database.dbQuery { getAktivInnboksForInnloggetBruker(fodselsnummer2) }.size `should be equal to` 1
        }
    }

    @Test
    fun `Finn kun inaktive cachede Innboks-eventer for fodselsnummer`() {
        runBlocking {
            database.dbQuery { getInaktivInnboksForInnloggetBruker(fodselsnummer1) }.`should be empty`()
            database.dbQuery { getInaktivInnboksForInnloggetBruker(fodselsnummer2) }.size `should be equal to` 1
        }
    }

    @Test
    fun `Returnerer tom liste hvis Innboks-eventer for fodselsnummer ikke finnes`() {
        val brukerUtenEventer = "0"
        runBlocking {
            database.dbQuery { getAllInnboksForInnloggetBruker(brukerUtenEventer) }.size `should be equal to` 0
        }
    }

    @Test
    fun `Returnerer tom liste hvis fodselsnummer er tomt`() {
        val brukerUtenEventer = ""
        runBlocking {
            database.dbQuery { getAllInnboksForInnloggetBruker(brukerUtenEventer) }.size `should be equal to` 0
        }
    }

    @Test
    fun `Returnerer lesbart navn for produsent som kan eksponeres for aktive eventer`() {
        runBlocking {
            val innboks = database.dbQuery { getAktivInnboksForInnloggetBruker(fodselsnummer1) }.first()
            innboks.produsent `should be equal to` "x-dittnav-produsent"
        }
    }

    @Test
    fun `Returnerer lesbart navn for produsent som kan eksponeres for inaktive eventer`() {
        runBlocking {
            val innboks = database.dbQuery { getInaktivInnboksForInnloggetBruker(fodselsnummer2) }.first()
            innboks.produsent `should be equal to` "x-dittnav-produsent"
        }
    }

    @Test
    fun `Returnerer lesbart navn for produsent som kan eksponeres for alle eventer`() {
        runBlocking {
            val innboks = database.dbQuery { getAllInnboksForInnloggetBruker(fodselsnummer1) }.first()
            innboks.produsent `should be equal to` "x-dittnav-produsent"
        }
    }

    @Test
    fun `Returnerer tom streng for produsent hvis eventet er produsert av systembruker vi ikke har i systembruker-tabellen`() {
        var innboksMedAnnenProdusent = InnboksObjectMother.createInnboks(id = 5, eventId = "111", fodselsnummer = "112233", aktiv = true)
                .copy(systembruker = "ukjent-systembruker")
        createInnboks(listOf(innboksMedAnnenProdusent))
        val innboks = runBlocking {
            database.dbQuery {
                getAllInnboksForInnloggetBruker("112233")
            }.first()
        }
        innboks.produsent `should be equal to` ""
        deleteInnboks(listOf(innboksMedAnnenProdusent))
    }

    @Test
    fun `Returnerer en liste av alle grupperte Innboks-eventer`() {
        runBlocking {
            database.dbQuery {
                getAllGroupedInnboksEventsByIds(fodselsnummer1, grupperingsid, produsent)
            }.size `should be equal to` 2
        }
    }

    @Test
    fun `Returnerer en tom liste hvis produsent ikke matcher innboks-eventet`() {
        val noMatchProdusent = "dummyProdusent"
        runBlocking {
            database.dbQuery {
                getAllGroupedInnboksEventsByIds(fodselsnummer1, grupperingsid, noMatchProdusent)
            }.`should be empty`()
        }
    }

    @Test
    fun `Returnerer en tom liste hvis grupperingsid ikke matcher innboks-eventet`() {
        val noMatchGrupperingsid = "dummyGrupperingsid"
        runBlocking {
            database.dbQuery {
                getAllGroupedInnboksEventsByIds(fodselsnummer1, noMatchGrupperingsid, produsent)
            }.`should be empty`()
        }
    }

    @Test
    fun `Returnerer en liste av alle grupperte innboks-eventer basert paa systembruker`() {
        runBlocking {
            val groupedEventsBySystemuser = database.dbQuery { getAllGroupedInnboksEventsBySystemuser() }

            groupedEventsBySystemuser.size `should be equal to` 2
            groupedEventsBySystemuser.get(innboks1.systembruker) `should be equal to` 3
            groupedEventsBySystemuser.get(innboks3.systembruker) `should be equal to` 1
        }
    }


    @Test
    fun `Returnerer en liste av alle grupperte innboks-eventer basert paa produsent`() {
        runBlocking {
            val groupedEventsBySystemuser = database.dbQuery { getAllGroupedInnboksEventsByProducer() }

            groupedEventsBySystemuser.size `should be equal to` 2
            groupedEventsBySystemuser.findCountFor(innboks1.namespace, innboks1.appnavn) `should be equal to` 3
            groupedEventsBySystemuser.findCountFor(innboks3.namespace, innboks3.appnavn) `should be equal to` 1
        }
    }

    private fun createInnboks(innboks: List<Innboks>) {
        runBlocking {
            database.dbQuery { createInnboks(innboks) }
        }
    }

    private fun createSystembruker(systembruker: String, produsentnavn: String) {
        runBlocking {
            database.dbQuery { createProdusent(systembruker, produsentnavn) }
        }
    }

    private fun deleteInnboks(innboks: List<Innboks>) {
        runBlocking {
            database.dbQuery { deleteInnboks(innboks) }
        }
    }

    private fun deleteSystembruker(systembruker: String) {
        runBlocking {
            database.dbQuery { deleteProdusent(systembruker) }
        }
    }
}
