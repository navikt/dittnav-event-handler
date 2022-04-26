package no.nav.personbruker.dittnav.eventhandler.innboks

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.database.LocalPostgresDatabase
import no.nav.personbruker.dittnav.eventhandler.common.findCountFor
import org.amshove.kluent.`should be empty`
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InnboksQueriesTest {

    private val database = LocalPostgresDatabase.cleanDb()

    private val fodselsnummer1 = "12345"
    private val fodselsnummer2 = "67890"
    private val systembruker = "x-dittnav"
    private val namespace = "localhost"
    private val appnavn = "dittnav"
    private val grupperingsid = "100${fodselsnummer1}"

    private val innboks1 = InnboksObjectMother.createInnboks(
        id = 1,
        eventId = "123",
        fodselsnummer = fodselsnummer1,
        grupperingsId = grupperingsid,
        aktiv = true,
        systembruker = systembruker,
        namespace = namespace,
        appnavn = appnavn
    )
    private val innboks2 = InnboksObjectMother.createInnboks(
        id = 2,
        eventId = "345",
        fodselsnummer = fodselsnummer1,
        grupperingsId = grupperingsid,
        aktiv = true,
        systembruker = systembruker,
        namespace = namespace,
        appnavn = appnavn
    )
    private val innboks3 = InnboksObjectMother.createInnboks(
        id = 3,
        eventId = "567",
        fodselsnummer = fodselsnummer2,
        aktiv = true,
        systembruker = "x-dittnav-2",
        namespace = namespace,
        appnavn = "dittnav-2"
    )
    private val innboks4 = InnboksObjectMother.createInnboks(
        id = 4,
        eventId = "789",
        fodselsnummer = fodselsnummer2,
        aktiv = false,
        systembruker = systembruker,
        namespace = namespace,
        appnavn = appnavn
    )

    @BeforeAll
    fun `populer test-data`() {
        createInnboks(listOf(innboks1, innboks2, innboks3, innboks4))
    }

    @AfterAll
    fun `slett Innboks-eventer fra tabellen`() {
        deleteInnboks(listOf(innboks1, innboks2, innboks3, innboks4))
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
            innboks.produsent `should be equal to` appnavn
        }
    }

    @Test
    fun `Returnerer lesbart navn for produsent som kan eksponeres for inaktive eventer`() {
        runBlocking {
            val innboks = database.dbQuery { getInaktivInnboksForInnloggetBruker(fodselsnummer2) }.first()
            innboks.produsent `should be equal to` appnavn
        }
    }

    @Test
    fun `Returnerer lesbart navn for produsent som kan eksponeres for alle eventer`() {
        runBlocking {
            val innboks = database.dbQuery { getAllInnboksForInnloggetBruker(fodselsnummer1) }.first()
            innboks.produsent `should be equal to` appnavn
        }
    }

    @Test
    fun `Returnerer en liste av alle grupperte Innboks-eventer`() {
        runBlocking {
            database.dbQuery {
                getAllGroupedInnboksEventsByIds(fodselsnummer1, grupperingsid, appnavn)
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
                getAllGroupedInnboksEventsByIds(fodselsnummer1, noMatchGrupperingsid, appnavn)
            }.`should be empty`()
        }
    }

    @Test
    fun `Returnerer en liste av alle grupperte innboks-eventer basert paa systembruker`() {
        runBlocking {
            val groupedEventsBySystemuser = database.dbQuery { getAllGroupedInnboksEventsBySystemuser() }

            groupedEventsBySystemuser.size `should be equal to` 2
            groupedEventsBySystemuser[innboks1.systembruker] `should be equal to` 3
            groupedEventsBySystemuser[innboks3.systembruker] `should be equal to` 1
        }
    }

    @Test
    fun `Returnerer en liste av alle grupperte innboks-eventer basert paa produsent`() {
        runBlocking {
            val groupedEventsByProducer = database.dbQuery { getAllGroupedInnboksEventsByProducer() }

            groupedEventsByProducer.size `should be equal to` 2
            groupedEventsByProducer.findCountFor(innboks1.namespace, innboks1.appnavn) `should be equal to` 3
            groupedEventsByProducer.findCountFor(innboks3.namespace, innboks3.appnavn) `should be equal to` 1
        }
    }

    private fun createInnboks(innboks: List<Innboks>) {
        runBlocking {
            database.dbQuery { createInnboks(innboks) }
        }
    }

    private fun deleteInnboks(innboks: List<Innboks>) {
        runBlocking {
            database.dbQuery { deleteInnboks(innboks) }
        }
    }
}
