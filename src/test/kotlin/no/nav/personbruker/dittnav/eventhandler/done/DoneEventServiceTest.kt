package no.nav.personbruker.dittnav.eventhandler.done

import Beskjed
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedObjectMother
import no.nav.personbruker.dittnav.eventhandler.beskjed.createBeskjed
import no.nav.personbruker.dittnav.eventhandler.beskjed.deleteBeskjed
import no.nav.personbruker.dittnav.eventhandler.common.database.H2Database
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.kafka.DuplicateEventException
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.kafka.NoEventsException
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should throw`
import org.amshove.kluent.invoking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.ZonedDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DoneEventServiceTest {

    private val database = H2Database()
    private val doneProducer = mockk<DoneProducer>()
    private val doneEventService = DoneEventService(database, doneProducer)
    private val fodselsnummer = "12345"
    private val uid = "11"
    private val eventId = "125"

    private val beskjed1 = BeskjedObjectMother.createBeskjed(id = 1, eventId = "125", fodselsnummer = "12345",
            synligFremTil = ZonedDateTime.now().plusHours(1), uid = "11", aktiv = true)

    @BeforeAll
    fun `populer tabellen med Beskjed-event`() {
        runBlocking {
            database.dbQuery { createBeskjed(listOf(beskjed1)) }
        }
    }

    @AfterAll
    fun `slett Beskjed-event fra tabellen`() {
        runBlocking {
            database.dbQuery { deleteBeskjed(listOf(beskjed1)) }
        }
    }


    @Test
    fun `Kaster exception hvis listen er tom`() {
        val emptyListOfBeskjed = emptyList<Beskjed>()
        invoking {
            runBlocking {
                doneEventService.isEventBeskjedListValid(emptyListOfBeskjed)
            }
        } `should throw` NoEventsException::class
    }

    @Test
    fun `Kaster exception hvis det er duplikat i listen`() {
        val beskjedListDuplicate = listOf<Beskjed>(BeskjedObjectMother.createBeskjed(1, "dummyEventId1", "dummmyFnr1", null, "dummyUid1", true),
                BeskjedObjectMother.createBeskjed(1, "dummyEventId1", "dummyFnr1", null, "dummyUid1", true))
        invoking {
            runBlocking {
                doneEventService.isEventBeskjedListValid(beskjedListDuplicate)
            }
        } `should throw` DuplicateEventException::class
    }

    @Test
    fun `should find event that matches input parameter`() {
        runBlocking {
            doneEventService.getBeskjedFromCacheForUser(fodselsnummer, uid, eventId).size `should be equal to` 1
        }
    }

    @Test
    fun `should return empty list if no events exists`() {
        runBlocking {
            doneEventService.getBeskjedFromCacheForUser(fodselsnummer, uid, "dummyEventId").size `should be equal to` 0
        }
    }

}
