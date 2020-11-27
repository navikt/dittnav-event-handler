package no.nav.personbruker.dittnav.eventhandler.common.produsent

import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.*
import java.sql.SQLException

internal class ProducerNameAliasServiceTest {

    private val database = mockk<Database>()
    private val producerNameAliasService = ProducerNameAliasService(database)

    @BeforeEach
    fun `setup mocks`() {
        coEvery {
            database.queryWithExceptionTranslation<List<Produsent>>(any())
        }.returns(listOf(Produsent("x-dittnav", "dittnav")))
    }

    @AfterEach
    fun `reset mocks`() {
        clearMocks(database)
    }

    @Test
    fun `Skal hente produsentnavn alias`() {
        runBlocking {
            val producerNameAlias = producerNameAliasService.getProducerNameAlias("x-dittnav")
            producerNameAlias `should be equal to` "dittnav"
        }
    }

    @Test
    fun `skal returnere det som allerede finnes i producerNameAliases hvis henting av nye feiler`() {
        runBlocking {
            val originalProducerNameAlias = producerNameAliasService.getProducerNameAlias("x-dittnav")

            coEvery {
                database.queryWithExceptionTranslation<List<Produsent>>(any())
            }.throws(SQLException())

            val newProducerNameAlias = producerNameAliasService.getProducerNameAlias("x-dittnav")
            originalProducerNameAlias `should be equal to` newProducerNameAlias
        }
    }

    @Test
    fun `skal returnere tom String hvis produsentnavn ikke ble funnet i DB`() {
        runBlocking {
            val unmatchedProducerNameAlias = producerNameAliasService.getProducerNameAlias("x-ukjent")
            unmatchedProducerNameAlias `should be equal to` ""
        }
    }

    @Test
    fun `skal trigge oppdatering av producerNameAliases hver gang getProducerNameAlias blir kalt`() {
        runBlocking {
            producerNameAliasService.getProducerNameAlias("x-ukjent")
            producerNameAliasService.getProducerNameAlias("x-dittnav")
            coVerify(exactly = 2) { database.queryWithExceptionTranslation<List<Produsent>>(any()) }
        }
    }

}