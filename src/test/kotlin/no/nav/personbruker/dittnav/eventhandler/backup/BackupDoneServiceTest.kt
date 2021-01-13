package no.nav.personbruker.dittnav.eventhandler.backup

import io.mockk.Called
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kafka.common.KafkaException
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.common.test.`with message containing`
import no.nav.personbruker.dittnav.eventhandler.common.database.H2Database
import no.nav.personbruker.dittnav.eventhandler.common.database.createProdusent
import no.nav.personbruker.dittnav.eventhandler.common.database.deleteProdusent
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.BackupEventException
import no.nav.personbruker.dittnav.eventhandler.common.kafka.KafkaProducerWrapper
import no.nav.personbruker.dittnav.eventhandler.done.Done
import no.nav.personbruker.dittnav.eventhandler.done.DoneObjectMother
import no.nav.personbruker.dittnav.eventhandler.done.createDone
import no.nav.personbruker.dittnav.eventhandler.done.deleteAllDone
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should throw`
import org.amshove.kluent.invoking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class BackupDoneServiceTest {

    private val database = H2Database()
    private val doneProducer = mockk<KafkaProducerWrapper<no.nav.brukernotifikasjon.schemas.Done>>(relaxUnitFun = true)
    private val backupDoneService = BackupDoneService(database, doneProducer)

    val done1 = DoneObjectMother.createDone(eventId = "1", fodselsnummer = "12345678901")
    val done2 = DoneObjectMother.createDone(eventId = "2", fodselsnummer = "12345678901")
    private val allDone = listOf(done1, done2)

    @BeforeEach
    fun `Populer testdata`() {
        createDone(allDone)
        createSystembruker(systembruker = "x-dittnav", produsentnavn = "dittnav")
    }

    @AfterEach
    fun `Slett testdata`() {
        deleteDone()
        deleteSystembruker(systembruker = "x-dittnav")
    }

    @Test
    fun `Skal opprette backup-eventer for alle Done-eventer`() {
        runBlocking {
            val produceDoneEventsForAllDoneEventsInCache = backupDoneService.produceDoneEventsForAllDoneEventsInCache(false)
            produceDoneEventsForAllDoneEventsInCache `should be equal to` allDone.size
        }
    }

    @Test
    fun `Skal ikke opprette backup-eventer for alle Done-eventer hvis dryrun`() {
        runBlocking {
            val produceDoneEventsForAllDoneEventsInCache = backupDoneService.produceDoneEventsForAllDoneEventsInCache(true)
            produceDoneEventsForAllDoneEventsInCache `should be equal to` allDone.size
            verify { doneProducer wasNot Called }
        }
    }

    @Test
    fun `Skal kaste exception med informasjon om hvor prosessering stoppet ved valideringsfeil`() {
        val doneWithValidationError = DoneObjectMother.createDone(eventId = "3", fodselsnummer = "ugyldigFnr")
        createDone(listOf(doneWithValidationError))
        coEvery{ doneProducer.topicName } returns no.nav.personbruker.dittnav.eventhandler.config.Kafka.doneTopicNameBackup
        invoking {
            runBlocking {
                backupDoneService.produceDoneEventsForAllDoneEventsInCache(false)
            }
        } `should throw` BackupEventException::class `with message containing` "Vi stoppet på nr 3 (i batch 1) av totalt 3 eventer"
    }

    @Test
    fun `Kaster exception med informasjon om hvor prosessering stoppet hvis Kafka feiler`() {
        coEvery{ doneProducer.topicName } returns no.nav.personbruker.dittnav.eventhandler.config.Kafka.doneTopicNameBackup
        coEvery { doneProducer.sendEvent(any(), any()) } throws KafkaException("Simulert feil i en test")
        invoking {
            runBlocking {
                backupDoneService.produceDoneEventsForAllDoneEventsInCache(false)
            }
        } `should throw` BackupEventException::class `with message containing` "Vi stoppet på nr 0 (i batch nr. 1) av totalt 2 eventer"
    }

    private fun createDone(done: List<Done>) {
        runBlocking {
            database.dbQuery { createDone(done) }
        }
    }

    private fun createSystembruker(systembruker: String, produsentnavn: String) {
        runBlocking {
            database.dbQuery { createProdusent(systembruker, produsentnavn) }
        }
    }

    private fun deleteDone() {
        runBlocking {
            database.dbQuery { deleteAllDone() }
        }
    }

    private fun deleteSystembruker(systembruker: String) {
        runBlocking {
            database.dbQuery { deleteProdusent(systembruker) }
        }
    }
}
