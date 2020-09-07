package no.nav.personbruker.dittnav.eventhandler.statusOppdatering

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.InnloggetBrukerObjectMother
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should contain`
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

internal class StatusOppdateringEventServiceTest {

    private val database = mockk<Database>()
    private val statusOppdateringEventService = StatusOppdateringEventService(database)
    private val bruker = InnloggetBrukerObjectMother.createInnloggetBruker("123")
    private val dummyEventId = "1"
    private val dummyStatusGlobal = "dummyStatusGlobal"
    private val dummyStatusIntern = "dummyStatusIntern"
    private val dummySakstema = "dummySakstema"


    private val appender: ListAppender<ILoggingEvent> = ListAppender()
    private val logger: Logger = LoggerFactory.getLogger(StatusOppdateringEventService::class.java) as Logger

    @BeforeEach
    fun `setup`() {
        appender.start()
        logger.addAppender(appender)
    }

    @AfterEach
    fun `teardown`() {
        logger.detachAppender(appender)
    }

    @Test
    fun `Should return all StatusOppdaterings events for user`() {
        val statusOppdateringList = getStatusOppdateringList()

        runBlocking {
            coEvery {
                database.queryWithExceptionTranslation<List<StatusOppdatering>>(any())
            }.returns(statusOppdateringList)

            val actualStatusOppdaterings = statusOppdateringEventService.getAllEventsFromCacheForUser(bruker)
            actualStatusOppdaterings.size `should be equal to` 2
        }
    }

    @Test
    fun `Should log warning if producer is empty`() {
        val statusOppdateringListWithEmptyProducer = listOf(
                StatusOppdateringObjectMother.createStatusOppdateringWithSystembruker(1, "x-dittnav"))

        runBlocking {
            coEvery {
                database.queryWithExceptionTranslation<List<StatusOppdatering>>(any())
            }.returns(statusOppdateringListWithEmptyProducer)

            statusOppdateringEventService.getAllEventsFromCacheForUser(bruker)
        }

        val logevent = appender.list.first()
        logevent.level.levelStr `should be equal to` "WARN"
        logevent.formattedMessage `should contain` "produsent"
    }

    fun getStatusOppdateringList(): MutableList<StatusOppdatering> {
        return mutableListOf(
                StatusOppdateringObjectMother.createStatusOppdatering(1, "$dummyEventId+2", bruker.ident, "$dummyStatusGlobal+1", "$dummyStatusIntern+1", "$dummySakstema+1"),
                StatusOppdateringObjectMother.createStatusOppdatering(2, "$dummyEventId+3", bruker.ident, "$dummyStatusGlobal+2", "$dummyStatusIntern+2", "$dummySakstema+2"))
    }
}
