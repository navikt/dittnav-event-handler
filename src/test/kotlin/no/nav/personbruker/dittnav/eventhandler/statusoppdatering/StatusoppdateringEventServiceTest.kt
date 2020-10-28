package no.nav.personbruker.dittnav.eventhandler.statusoppdatering

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.InnloggetBrukerObjectMother
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.FieldValidationException
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should contain`
import org.amshove.kluent.`should throw`
import org.amshove.kluent.invoking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

internal class StatusoppdateringEventServiceTest {

    private val database = mockk<Database>()
    private val statusoppdateringEventService = StatusoppdateringEventService(database)
    private val bruker = InnloggetBrukerObjectMother.createInnloggetBruker("123")
    private val produsent = "dittnav"
    private val grupperingsid = "100${bruker.ident}"
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
    fun `Should return all events that are grouped together by ids`() {
        val statusoppdateringEvents = StatusoppdateringObjectMother.getStatusoppdateringEvents(bruker)

        runBlocking {
            coEvery {
                database.queryWithExceptionTranslation<List<Statusoppdatering>>(any())
            }.returns(statusoppdateringEvents)

            val actualStatusoppdateringEvents = statusoppdateringEventService.getAllGroupedEventsFromCacheForUser(bruker, grupperingsid, produsent)
            actualStatusoppdateringEvents.size `should be equal to` 4
        }
    }

    @Test
    fun `Should log warning if producer is empty`() {
        val statusoppdateringEventsWithEmptyProducer = listOf(
                StatusoppdateringObjectMother.createStatusoppdateringWithSystembruker(1, "x-dittnav"))

        runBlocking {
            coEvery {
                database.queryWithExceptionTranslation<List<Statusoppdatering>>(any())
            }.returns(statusoppdateringEventsWithEmptyProducer)

            statusoppdateringEventService.getAllGroupedEventsFromCacheForUser(bruker, grupperingsid, produsent)
        }

        val logevent = appender.list.first()
        logevent.level.levelStr `should be equal to` "WARN"
        logevent.formattedMessage `should contain` "produsent"
        logevent.formattedMessage `should contain` "fodselsnummer=***"
    }

    @Test
    fun `Kaster FieldValidationException hvis grupperingsid er null`() {
        val grupperingsidSomErNull = null
        invoking {
            runBlocking {
                statusoppdateringEventService.getAllGroupedEventsFromCacheForUser(bruker, grupperingsidSomErNull, produsent)
            }
        } `should throw` FieldValidationException::class
    }

    @Test
    fun `Kaster FieldValidationException hvis grupperingsid er tomt`() {
        val grupperingsidSomErTom = ""
        invoking {
            runBlocking {
                statusoppdateringEventService.getAllGroupedEventsFromCacheForUser(bruker, grupperingsidSomErTom, produsent)
            }
        } `should throw` FieldValidationException::class
    }

    @Test
    fun `Kaster FieldValidationException hvis grupperingsid er for lang`() {
        val grupperingsidForLang = "g".repeat(101)
        invoking {
            runBlocking {
                statusoppdateringEventService.getAllGroupedEventsFromCacheForUser(bruker, grupperingsidForLang, produsent)
            }
        } `should throw` FieldValidationException::class
    }

    @Test
    fun `Kaster FieldValidationException hvis produsent er null`() {
        val produsentSomErNull = null
        invoking {
            runBlocking {
                statusoppdateringEventService.getAllGroupedEventsFromCacheForUser(bruker, grupperingsid, produsentSomErNull)
            }
        } `should throw` FieldValidationException::class
    }

    @Test
    fun `Kaster FieldValidationException hvis produsent er tomt`() {
        val produsentSomErTom = ""
        invoking {
            runBlocking {
                statusoppdateringEventService.getAllGroupedEventsFromCacheForUser(bruker, grupperingsid, produsentSomErTom)
            }
        } `should throw` FieldValidationException::class
    }

    @Test
    fun `Kaster FieldValidationException hvis produsent er for lang`() {
        val produsentForLang = "p".repeat(101)
        invoking {
            runBlocking {
                statusoppdateringEventService.getAllGroupedEventsFromCacheForUser(bruker, grupperingsid, produsentForLang)
            }
        } `should throw` FieldValidationException::class
    }
}
