package no.nav.personbruker.dittnav.eventhandler.beskjed

import Beskjed
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.InnloggetBrukerObjectMother
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

class BeskjedEventServiceTest {

    private val database = mockk<Database>()
    private val beskjedEventService = BeskjedEventService(database)
    private val bruker = InnloggetBrukerObjectMother.createInnloggetBrukerWithSubject("123")

    @Test
    fun `Should not filter on expiry date when requesting all Beskjeds`() {

        runBlocking {
            coEvery {
                database.dbQuery<List<Beskjed>>(any())
            }.returns(beskjedList)

            val actualBeskjeds = beskjedEventService.getAllEventsFromCacheForUser(bruker)
            actualBeskjeds.size `should be equal to` beskjedList.size
        }
    }

    @Test
    fun `Should filter on expiry date when requesting active Beskjeds`() {
        runBlocking {
            coEvery {
                database.dbQuery<List<Beskjed>>(any())
            }.returns(beskjedList)

            val actualBeskjeds = beskjedEventService.getEventsFromCacheForUser(bruker)
            actualBeskjeds.size `should be equal to` 1
        }
    }

    val beskjedList
        get() = listOf(
                createBeskjed(1, "1", bruker.getIdent(), null, "1"),
                createBeskjed(2, "2", bruker.getIdent(), ZonedDateTime.now().minusDays(2), "2")
        )
}