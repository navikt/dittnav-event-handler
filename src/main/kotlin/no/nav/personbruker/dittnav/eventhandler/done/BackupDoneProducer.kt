package no.nav.personbruker.dittnav.eventhandler.done

import no.nav.brukernotifikasjon.schemas.Done
import no.nav.brukernotifikasjon.schemas.Nokkel
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.BackupEventException
import no.nav.personbruker.dittnav.eventhandler.common.kafka.KafkaProducerWrapper
import no.nav.personbruker.dittnav.eventhandler.oppgave.OppgaveEventService
import org.apache.avro.AvroMissingFieldException
import org.apache.avro.AvroRuntimeException
import org.apache.kafka.common.KafkaException
import org.apache.kafka.common.errors.AuthenticationException
import org.slf4j.LoggerFactory

class BackupDoneProducer(private val doneKafkaProducer: KafkaProducerWrapper<Done>) {

    private val log = LoggerFactory.getLogger(BackupDoneProducer::class.java)

    fun toSchemasDone(batchNumber: Int, events: List<BackupDone>): MutableMap<Nokkel, Done> {
        var count = 0
        var convertedEvents: MutableMap<Nokkel, Done> = mutableMapOf()
        events.forEach { event ->
            try {
                count++
                val key = createKeyForEvent(event.eventId, event.systembruker)
                val doneEvent = createBackupDoneEvent(event.fodselsnummer, event.grupperingsId, event.eventTidspunkt)
                convertedEvents.put(key, doneEvent)
                log.info("EventId: ${key.getEventId()}, grupperingsId: ${doneEvent.getGrupperingsId()}, eventTidspunkt: ${doneEvent.getTidspunkt()}")
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
        return convertedEvents
    }

    fun produceDoneEvents(batchNumber: Int, events: MutableMap<Nokkel, Done>): Int {
        var count = 0
        events.forEach { event ->
            try {
                count++
                doneKafkaProducer.sendEvent(event.key, event.value)
            } catch (e: AuthenticationException) {
                val msg = "Vi får feil når vi prøver å koble oss til Kafka. Prøver å sende done til done-backup-topic-en. " +
                        "EventId: ${event.key.getEventId()}, eventTidspunkt: ${event.value.getTidspunkt()}. " +
                        "Vi stoppet på nr $count (i batch $batchNumber) av totalt ${events.size} eventer som var i done-listen."
                throw BackupEventException(msg, e)
            } catch (e: KafkaException) {
                val msg = "Producer sin send funksjon feilet i Kafka. Prøver å sende done til done-backup-topic-en. " +
                        "EventId: ${event.key.getEventId()}, eventTidspunkt: ${event.value.getTidspunkt()}. " +
                        "Vi stoppet på nr $count (i batch $batchNumber) av totalt ${events.size} eventer som var i done-listen."
                throw BackupEventException(msg, e)
            } catch (e: Exception) {
                val msg = "Vi fikk en uventet feil når vi prøver å sende done til done-backup-topic-en. " +
                        "EventId: ${event.key.getEventId()}, eventTidspunkt: ${event.value.getTidspunkt()}. " +
                        "Vi stoppet på nr $count (i batch $batchNumber) av totalt ${events.size} eventer som var i done-listen."
                throw BackupEventException(msg, e)
            }
        }
        return count
    }
}
