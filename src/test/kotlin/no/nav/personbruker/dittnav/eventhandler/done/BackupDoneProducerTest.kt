package no.nav.personbruker.dittnav.eventhandler.done

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.brukernotifikasjon.schemas.Done
import no.nav.personbruker.dittnav.eventhandler.backup.BackupDoneProducer
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.BackupEventException
import no.nav.personbruker.dittnav.eventhandler.common.kafka.KafkaProducerWrapper
import org.amshove.kluent.`should throw`
import org.amshove.kluent.invoking
import org.apache.kafka.common.KafkaException
import org.junit.jupiter.api.Test

class BackupDoneProducerTest {

    val backupDoneKafkaProducer = mockk<KafkaProducerWrapper<Done>>()
    val backupDoneProducer = BackupDoneProducer(backupDoneKafkaProducer)

    @Test
    fun `Kaster BackupEventException hvis kafka feiler`() {
        val beskjedList = getBackupDoneList()
        invoking {
            runBlocking {

                coEvery {
                    backupDoneKafkaProducer.sendEvent(any(), any())
                }.throws(KafkaException("Simulert feil i en test"))
                val doneEvents = backupDoneProducer.toSchemasDone(1, beskjedList)
                backupDoneProducer.produceDoneEvents(1, doneEvents)
            }
        } `should throw` BackupEventException::class
    }

    fun getBackupDoneList(): MutableList<BackupDone> {
        return mutableListOf(
                BackupDoneObjectMother.createBackupDone("1", "123"),
                BackupDoneObjectMother.createBackupDone("2","456"),
                BackupDoneObjectMother.createBackupDone("3", "789"))
    }

}
