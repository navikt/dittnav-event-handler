package no.nav.personbruker.dittnav.eventhandler.beskjed


import Beskjed
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.BackupEventException
import no.nav.personbruker.dittnav.eventhandler.common.kafka.KafkaProducerWrapper
import no.nav.personbruker.dittnav.eventhandler.done.createDoneEvent
import no.nav.personbruker.dittnav.eventhandler.done.createKeyForEvent
import org.apache.avro.AvroMissingFieldException
import org.apache.avro.AvroRuntimeException
import org.apache.kafka.common.KafkaException
import org.apache.kafka.common.errors.AuthenticationException

class BeskjedProducer(
        private val beskjedKafkaProducer: KafkaProducerWrapper<no.nav.brukernotifikasjon.schemas.Beskjed>,
        private val doneKafkaProducer: KafkaProducerWrapper<no.nav.brukernotifikasjon.schemas.Done>
) {

    fun produceAllBeskjedEventsFromList(events: List<Beskjed>) {
        var count = 0
        events.forEach { event ->
            try {
                count++
                val key = createKeyForEvent(event.eventId, event.systembruker)
                val beskjedEvent = createBeskjedEvent(event)
                beskjedKafkaProducer.sendEvent(key, beskjedEvent)
            } catch (e: AvroMissingFieldException) {
                val msg = "Et eller flere felt er tomme. Vi får feil når vi prøver å konvertere en intern beskjed til schemas.Beskjed. " +
                        "EventId: ${event.eventId}, produsent: ${event.produsent}, eventTidspunkt: ${event.eventTidspunkt}. " +
                        "Vi stoppet på nr $count av totalt ${events.size} eventer som var i beskjed-listen."
                throw BackupEventException(msg, e)
            } catch (e: AvroRuntimeException) {
                val msg = "Vi får en feil når vi prøver å konvertere interne Beskjed til schemas.Beskjed. " +
                        "EventId: ${event.eventId}, produsent: ${event.produsent}, eventTidspunkt: ${event.eventTidspunkt}. " +
                        "Vi stoppet på nr $count av totalt ${events.size} eventer som var i beskjed-listen."
                throw BackupEventException(msg, e)
            } catch (e: AuthenticationException) {
                val msg = "Vi får feil når vi prøver å koble oss til Kafka (beskjed-backup-topic). " +
                        "EventId: ${event.eventId}, produsent: ${event.produsent}, eventTidspunkt: ${event.eventTidspunkt}. " +
                        "Vi stoppet på nr $count av totalt ${events.size} eventer som var i oppgave-listen."
                throw BackupEventException(msg, e)
            } catch (e: KafkaException) {
                val msg = "Producer sin send funksjon feilet i Kafka (beskjed-backup-topic). " +
                        "EventId: ${event.eventId}, produsent: ${event.produsent}, eventTidspunkt: ${event.eventTidspunkt}. " +
                        "Vi stoppet på nr $count av totalt ${events.size} eventer som var i beskjed-listen."
                throw BackupEventException(msg, e)
            } catch (e: Exception) {
                val msg = "Vi fikk en uventet feil når vi skriver til beskjed-backup-topic-en. " +
                        "EventId: ${event.eventId}, produsent: ${event.produsent}, eventTidspunkt: ${event.eventTidspunkt}. " +
                        "Vi stoppet på nr $count av totalt ${events.size} eventer som var i beskjed-listen."
                throw BackupEventException(msg, e)
            }
        }
    }

    fun produceDoneEventFromInactiveBeskjedEvents(events: List<Beskjed>) {
        var count = 0
        events.forEach { event ->
            try {
                count++
                val key = createKeyForEvent(event.eventId, event.systembruker)
                val doneEvent = createDoneEvent(event.fodselsnummer, event.grupperingsId)
                doneKafkaProducer.sendEvent(key, doneEvent)
            } catch (e: AvroMissingFieldException) {
                val msg = "Et eller flere felt er tomme. Vi får feil når vi prøver å konvertere en interne inaktive-beskjed til schemas.Done. " +
                        "EventId: ${event.eventId}, produsent: ${event.produsent}, eventTidspunkt: ${event.eventTidspunkt}. " +
                        "Vi stoppet på nr $count av totalt ${events.size} eventer som var i beskjed-listen."
                throw BackupEventException(msg, e)
            } catch (e: AvroRuntimeException) {
                val msg = "Vi får en feil når vi prøver å konvertere interne inaktive-beskjeder til schemas.Done. " +
                        "EventId: ${event.eventId}, produsent: ${event.produsent}, eventTidspunkt: ${event.eventTidspunkt}. " +
                        "Vi stoppet på nr $count av totalt ${events.size} eventer som var i beskjed-listen."
                throw BackupEventException(msg, e)
            } catch (e: AuthenticationException) {
                val msg = "Vi får feil når vi prøver å koble oss til Kafka. Prøver å sende inaktive beskjeder til done-backup-topic-en. " +
                        "EventId: ${event.eventId}, produsent: ${event.produsent}, eventTidspunkt: ${event.eventTidspunkt}. " +
                        "Vi stoppet på nr $count av totalt ${events.size} eventer som var i beskjed-listen."
                throw BackupEventException(msg, e)
            } catch (e: KafkaException) {
                val msg = "Producer sin send funksjon feilet i Kafka. Prøver å sende inaktive beskjeder til done-backup-topic-en. " +
                        "EventId: ${event.eventId}, produsent: ${event.produsent}, eventTidspunkt: ${event.eventTidspunkt}. " +
                        "Vi stoppet på nr $count av totalt ${events.size} eventer som var i beskjed-listen."
                throw BackupEventException(msg, e)
            } catch (e: Exception) {
                val msg = "Vi fikk en uventet feil når vi prøver å sende inaktive beskjeder til done-backup-topic-en. " +
                        "EventId: ${event.eventId}, produsent: ${event.produsent}, eventTidspunkt: ${event.eventTidspunkt}. " +
                        "Vi stoppet på nr $count av totalt ${events.size} eventer som var i beskjed-listen."
                throw BackupEventException(msg, e)
            }
        }
    }

}