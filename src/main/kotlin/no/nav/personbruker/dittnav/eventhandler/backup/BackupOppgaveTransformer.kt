package no.nav.personbruker.dittnav.eventhandler.backup

import no.nav.brukernotifikasjon.schemas.Nokkel
import no.nav.brukernotifikasjon.schemas.builders.exception.FieldValidationException
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.BackupEventException
import no.nav.personbruker.dittnav.eventhandler.oppgave.Oppgave
import org.apache.avro.AvroMissingFieldException
import org.apache.avro.AvroRuntimeException

class BackupOppgaveTransformer {

    fun toSchemasOppgave(batchNumber: Int, events: List<Oppgave>): MutableMap<Nokkel, no.nav.brukernotifikasjon.schemas.Oppgave> {
        var count = 0
        val convertedEvents: MutableMap<Nokkel, no.nav.brukernotifikasjon.schemas.Oppgave> = mutableMapOf()
        events.forEach { event ->
            try {
                count++
                val key = createKeyForEvent(event.eventId, event.systembruker)
                val oppgaveEvent = createOppgaveEvent(event)
                convertedEvents[key] = oppgaveEvent
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
            } catch (e: FieldValidationException) {
                val msg = "Vi får en valideringsfeil når vi konverterer Oppgave til schemas.Oppgave. " +
                        "EventId: ${event.eventId}, produsent: ${event.produsent}, eventTidspunkt: ${event.eventTidspunkt}. " +
                        "Vi stoppet på nr $count (i batch nr. $batchNumber) av totalt ${events.size} eventer som var i oppgave-listen."
                throw BackupEventException(msg, e)
            } catch (e: Exception) {
                val msg = "Vi får en ukjent feil når vi konverterer Oppgave til schemas.Oppgave. " +
                        "EventId: ${event.eventId}, produsent: ${event.produsent}, eventTidspunkt: ${event.eventTidspunkt}. " +
                        "Vi stoppet på nr $count (i batch nr. $batchNumber) av totalt ${events.size} eventer som var i oppgave-listen."
                throw BackupEventException(msg, e)

            }
        }
        return convertedEvents
    }

    fun toSchemasDone(batchNumber: Int, events: List<Oppgave>): MutableMap<Nokkel, no.nav.brukernotifikasjon.schemas.Done> {
        var count = 0
        var convertedEvents: MutableMap<Nokkel, no.nav.brukernotifikasjon.schemas.Done> = mutableMapOf()
        events.forEach { event ->
            try {
                count++
                val key = createKeyForEvent(event.eventId, event.systembruker)
                val doneEvent = createDoneEvent(event.fodselsnummer, event.grupperingsId, event.sistOppdatert)
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
            } catch (e: Exception) {
                val msg = "Vi får en ukjent feil når vi prøver å konvertere interne inaktive-oppgaver til schemas.Done. " +
                        "EventId: ${event.eventId}, produsent: ${event.produsent}, eventTidspunkt: ${event.eventTidspunkt}. " +
                        "Vi stoppet på nr $count (i batch nr. $batchNumber) av totalt ${events.size} eventer som var i oppgave-listen."
                throw BackupEventException(msg, e)
            }
        }
        return convertedEvents
    }
}
