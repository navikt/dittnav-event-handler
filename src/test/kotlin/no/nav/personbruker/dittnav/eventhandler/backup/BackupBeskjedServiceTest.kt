package no.nav.personbruker.dittnav.eventhandler.backup

import Beskjed
import io.mockk.Called
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kafka.common.KafkaException
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.common.test.`with message containing`
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedObjectMother
import no.nav.personbruker.dittnav.eventhandler.beskjed.createBeskjed
import no.nav.personbruker.dittnav.eventhandler.beskjed.deleteAllBeskjed
import no.nav.personbruker.dittnav.eventhandler.common.InnloggetBrukerObjectMother
import no.nav.personbruker.dittnav.eventhandler.common.database.H2Database
import no.nav.personbruker.dittnav.eventhandler.common.database.createProdusent
import no.nav.personbruker.dittnav.eventhandler.common.database.deleteProdusent
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.BackupEventException
import no.nav.personbruker.dittnav.eventhandler.common.kafka.KafkaProducerWrapper
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should throw`
import org.amshove.kluent.invoking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

internal class BackupBeskjedServiceTest {

    private val database = H2Database()
    private val beskjedProducer = mockk<KafkaProducerWrapper<no.nav.brukernotifikasjon.schemas.Beskjed>>(relaxUnitFun = true)
    private val doneProducer = mockk<KafkaProducerWrapper<no.nav.brukernotifikasjon.schemas.Done>>(relaxUnitFun = true)
    private val backupBeskjedService = BackupBeskjedService(database, beskjedProducer, doneProducer)

    private val bruker = InnloggetBrukerObjectMother.createInnloggetBruker("12345678901")
    private val beskjed1 = BeskjedObjectMother.createBeskjed(id = 1, eventId = "123", fodselsnummer = bruker.ident,
            synligFremTil = ZonedDateTime.now().plusHours(1), uid = "11", aktiv = true)
    private val beskjed2 = BeskjedObjectMother.createBeskjed(id = 2, eventId = "345", fodselsnummer = bruker.ident,
            synligFremTil = ZonedDateTime.now().plusHours(1), uid = "22", aktiv = true)
    private val beskjed3 = BeskjedObjectMother.createBeskjed(id = 3, eventId = "567", fodselsnummer = bruker.ident,
            synligFremTil = ZonedDateTime.now().plusHours(1), uid = "33", aktiv = false)
    private val allBeskjed = listOf(beskjed1, beskjed2, beskjed3)

    @BeforeEach
    fun `Populer testdata`() {
        createBeskjed(allBeskjed)
        createSystembruker(systembruker = "x-dittnav", produsentnavn = "dittnav")
    }

    @AfterEach
    fun `Slett testdata`() {
        deleteBeskjed()
        deleteSystembruker(systembruker = "x-dittnav")
    }

    @Test
    fun `Skal opprette backup-eventer for alle Beskjed-eventer`() {
        runBlocking {
            val produceBeskjedEventsForAllBeskjedEventsInCache = backupBeskjedService.produceBeskjedEventsForAllBeskjedEventsInCache(false)
            produceBeskjedEventsForAllBeskjedEventsInCache `should be equal to` allBeskjed.size
        }
    }

    @Test
    fun `Skal ikke opprette backup-eventer for alle Beskjed-eventer hvis dryrun`() {
        runBlocking {
            val produceBeskjedEventsForAllBeskjedEventsInCache = backupBeskjedService.produceBeskjedEventsForAllBeskjedEventsInCache(true)
            produceBeskjedEventsForAllBeskjedEventsInCache `should be equal to` allBeskjed.size
            verify { beskjedProducer wasNot Called }
        }
    }

    @Test
    fun `Skal kaste exception med informasjon om hvor prosessering stoppet ved valideringsfeil`() {
        val beskjedWithValidationError = BeskjedObjectMother.createBeskjed(id = 4, eventId = "789", fodselsnummer = "ugyldigfnr",
                synligFremTil = ZonedDateTime.now().plusHours(1), uid = "11", aktiv = true)
        createBeskjed(listOf(beskjedWithValidationError))
        coEvery{ beskjedProducer.topicName } returns no.nav.personbruker.dittnav.eventhandler.config.Kafka.beskjedTopicNameBackup
        invoking {
            runBlocking {
                backupBeskjedService.produceBeskjedEventsForAllBeskjedEventsInCache(false)
            }
        } `should throw` BackupEventException::class `with message containing` "Vi stoppet på nr 4 (i batch 1) av totalt 4 eventer"
    }

    @Test
    fun `Kaster exception med informasjon om hvor prosessering stoppet hvis Kafka feiler`() {
        coEvery{ beskjedProducer.topicName } returns no.nav.personbruker.dittnav.eventhandler.config.Kafka.beskjedTopicNameBackup
        coEvery { beskjedProducer.sendEvent(any(), any()) } throws KafkaException("Simulert feil i en test")
        invoking {
            runBlocking {
                backupBeskjedService.produceBeskjedEventsForAllBeskjedEventsInCache(false)
            }
        } `should throw` BackupEventException::class `with message containing` "Vi stoppet på nr 0 (i batch nr. 1) av totalt 3 eventer"
    }

    @Test
    fun `Skal opprette Done-backup-eventer for alle inaktive Beskjed-eventer`() {
        runBlocking {
            val produceDoneEventsForAllInactiveBeskjedEventsInCache = backupBeskjedService.produceDoneEventsFromAllInactiveBeskjedEvents(false)
            produceDoneEventsForAllInactiveBeskjedEventsInCache `should be equal to` 1
        }
    }

    private fun createBeskjed(beskjeder: List<Beskjed>) {
        runBlocking {
            database.dbQuery { createBeskjed(beskjeder) }
        }
    }

    private fun createSystembruker(systembruker: String, produsentnavn: String) {
        runBlocking {
            database.dbQuery { createProdusent(systembruker, produsentnavn) }
        }
    }

    private fun deleteBeskjed() {
        runBlocking {
            database.dbQuery { deleteAllBeskjed() }
        }
    }

    private fun deleteSystembruker(systembruker: String) {
        runBlocking {
            database.dbQuery { deleteProdusent(systembruker) }
        }
    }
}
