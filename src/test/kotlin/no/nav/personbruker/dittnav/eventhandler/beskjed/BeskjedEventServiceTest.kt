package no.nav.personbruker.dittnav.eventhandler.beskjed

import Beskjed
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

class BeskjedEventServiceTest {

    private val database = mockk<Database>()
    private val beskjedEventService = BeskjedEventService(database)


    @Test
    fun `Should not filter on expiry date when requesting all Beskjeds`() {
        runBlocking {
            coEvery {
                database.dbQuery<List<Beskjed>>(any())
            }.returns(beskjedList)

            val actualBeskjeds = beskjedEventService.getAllEventsFromCacheForUser("123")
            actualBeskjeds.size `should be equal to` beskjedList.size
        }
    }
    @Test
    fun `Should filter on expiry date when requesting active Beskjeds`() {
        runBlocking {
            coEvery {
                database.dbQuery<List<Beskjed>>(any())
            }.returns(beskjedList)

            val actualBeskjeds = beskjedEventService.getEventsFromCacheForUser("123")
            actualBeskjeds.size `should be equal to` 1
        }
    }

    val beskjedList get() = listOf(
            createBeskjed(1, "1", "123", null),
            createBeskjed(2, "2", "123", ZonedDateTime.now().minusDays(2))
    )
}