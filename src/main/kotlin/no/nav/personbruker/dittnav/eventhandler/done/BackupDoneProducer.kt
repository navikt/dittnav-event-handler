package no.nav.personbruker.dittnav.eventhandler.done

import no.nav.brukernotifikasjon.schemas.Done
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.BackupEventException
import no.nav.personbruker.dittnav.eventhandler.common.kafka.KafkaProducerWrapper
import org.apache.avro.AvroMissingFieldException
import org.apache.avro.AvroRuntimeException
import org.apache.kafka.common.KafkaException
import org.apache.kafka.common.errors.AuthenticationException

class BackupDoneProducer(private val doneKafkaProducer: KafkaProducerWrapper<Done>) {

    fun toSchemasDone(batchNumber: Int, events: List<BackupDone>): MutableList<BackupConvertedEvent> {
        var count = 0
        var convertedEventsList = mutableListOf<BackupConvertedEvent>()
        events.forEach { event ->
            try {
                count++
                val key = createKeyForEvent(event.eventId, event.systembruker)
                val doneEvent = createBackupDoneEvent(event.fodselsnummer, event.grupperingsId, event.eventTidspunkt)
                convertedEventsList.add(BackupConvertedEvent(key, doneEvent))
            } catch (e: AvroMissingFieldException) {
                val msg = "Et eller flere felt er tomme. Vi får feil når vi prøver å konvertere en interne done til schemas.Done. " +
                        "EventId: ${event.eventId}, eventTidspunkt: ${event.eventTidspunkt}. " +
                        "Vi stoppet på nr $count (i batch $batchNumber) av totalt ${events.size} eventer som var i done-listen."
                throw BackupEventException(msg, e)
            } catch (e: AvroRuntimeException) {
                val msg = "Vi får en feil når vi prøver å konvertere interne done til schemas.Done. " +
                        "EventId: ${event.eventId}, eventTidspunkt: ${event.eventTidspunkt}. " +
                        "Vi stoppet på nr $count (i batch $batchNumber) av totalt ${events.size} eventer som var i done-listen."
                throw BackupEventException(msg, e)
            } catch (e: Exception) {
                val msg = "Vi får en ukjent feil når vi prøver å konvertere interne done til schemas.Done. " +
                        "EventId: ${event.eventId}, eventTidspunkt: ${event.eventTidspunkt}. " +
                        "Vi stoppet på nr $count (i batch $batchNumber) av totalt ${events.size} eventer som var i done-listen."
                throw BackupEventException(msg, e)

            }
        }
        return convertedEventsList
    }

    fun produceDoneEvents(batchNumber: Int, events: MutableList<BackupConvertedEvent>): Int {
        var count = 0
        events.forEach { event ->
            try {
                count++
                doneKafkaProducer.sendEvent(event.nokkel, event.doneEvent)
            } catch (e: AuthenticationException) {
                val msg = "Vi får feil når vi prøver å koble oss til Kafka. Prøver å sende done til done-backup-topic-en. " +
                        "EventId: ${event.nokkel.getEventId()}, eventTidspunkt: ${event.doneEvent.getTidspunkt()}. " +
                        "Vi stoppet på nr $count (i batch $batchNumber) av totalt ${events.size} eventer som var i done-listen."
                throw BackupEventException(msg, e)
            } catch (e: KafkaException) {
                val msg = "Producer sin send funksjon feilet i Kafka. Prøver å sende done til done-backup-topic-en. " +
                        "EventId: ${event.nokkel.getEventId()}, eventTidspunkt: ${event.doneEvent.getTidspunkt()}. " +
                        "Vi stoppet på nr $count (i batch $batchNumber) av totalt ${events.size} eventer som var i done-listen."
                throw BackupEventException(msg, e)
            } catch (e: Exception) {
                val msg = "Vi fikk en uventet feil når vi prøver å sende done til done-backup-topic-en. " +
                        "EventId: ${event.nokkel.getEventId()}, eventTidspunkt: ${event.doneEvent.getTidspunkt()}. " +
                        "Vi stoppet på nr $count (i batch $batchNumber) av totalt ${events.size} eventer som var i done-listen."
                throw BackupEventException(msg, e)
            }
        }
        return count
    }
}
