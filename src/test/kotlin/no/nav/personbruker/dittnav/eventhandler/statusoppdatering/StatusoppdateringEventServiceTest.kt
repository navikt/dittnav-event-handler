package no.nav.personbruker.dittnav.eventhandler.statusoppdatering

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

internal class StatusoppdateringEventServiceTest {

    private val database = mockk<Database>()
    private val statusoppdateringEventService = StatusoppdateringEventService(database)
    private val bruker = InnloggetBrukerObjectMother.createInnloggetBruker("123")
    private val appender: ListAppender<ILoggingEvent> = ListAppender()
    private val logger: Logger = LoggerFactory.getLogger(StatusoppdateringEventService::class.java) as Logger

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
    fun `Should return all Statusoppdaterings events for user`() {
        val statusoppdateringEvents = StatusoppdateringObjectMother.getStatusoppdateringEvents(bruker)

        runBlocking {
            coEvery {
                database.queryWithExceptionTranslation<List<Statusoppdatering>>(any())
            }.returns(statusoppdateringEvents)

            val actualStatusoppdaterings = statusoppdateringEventService.getAllEventsFromCacheForUser(bruker)
            actualStatusoppdaterings.size `should be equal to` 4
        }
    }

    @Test
    fun `Should log warning if producer is empty`() {
        val statusoppdateringListWithEmptyProducer = listOf(
                StatusoppdateringObjectMother.createStatusoppdateringWithSystembruker(1, "x-dittnav"))

        runBlocking {
            coEvery {
                database.queryWithExceptionTranslation<List<Statusoppdatering>>(any())
            }.returns(statusoppdateringListWithEmptyProducer)

            statusoppdateringEventService.getAllEventsFromCacheForUser(bruker)
        }

        val logevent = appender.list.first()
        logevent.level.levelStr `should be equal to` "WARN"
        logevent.formattedMessage `should contain` "produsent"
    }
}
