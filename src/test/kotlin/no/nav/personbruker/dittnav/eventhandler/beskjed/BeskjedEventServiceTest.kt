package no.nav.personbruker.dittnav.eventhandler.beskjed

import Beskjed
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.api.common.InnloggetBrukerObjectMother
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
        val beskjedList = getBeskjedList()
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
        val beskjedList = getBeskjedList()
        runBlocking {
            coEvery {
                database.dbQuery<List<Beskjed>>(any())
            }.returns(beskjedList)

            val actualBeskjeds = beskjedEventService.getActiveCachedEventsForUser(bruker)
            actualBeskjeds.size `should be equal to` 1
        }
    }

    @Test
    fun `Should return expired as inactive`() {
        val beskjedList = getBeskjedList()
        beskjedList.add(createBeskjed(3, "3", bruker.getIdent(), null, false))
        beskjedList.add(createBeskjed(4, "4", bruker.getIdent(), ZonedDateTime.now().minusDays(1), true))
        runBlocking {
            coEvery {
                database.dbQuery<List<Beskjed>>(any())
            }.returns(beskjedList)

            val actualBeskjeds = beskjedEventService.getInactiveCachedEventsForUser(bruker)
            actualBeskjeds.size `should be equal to` 3
        }
    }

    fun getBeskjedList(): MutableList<Beskjed> {
        return mutableListOf(
                createBeskjed(1, "1", bruker.getIdent(), null, true),
                createBeskjed(2, "2", bruker.getIdent(), ZonedDateTime.now().minusDays(2), true))
    }
}
