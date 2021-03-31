package no.nav.personbruker.dittnav.eventhandler.innboks

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.InnloggetBrukerObjectMother
import no.nav.personbruker.dittnav.eventhandler.common.database.H2Database
import no.nav.personbruker.dittnav.eventhandler.common.database.createProdusent
import no.nav.personbruker.dittnav.eventhandler.common.database.deleteProdusent
import org.amshove.kluent.`should be empty`
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InnboksQueriesTest {

    private val database = H2Database()

    private val bruker1 = InnloggetBrukerObjectMother.createInnloggetBruker("12345")
    private val bruker2 = InnloggetBrukerObjectMother.createInnloggetBruker("67890")
    private val systembruker = "x-dittnav"
    private val grupperingsid = "100${bruker1.ident}"

    private val innboks1 = InnboksObjectMother.createInnboks(id = 1, eventId = "123", fodselsnummer = bruker1.ident, aktiv = true)
    private val innboks2 = InnboksObjectMother.createInnboks(id = 2, eventId = "345", fodselsnummer = bruker1.ident, aktiv = true)
    private val innboks3 = InnboksObjectMother.createInnboks(id = 3, eventId = "567", fodselsnummer = bruker2.ident, aktiv = true)
    private val innboks4 = InnboksObjectMother.createInnboks(id = 4, eventId = "789", fodselsnummer = bruker2.ident, aktiv = false)

    @BeforeAll
    fun `populer test-data`() {
        createInnboks(listOf(innboks1, innboks2, innboks3, innboks4))
        createSystembruker(systembruker = "x-dittnav", produsentnavn = "dittnav")
    }

    @AfterAll
    fun `slett Innboks-eventer fra tabellen`() {
        deleteInnboks(listOf(innboks1, innboks2, innboks3, innboks4))
        deleteSystembruker(systembruker = "x-dittnav")
    }

    @Test
    fun `Finn alle cachede Innboks-eventer for fodselsnummer`() {
        runBlocking {
            database.dbQuery { getAllInnboksForInnloggetBruker(bruker1) }.size `should be equal to` 2
            database.dbQuery { getAllInnboksForInnloggetBruker(bruker2) }.size `should be equal to` 2
        }
    }

    @Test
    fun `Finn kun aktive cachede Innboks-eventer for fodselsnummer`() {
        runBlocking {
            database.dbQuery { getAktivInnboksForInnloggetBruker(bruker1) }.size `should be equal to` 2
            database.dbQuery { getAktivInnboksForInnloggetBruker(bruker2) }.size `should be equal to` 1
        }
    }

    @Test
    fun `Finn kun inaktive cachede Innboks-eventer for fodselsnummer`() {
        runBlocking {
            database.dbQuery { getInaktivInnboksForInnloggetBruker(bruker1) }.`should be empty`()
            database.dbQuery { getInaktivInnboksForInnloggetBruker(bruker2) }.size `should be equal to` 1
        }
    }

    @Test
    fun `Returnerer tom liste hvis Innboks-eventer for fodselsnummer ikke finnes`() {
        val brukerUtenEventer = InnloggetBrukerObjectMother.createInnloggetBruker("0")
        runBlocking {
            database.dbQuery { getAllInnboksForInnloggetBruker(brukerUtenEventer) }.size `should be equal to` 0
        }
    }

    @Test
    fun `Returnerer tom liste hvis fodselsnummer er tomt`() {
        val brukerUtenEventer = InnloggetBrukerObjectMother.createInnloggetBruker("")
        runBlocking {
            database.dbQuery { getAllInnboksForInnloggetBruker(brukerUtenEventer) }.size `should be equal to` 0
        }
    }

    @Test
    fun `Returnerer lesbart navn for produsent som kan eksponeres for aktive eventer`() {
        runBlocking {
            val innboks = database.dbQuery { getAktivInnboksForInnloggetBruker(bruker1) }.first()
            innboks.produsent `should be equal to` "dittnav"
        }
    }

    @Test
    fun `Returnerer lesbart navn for produsent som kan eksponeres for inaktive eventer`() {
        runBlocking {
            val innboks = database.dbQuery { getInaktivInnboksForInnloggetBruker(bruker2) }.first()
            innboks.produsent `should be equal to` "dittnav"
        }
    }

    @Test
    fun `Returnerer lesbart navn for produsent som kan eksponeres for alle eventer`() {
        runBlocking {
            val innboks = database.dbQuery { getAllInnboksForInnloggetBruker(bruker1) }.first()
            innboks.produsent `should be equal to` "dittnav"
        }
    }

    @Test
    fun `Returnerer tom streng for produsent hvis eventet er produsert av systembruker vi ikke har i systembruker-tabellen`() {
        var innboksMedAnnenProdusent = InnboksObjectMother.createInnboks(id = 5, eventId = "111", fodselsnummer = "112233", aktiv = true)
                .copy(systembruker = "ukjent-systembruker")
        createInnboks(listOf(innboksMedAnnenProdusent))
        val innboks = runBlocking {
            database.dbQuery {
                getAllInnboksForInnloggetBruker(InnloggetBrukerObjectMother.createInnloggetBruker("112233"))
            }.first()
        }
        innboks.produsent `should be equal to` ""
        deleteInnboks(listOf(innboksMedAnnenProdusent))
    }

    @Test
    fun `Returnerer en liste av alle grupperte Innboks-eventer`() {
        runBlocking {
            database.dbQuery {
                getAllGroupedInnboksEventsByIds(bruker1, grupperingsid, systembruker)
            }.size `should be equal to` 2
        }
    }

    @Test
    fun `Returnerer en tom liste hvis produsent ikke matcher innboks-eventet`() {
        val noMatchProdusent = "dummyProdusent"
        runBlocking {
            database.dbQuery {
                getAllGroupedInnboksEventsByIds(bruker1, grupperingsid, noMatchProdusent)
            }.`should be empty`()
        }
    }

    @Test
    fun `Returnerer en tom liste hvis grupperingsid ikke matcher innboks-eventet`() {
        val noMatchGrupperingsid = "dummyGrupperingsid"
        runBlocking {
            database.dbQuery {
                getAllGroupedInnboksEventsByIds(bruker1, noMatchGrupperingsid, systembruker)
            }.`should be empty`()
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
