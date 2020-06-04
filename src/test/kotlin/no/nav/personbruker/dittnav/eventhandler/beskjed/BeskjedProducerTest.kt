package no.nav.personbruker.dittnav.eventhandler.beskjed

import Beskjed
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.brukernotifikasjon.schemas.Done
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.BackupEventException
import no.nav.personbruker.dittnav.eventhandler.common.kafka.KafkaProducerWrapper
import org.amshove.kluent.`should throw`
import org.amshove.kluent.invoking
import org.apache.kafka.common.KafkaException
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

class BeskjedProducerTest {
    val kafkaProducerDoneBackup = mockk<KafkaProducerWrapper<Done>>()
    val kafkaProducerBeskjedBackup = mockk<KafkaProducerWrapper<no.nav.brukernotifikasjon.schemas.Beskjed>>()
    val beskjedProducer = BeskjedProducer(kafkaProducerBeskjedBackup, kafkaProducerDoneBackup)

    @Test
    fun `Kaster BackupEventException hvis kafka feiler`() {
        val beskjedList = getBeskjedList()
        invoking {
            runBlocking {

                coEvery {
                    kafkaProducerBeskjedBackup.sendEvent(any(), any())
                }.throws(KafkaException("Simulert feil i en test"))

                val beskjedEvents = beskjedProducer.toSchemasBeskjed(beskjedList)
                beskjedProducer.produceAllBeskjedEvents(beskjedEvents)
            }
        } `should throw` BackupEventException::class

    }

    fun getBeskjedList(): MutableList<Beskjed> {
        return mutableListOf(
                BeskjedObjectMother.createBeskjed(1, "1", "123", null, "123", true),
                BeskjedObjectMother.createBeskjed(2, "2", "456", ZonedDateTime.now().minusDays(2), "124", true),
                BeskjedObjectMother.createBeskjed(3, "3", "123", null, "125", true))
    }
}