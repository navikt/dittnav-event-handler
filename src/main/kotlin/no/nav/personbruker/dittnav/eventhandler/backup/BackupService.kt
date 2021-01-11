package no.nav.personbruker.dittnav.eventhandler.backup

import no.nav.brukernotifikasjon.schemas.Nokkel
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.BackupEventException
import no.nav.personbruker.dittnav.eventhandler.common.kafka.KafkaProducerWrapper
import no.nav.personbruker.dittnav.eventhandler.config.Kafka
import org.apache.kafka.common.KafkaException
import org.apache.kafka.common.errors.AuthenticationException
import java.sql.Connection

abstract class BackupService<T>(private val database: Database) {

    suspend fun getEventsFromCache(operationToExecute: Connection.() -> List<T>): List<T> {
        return database.queryWithExceptionTranslation {
            operationToExecute()
        }
    }

    protected fun <S> produceKafkaEventsForAllEventsInCache(kafkaProducerWrapper: KafkaProducerWrapper<S>, dryrun: Boolean, converter: (batchNumber: Int, events: List<T>) -> MutableMap<Nokkel, S>, events: List<T>): Int {
        var batchNumber = 0
        var numberOfProcessedEvents = 0
        if (events.isNotEmpty()) {
            events.chunked(Kafka.BACKUP_EVENT_CHUNCK_SIZE) { listChunck ->
                batchNumber++
                val convertedEvents = converter.invoke(batchNumber, listChunck)
                numberOfProcessedEvents += if(!dryrun) {
                    produceKafkaEvents(kafkaProducerWrapper, dryrun, batchNumber, convertedEvents)
                }
                else {
                    convertedEvents.size
                }
            }
        }
        return numberOfProcessedEvents
    }

    private fun <S> produceKafkaEvents(kafkaProducer: KafkaProducerWrapper<S>, dryRun: Boolean, batchNumber: Int, convertedEvents: MutableMap<Nokkel, S>): Int {
        var count = 0
        convertedEvents.forEach { event ->
            try {
                if(!dryRun) {
                    kafkaProducer.sendEvent(event.key, event.value)
                }
                count++
            } catch (e: AuthenticationException) {
                val msg = "Vi får feil når vi prøver å koble oss til Kafka (topic ${kafkaProducer.topicName}). " +
                        "EventId: ${event.key.getEventId()}. " +
                        "Vi stoppet på nr $count (i batch nr. ${batchNumber}) av totalt ${convertedEvents.size} eventer som var i event-listen."
                throw BackupEventException(msg, e)
            } catch (e: KafkaException) {
                val msg = "Producer sin send funksjon feilet i Kafka (topic ${kafkaProducer.topicName}). " +
                        "EventId: ${event.key.getEventId()}. " +
                        "Vi stoppet på nr $count (i batch nr. ${batchNumber}) av totalt ${convertedEvents.size} eventer som var i event-listen."
                throw BackupEventException(msg, e)
            } catch (e: Exception) {
                val msg = "Vi fikk en uventet feil når vi skriver til topic ${kafkaProducer.topicName}. " +
                        "EventId: ${event.key.getEventId()}. " +
                        "Vi stoppet på nr $count (i batch nr. ${batchNumber}) av totalt ${convertedEvents.size} eventer som var i event-listen."
                throw BackupEventException(msg, e)
            }
        }
        return count
    }
}
