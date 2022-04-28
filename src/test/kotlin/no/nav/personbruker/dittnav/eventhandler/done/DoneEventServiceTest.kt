package no.nav.personbruker.dittnav.eventhandler.done

import Beskjed
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedObjectMother
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.kafka.DuplicateEventException
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.kafka.EventMarkedInactiveException
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.kafka.NoEventsException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.ZonedDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DoneEventServiceTest {

    private val database = mockk<Database>()
    private val doneProducer = mockk<DoneProducer>()
    private val doneEventService = DoneEventService(database, doneProducer)
    private val fodselsnummer = "12345"
    private val eventId = "125"

    private val beskjed1 = BeskjedObjectMother.createBeskjed(
        id = 1, eventId = "125", fodselsnummer = "12345",
        synligFremTil = ZonedDateTime.now().plusHours(1)
    )

    @Test
    fun `Kaster exception hvis listen er tom`() {
        val emptyListOfBeskjed = emptyList<Beskjed>()
        shouldThrow<NoEventsException> {
            runBlocking {
                doneEventService.validBeskjed(emptyListOfBeskjed)
            }
        }
    }

    @Test
    fun `Kaster exception hvis det er duplikat i listen`() {
        val beskjedListDuplicate = listOf(
            BeskjedObjectMother.createBeskjed(id = 1, eventId = "dummyEventId1", fodselsnummer = "dummmyFnr1"),
            BeskjedObjectMother.createBeskjed(id = 1, eventId = "dummyEventId1", fodselsnummer = "dummyFnr1")
        )
        shouldThrow<DuplicateEventException> {
            runBlocking {
                doneEventService.validBeskjed(beskjedListDuplicate)
            }
        }
    }

    @Test
    fun `Kaster exception hvis gjeldende event allerede er markert done`() {
        val beskjedListDuplicate =
            listOf(BeskjedObjectMother.createBeskjed(id = 1, eventId = "dummyEventId1", fodselsnummer = "dummmyFnr1", aktiv = false))
        shouldThrow<EventMarkedInactiveException> {
            runBlocking {
                doneEventService.validBeskjed(beskjedListDuplicate)
            }
        }
    }

    @Test
    fun `should find event that matches input parameter`() {
        runBlocking {
            coEvery {
                database.queryWithExceptionTranslation<List<Beskjed>>(any())
            }.returns(listOf(beskjed1))

            doneEventService.getBeskjedFromCacheForUser(
                fodselsnummer,
                eventId
            ).eventId shouldBe eventId
        }
    }
}
