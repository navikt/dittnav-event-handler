package no.nav.personbruker.dittnav.eventhandler.beskjed

import Beskjed
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.brukernotifikasjon.schemas.builders.exception.FieldValidationException
import no.nav.personbruker.dittnav.eventhandler.common.TokenXUserObjectMother
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.slf4j.LoggerFactory
import java.time.ZonedDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BeskjedEventServiceTest {

    private val database = mockk<Database>()
    private val beskjedEventService = BeskjedEventService(database)
    private val bruker = TokenXUserObjectMother.createInnloggetBruker("123")
    private val produsent = "dittnav"
    private val grupperingsid = "100${bruker.ident}"

    private val appender: ListAppender<ILoggingEvent> = ListAppender()
    private val logger: Logger = LoggerFactory.getLogger(BeskjedEventService::class.java) as Logger

    @BeforeAll
    fun setup() {
        appender.start()
        logger.addAppender(appender)
    }

    @AfterAll
    fun teardown() {
        logger.detachAppender(appender)
    }

    @Test
    fun `Skal kunne hente alle beskjeder`() {
        val beskjedList = getBeskjedList()
        runBlocking {
            coEvery {
                database.queryWithExceptionTranslation<List<Beskjed>>(any())
            }.returns(beskjedList)

            val actualBeskjeds = beskjedEventService.getActiveEventsForFodselsnummer(bruker.ident)
            actualBeskjeds.size shouldBe beskjedList.size
        }
    }

    @Test
    fun `Should return all events that are grouped together by ids`() {
        val innloggetbruker = TokenXUserObjectMother.createInnloggetBruker("100")
        val grupperingsid = "100${innloggetbruker.ident}"
        val beskjedEvents = listOf(
            BeskjedObjectMother.createBeskjed(fodselsnummer = innloggetbruker.ident, grupperingsId = grupperingsid),
            BeskjedObjectMother.createBeskjed(fodselsnummer = innloggetbruker.ident, grupperingsId = grupperingsid)
        )
        runBlocking {
            coEvery {
                database.queryWithExceptionTranslation<List<Beskjed>>(any())
            }.returns(beskjedEvents)

            val actualBeskjedEvents = beskjedEventService.getAllGroupedEventsFromCacheForUser(innloggetbruker, grupperingsid, produsent)
            actualBeskjedEvents.size shouldBe 2
        }
    }

    @Test
    fun `Kaster FieldValidationException hvis grupperingsid er null`() {
        val grupperingsidSomErNull = null
        shouldThrow<FieldValidationException> {
            runBlocking {
                beskjedEventService.getAllGroupedEventsFromCacheForUser(bruker, grupperingsidSomErNull, produsent)
            }
        }
    }

    @Test
    fun `Kaster FieldValidationException hvis grupperingsid er tomt`() {
        val grupperingsidSomErTom = ""
        shouldThrow<FieldValidationException> {
            runBlocking {
                beskjedEventService.getAllGroupedEventsFromCacheForUser(bruker, grupperingsidSomErTom, produsent)
            }
        }
    }

    @Test
    fun `Kaster FieldValidationException hvis grupperingsid er for lang`() {
        val grupperingsidForLang = "g".repeat(101)
        shouldThrow<FieldValidationException> {
            runBlocking {
                beskjedEventService.getAllGroupedEventsFromCacheForUser(bruker, grupperingsidForLang, produsent)
            }
        }
    }

    @Test
    fun `Kaster FieldValidationException hvis produsent er null`() {
        val produsentSomErNull = null
        shouldThrow<FieldValidationException> {
            runBlocking {
                beskjedEventService.getAllGroupedEventsFromCacheForUser(bruker, grupperingsid, produsentSomErNull)
            }
        }
    }

    @Test
    fun `Kaster FieldValidationException hvis produsent er tomt`() {
        val produsentSomErTom = ""
        shouldThrow<FieldValidationException> {
            runBlocking {
                beskjedEventService.getAllGroupedEventsFromCacheForUser(bruker, grupperingsid, produsentSomErTom)
            }
        }
    }

    @Test
    fun `Kaster FieldValidationException hvis produsent er for lang`() {
        val produsentForLang = "p".repeat(101)
        shouldThrow<FieldValidationException> {
            runBlocking {
                beskjedEventService.getAllGroupedEventsFromCacheForUser(bruker, grupperingsid, produsentForLang)
            }
        }
    }

    private fun getBeskjedList(): MutableList<Beskjed> {
        return mutableListOf(
            BeskjedObjectMother.createBeskjed(fodselsnummer = bruker.ident, synligFremTil = null),
            BeskjedObjectMother.createBeskjed(fodselsnummer = bruker.ident, synligFremTil = ZonedDateTime.now().minusDays(2))
        )
    }
}
