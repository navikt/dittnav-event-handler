package no.nav.personbruker.dittnav.eventhandler.backup

import io.mockk.Called
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kafka.common.KafkaException
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.`with message containing`
import no.nav.personbruker.dittnav.eventhandler.common.InnloggetBrukerObjectMother
import no.nav.personbruker.dittnav.eventhandler.common.database.H2Database
import no.nav.personbruker.dittnav.eventhandler.common.database.createProdusent
import no.nav.personbruker.dittnav.eventhandler.common.database.deleteProdusent
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.BackupEventException
import no.nav.personbruker.dittnav.eventhandler.common.kafka.KafkaProducerWrapper
import no.nav.personbruker.dittnav.eventhandler.oppgave.Oppgave
import no.nav.personbruker.dittnav.eventhandler.oppgave.OppgaveObjectMother
import no.nav.personbruker.dittnav.eventhandler.oppgave.createOppgave
import no.nav.personbruker.dittnav.eventhandler.oppgave.deleteAllOppgave
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should throw`
import org.amshove.kluent.invoking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class BackupOppgaveServiceTest {

    private val database = H2Database()
    private val oppgaveProducer = mockk<KafkaProducerWrapper<no.nav.brukernotifikasjon.schemas.Oppgave>>(relaxUnitFun = true)
    private val doneProducer = mockk<KafkaProducerWrapper<no.nav.brukernotifikasjon.schemas.Done>>(relaxUnitFun = true)
    private val backupOppgaveService = BackupOppgaveService(database, oppgaveProducer, doneProducer)

    private val bruker = InnloggetBrukerObjectMother.createInnloggetBruker("12345678901")
    private val oppgave1 = OppgaveObjectMother.createOppgave(id = 1, eventId = "123", fodselsnummer = bruker.ident, aktiv = true)
    private val oppgave2 = OppgaveObjectMother.createOppgave(id = 2, eventId = "345", fodselsnummer = bruker.ident, aktiv = true)
    private val oppgave3 = OppgaveObjectMother.createOppgave(id = 3, eventId = "567", fodselsnummer = bruker.ident, aktiv = false)
    private val allOppgave = listOf(oppgave1, oppgave2, oppgave3)

    @BeforeEach
    fun `Populer testdata`() {
        createOppgave(allOppgave)
        createSystembruker(systembruker = "x-dittnav", produsentnavn = "dittnav")
    }

    @AfterEach
    fun `Slett testdata`() {
        deleteOppgave()
        deleteSystembruker(systembruker = "x-dittnav")
    }

    @Test
    fun `Skal opprette backup-eventer for alle Oppgave-eventer`() {
        runBlocking {
            val produceOppgaveEventsForAllOppgaveEventsInCache = backupOppgaveService.produceOppgaveEventsForAllOppgaveEventsInCache(false)
            produceOppgaveEventsForAllOppgaveEventsInCache `should be equal to` allOppgave.size
        }
    }

    @Test
    fun `Skal ikke opprette backup-eventer for alle Oppgave-eventer hvis dryrun`() {
        runBlocking {
            val produceOppgaveEventsForAllOppgaveEventsInCache = backupOppgaveService.produceOppgaveEventsForAllOppgaveEventsInCache(true)
            produceOppgaveEventsForAllOppgaveEventsInCache `should be equal to` allOppgave.size
            verify { oppgaveProducer wasNot Called }
        }
    }

    @Test
    fun `Skal kaste exception med informasjon om hvor prosessering stoppet ved valideringsfeil`() {
        val oppgaveWithValidationError = OppgaveObjectMother.createOppgave(id = 4, eventId = "789", fodselsnummer = "ugyldigfnr", aktiv = true)
        createOppgave(listOf(oppgaveWithValidationError))
        coEvery{ oppgaveProducer.topicName } returns no.nav.personbruker.dittnav.eventhandler.config.Kafka.oppgaveTopicNameBackup
        invoking {
            runBlocking {
                backupOppgaveService.produceOppgaveEventsForAllOppgaveEventsInCache(false)
            }
        } `should throw` BackupEventException::class `with message containing` "Vi stoppet på nr 4 (i batch nr. 1) av totalt 4 eventer"
    }

    @Test
    fun `Kaster exception med informasjon om hvor prosessering stoppet hvis Kafka feiler`() {
        coEvery{ oppgaveProducer.topicName } returns no.nav.personbruker.dittnav.eventhandler.config.Kafka.oppgaveTopicNameBackup
        coEvery { oppgaveProducer.sendEvent(any(), any()) } throws KafkaException("Simulert feil i en test")
        invoking {
            runBlocking {
                backupOppgaveService.produceOppgaveEventsForAllOppgaveEventsInCache(false)
            }
        } `should throw` BackupEventException::class `with message containing` "Vi stoppet på nr 0 (i batch nr. 1) av totalt 3 eventer"
    }

    @Test
    fun `Skal opprette Done-backup-eventer for alle inaktive Oppgave-eventer`() {
        runBlocking {
            val produceDoneEventsForAllInactiveOppgaveEventsInCache = backupOppgaveService.produceDoneEventsFromAllInactiveOppgaveEvents(false)
            produceDoneEventsForAllInactiveOppgaveEventsInCache `should be equal to` 1
        }
    }

    private fun createOppgave(oppgaver: List<Oppgave>) {
        runBlocking {
            database.dbQuery { createOppgave(oppgaver) }
        }
    }

    private fun createSystembruker(systembruker: String, produsentnavn: String) {
        runBlocking {
            database.dbQuery { createProdusent(systembruker, produsentnavn) }
        }
    }

    private fun deleteOppgave() {
        runBlocking {
            database.dbQuery { deleteAllOppgave() }
        }
    }

    private fun deleteSystembruker(systembruker: String) {
        runBlocking {
            database.dbQuery { deleteProdusent(systembruker) }
        }
    }
}
