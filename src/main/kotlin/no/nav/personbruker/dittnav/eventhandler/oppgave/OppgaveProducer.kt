package no.nav.personbruker.dittnav.eventhandler.oppgave

import no.nav.brukernotifikasjon.schemas.Done
import no.nav.brukernotifikasjon.schemas.Nokkel
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.BackupEventException
import no.nav.personbruker.dittnav.eventhandler.common.kafka.KafkaProducerWrapper
import no.nav.personbruker.dittnav.eventhandler.done.createDoneEvent
import no.nav.personbruker.dittnav.eventhandler.done.createKeyForEvent
import org.apache.avro.AvroMissingFieldException
import org.apache.avro.AvroRuntimeException
import org.apache.kafka.common.KafkaException
import org.apache.kafka.common.errors.AuthenticationException

class OppgaveProducer(
        private val oppgaveKafkaProducer: KafkaProducerWrapper<no.nav.brukernotifikasjon.schemas.Oppgave>,
        private val doneKafkaProducer: KafkaProducerWrapper<Done>
) {

    fun toSchemasOppgave(batchNumber: Int, events: List<Oppgave>): MutableMap<Nokkel, no.nav.brukernotifikasjon.schemas.Oppgave> {
        var count = 0
        var convertedEvents: MutableMap<Nokkel, no.nav.brukernotifikasjon.schemas.Oppgave> = mutableMapOf()
        events.forEach { event ->
            try {
                count++
                val key = createKeyForEvent(event.eventId, event.systembruker)
                val oppgaveEvent = createOppgaveEvent(event)
                convertedEvents.put(key, oppgaveEvent)
            } catch (e: AvroMissingFieldException) {
                val msg = "Et eller flere felt er tomme. Vi får feil når vi prøver å konvertere en intern Oppgave til schemas.Oppgave. " +
                        "EventId: ${event.eventId}, produsent: ${event.produsent}, eventTidspunkt: ${event.eventTidspunkt}. " +
                        "Vi stoppet på nr $count (i batch nr. $batchNumber) av totalt ${events.size} eventer som var i oppgave-listen."
                throw BackupEventException(msg, e)
            } catch (e: AvroRuntimeException) {
                val msg = "Vi får en feil når vi prøver å konvertere interne Oppgaver til schemas.Oppgaver. " +
                        "EventId: ${event.eventId}, produsent: ${event.produsent}, eventTidspunkt: ${event.eventTidspunkt}. " +
                        "Vi stoppet på nr $count (i batch nr. $batchNumber) av totalt ${events.size} eventer som var i oppgave-listen."
                throw BackupEventException(msg, e)
            } catch (e: BackupEventException) {
                val msg = "Vi får en valideringsfeil når vi konverterer Oppgave til schemas.Oppgave. " +
                        "EventId: ${event.eventId}, produsent: ${event.produsent}, eventTidspunkt: ${event.eventTidspunkt}. " +
                        "Vi stoppet på nr $count (i batch nr. $batchNumber) av totalt ${events.size} eventer som var i oppgave-listen."
                throw BackupEventException(msg, e)
            }
        }
        return convertedEvents
    }

    fun produceAllOppgaveEvents(batchNumber: Int, events: MutableMap<Nokkel, no.nav.brukernotifikasjon.schemas.Oppgave>): Int {
        var count = 0
        events.forEach { event ->
            try {
                count++
                oppgaveKafkaProducer.sendEvent(event.key, event.value)
            } catch (e: AuthenticationException) {
                val msg = "Vi får feil når vi prøver å koble oss til Kafka (oppgave-backup-topic). " +
                        "EventId: ${event.key.getEventId()}, eventTidspunkt: ${event.value.getTidspunkt()}. " +
                        "Vi stoppet på nr $count (i batch nr. $batchNumber) av totalt ${events.size} eventer som var i oppgave-listen."
                throw BackupEventException(msg, e)
            } catch (e: KafkaException) {
                val msg = "Producer sin send funksjon feilet i Kafka (oppgave-backup-topic). " +
                        "EventId: ${event.key.getEventId()}, eventTidspunkt: ${event.value.getTidspunkt()}. " +
                        "Vi stoppet på nr $count (i batch nr. $batchNumber) av totalt ${events.size} eventer som var i oppgave-listen."
                throw BackupEventException(msg, e)
            } catch (e: Exception) {
                val msg = "Vi fikk en uventet feil når vi skriver til oppgave-backup-topic. " +
                        "EventId: ${event.key.getEventId()}, eventTidspunkt: ${event.value.getTidspunkt()}. " +
                        "Vi stoppet på nr $count (i batch nr. $batchNumber) av totalt ${events.size} eventer som var i oppgave-listen."
                throw BackupEventException(msg, e)
            }
        }
        return count
    }

    fun toSchemasDone(batchNumber: Int, events: List<Oppgave>): MutableMap<Nokkel, Done> {
        var count = 0
        var convertedEvents: MutableMap<Nokkel, Done> = mutableMapOf()
        events.forEach { event ->
            try {
                count++
                val key = createKeyForEvent(event.eventId, event.systembruker)
                val doneEvent = createDoneEvent(event.fodselsnummer, event.grupperingsId)
                convertedEvents.put(key, doneEvent)
            } catch (e: AvroMissingFieldException) {
                val msg = "Et eller flere felt er tomme. Vi får feil når vi prøver å konvertere en interne inaktive-oppgaver til schemas.Done. " +
                        "EventId: ${event.eventId}, produsent: ${event.produsent}, eventTidspunkt: ${event.eventTidspunkt}. " +
                        "Vi stoppet på nr $count (i batch nr. $batchNumber) av totalt ${events.size} eventer som var i oppgave-listen."
                throw BackupEventException(msg, e)
            } catch (e: AvroRuntimeException) {
                val msg = "Vi får en feil når vi prøver å konvertere interne inaktive-oppgaver til schemas.Done. " +
                        "EventId: ${event.eventId}, produsent: ${event.produsent}, eventTidspunkt: ${event.eventTidspunkt}. " +
                        "Vi stoppet på nr $count (i batch nr. $batchNumber) av totalt ${events.size} eventer som var i oppgave-listen."
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
                val msg = "Vi får feil når vi prøver å koble oss til Kafka. Prøver å sende inaktive oppgaver til done-backup-topic-en. " +
                        "EventId: ${event.key.getEventId()}, eventTidspunkt: ${event.value.getTidspunkt()}. " +
                        "Vi stoppet på nr $count (i batch nr. $batchNumber) av totalt ${events.size} eventer som var i oppgave-listen."
                throw BackupEventException(msg, e)
            } catch (e: KafkaException) {
                val msg = "Producer sin send funksjon feilet i Kafka. Prøver å sende inaktive oppgaver til done-backup-topic-en. " +
                        "EventId: ${event.key.getEventId()}, eventTidspunkt: ${event.value.getTidspunkt()}. " +
                        "Vi stoppet på nr $count (i batch nr. $batchNumber) av totalt ${events.size} eventer som var i oppgave-listen."
                throw BackupEventException(msg, e)
            } catch (e: Exception) {
                val msg = "Vi fikk en uventet feil når vi prøver å sende inaktive oppgaver til done-backup-topic-en. " +
                        "EventId: ${event.key.getEventId()}, eventTidspunkt: ${event.value.getTidspunkt()}. " +
                        "Vi stoppet på nr $count (i batch nr. $batchNumber) av totalt ${events.size} eventer som var i oppgave-listen."
                throw BackupEventException(msg, e)
            }
        }
        return count
    }
}
