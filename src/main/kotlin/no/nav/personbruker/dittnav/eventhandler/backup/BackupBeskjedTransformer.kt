package no.nav.personbruker.dittnav.eventhandler.backup

import Beskjed
import no.nav.brukernotifikasjon.schemas.Nokkel
import no.nav.brukernotifikasjon.schemas.builders.exception.FieldValidationException
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.BackupEventException
import org.apache.avro.AvroMissingFieldException
import org.apache.avro.AvroRuntimeException

object BackupBeskjedTransformer {

    fun toSchemasBeskjed(batchNumber: Int, events: List<Beskjed>): MutableMap<Nokkel, no.nav.brukernotifikasjon.schemas.Beskjed> {
        var count = 0
        val convertedEvents: MutableMap<Nokkel, no.nav.brukernotifikasjon.schemas.Beskjed> = mutableMapOf()
        events.forEach { event ->
            try {
                count++
                val key = createKeyForEvent(event.eventId, event.systembruker)
                val beskjedEvent = createBeskjedEvent(event)
                convertedEvents[key] = beskjedEvent
            } catch (e: AvroMissingFieldException) {
                val msg = "Et eller flere felt er tomme. Vi får feil når vi prøver å konvertere en intern beskjed til schemas.Beskjed. " +
                        "EventId: ${event.eventId}, produsent: ${event.produsent}, eventTidspunkt: ${event.eventTidspunkt}. " +
                        "Vi stoppet på nr $count (i batch $batchNumber) av totalt ${events.size} eventer som var i beskjed-listen."
                throw BackupEventException(msg, e)
            } catch (e: AvroRuntimeException) {
                val msg = "Vi får en feil når vi prøver å konvertere interne Beskjed til schemas.Beskjed. " +
                        "EventId: ${event.eventId}, produsent: ${event.produsent}, eventTidspunkt: ${event.eventTidspunkt}. " +
                        "Vi stoppet på nr $count (i batch $batchNumber) av totalt ${events.size} eventer som var i beskjed-listen."
                throw BackupEventException(msg, e)
            } catch (e: FieldValidationException) {
                val msg = "Vi får en valideringsfeil når vi konverterer Beskjed til schemas.Beskjed. " +
                        "EventId: ${event.eventId}, produsent: ${event.produsent}, eventTidspunkt: ${event.eventTidspunkt}. " +
                        "Vi stoppet på nr $count (i batch $batchNumber) av totalt ${events.size} eventer som var i beskjed-listen."
                throw BackupEventException(msg, e)
            } catch (e: Exception) {
                val msg = "Vi får en ukjent feil når vi konverterer Beskjed til schemas.Beskjed. " +
                        "EventId: ${event.eventId}, produsent: ${event.produsent}, eventTidspunkt: ${event.eventTidspunkt}. " +
                        "Vi stoppet på nr $count (i batch $batchNumber) av totalt ${events.size} eventer som var i beskjed-listen."
                throw BackupEventException(msg, e)
            }
        }
        return convertedEvents
    }

    fun toSchemasDone(batchNumber: Int, events: List<Beskjed>): MutableMap<Nokkel, no.nav.brukernotifikasjon.schemas.Done> {
        var count = 0
        val convertedEvents: MutableMap<Nokkel, no.nav.brukernotifikasjon.schemas.Done> = mutableMapOf()
        events.forEach { event ->
            try {
                count++
                val key = createKeyForEvent(event.eventId, event.systembruker)
                val doneEvent = createDoneEvent(event.fodselsnummer, event.grupperingsId, event.sistOppdatert)
                convertedEvents[key] = doneEvent
            } catch (e: AvroMissingFieldException) {
                val msg = "Et eller flere felt er tomme. Vi får feil når vi prøver å konvertere en interne inaktive-beskjed til schemas.Done. " +
                        "EventId: ${event.eventId}, produsent: ${event.produsent}, eventTidspunkt: ${event.eventTidspunkt}. " +
                        "Vi stoppet på nr $count (i batch $batchNumber) av totalt ${events.size} eventer som var i beskjed-listen."
                throw BackupEventException(msg, e)
            } catch (e: AvroRuntimeException) {
                val msg = "Vi får en feil når vi prøver å konvertere interne inaktive-beskjeder til schemas.Done. " +
                        "EventId: ${event.eventId}, produsent: ${event.produsent}, eventTidspunkt: ${event.eventTidspunkt}. " +
                        "Vi stoppet på nr $count (i batch $batchNumber) av totalt ${events.size} eventer som var i beskjed-listen."
                throw BackupEventException(msg, e)
            } catch (e: Exception) {
                val msg = "Vi får en ukjent feil når vi prøver å konvertere interne inaktive-beskjeder til schemas.Done. " +
                        "EventId: ${event.eventId}, produsent: ${event.produsent}, eventTidspunkt: ${event.eventTidspunkt}. " +
                        "Vi stoppet på nr $count (i batch $batchNumber) av totalt ${events.size} eventer som var i beskjed-listen."
                throw BackupEventException(msg, e)
            }
        }
        return convertedEvents
    }
}
