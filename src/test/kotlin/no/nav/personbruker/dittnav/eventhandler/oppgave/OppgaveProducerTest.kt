package no.nav.personbruker.dittnav.eventhandler.oppgave

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

internal class OppgaveProducerTest {

    val kafkaProducerDoneBackup = mockk<KafkaProducerWrapper<Done>>()
    val kafkaProducerOppgaveBackup = mockk<KafkaProducerWrapper<no.nav.brukernotifikasjon.schemas.Oppgave>>()
    val oppgaveProducer = OppgaveProducer(kafkaProducerOppgaveBackup, kafkaProducerDoneBackup)

    @Test
    fun `Kaster exception hvis kafka feiler`() {
        val oppgaveList = getOppgaveList()
        invoking {
            runBlocking {

                coEvery {
                    kafkaProducerOppgaveBackup.sendEvent(any(), any())
                }.throws(KafkaException("Simulert feil i en test"))

                val oppgaveEvents = oppgaveProducer.toSchemasOppgave(1, oppgaveList)
                oppgaveProducer.produceAllOppgaveEvents(1, oppgaveEvents)

            }
        } `should throw` BackupEventException::class

    }

    fun getOppgaveList(): MutableList<Oppgave> {
        return mutableListOf(
                OppgaveObjectMother.createOppgave(1, "1", "123", true),
                OppgaveObjectMother.createOppgave(2, "2", "456", true),
                OppgaveObjectMother.createOppgave(3, "3", "123", true))
    }
}
