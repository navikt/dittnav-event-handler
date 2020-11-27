package no.nav.personbruker.dittnav.eventhandler.common.produsent

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.database.H2Database
import no.nav.personbruker.dittnav.eventhandler.common.database.createProdusent
import no.nav.personbruker.dittnav.eventhandler.common.database.deleteProdusent
import org.amshove.kluent.`should be empty`
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ProducerNameAliasQueriesKtTest {

    private val database = H2Database()

    @BeforeEach
    fun `populer testdata`() {
        runBlocking {
            database.dbQuery { createProdusent(systembruker = "x-dittnav_1", produsentnavn = "dittnav_1") }
            database.dbQuery { createProdusent(systembruker = "x-dittnav_2", produsentnavn = "dittnav_2") }
        }
    }

    @AfterEach
    fun `slett testdata`() {
        runBlocking {
            database.dbQuery { deleteProdusent(systembruker = "x-dittnav_1") }
            database.dbQuery { deleteProdusent(systembruker = "x-dittnav_2") }

        }
    }

    @Test
    fun `Skal hente alle produsenter fra DB`() {
        runBlocking {
            database.dbQuery {
                getProdusent()
            }.size `should be equal to` 2
        }

    }

    @Test
    fun `Skal returnere tom liste hvis det ikke finnes produsenter i tabellen`() {
        `slett testdata`()
        runBlocking {
            database.dbQuery {
                getProdusent()
            }.`should be empty`()
        }
    }

}