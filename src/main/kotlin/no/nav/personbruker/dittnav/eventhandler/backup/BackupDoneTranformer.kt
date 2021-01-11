package no.nav.personbruker.dittnav.eventhandler.backup

import no.nav.brukernotifikasjon.schemas.Nokkel
import no.nav.brukernotifikasjon.schemas.builders.exception.FieldValidationException
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.BackupEventException
import no.nav.personbruker.dittnav.eventhandler.done.Done
import org.apache.avro.AvroMissingFieldException
import org.apache.avro.AvroRuntimeException

object BackupDoneTranformer {

     fun toSchemasDone(batchNumber: Int, events: List<Done>): MutableMap<Nokkel, no.nav.brukernotifikasjon.schemas.Done> {
        var count = 0
        val convertedEvents: MutableMap<Nokkel, no.nav.brukernotifikasjon.schemas.Done> = mutableMapOf()
        events.forEach { event ->
            try {
                count++
                val key = createKeyForEvent(event.eventId, event.systembruker)
                val doneEvent = createDoneEvent(event.fodselsnummer, event.grupperingsId, event.eventTidspunkt)
                convertedEvents[key] = doneEvent
            } catch (e: AvroMissingFieldException) {
                val msg = "Et eller flere felt er tomme. Vi får feil når vi prøver å konvertere en interne Done til schemas.Done. " +
                    "EventId: ${event.eventId}, eventTidspunkt: ${event.eventTidspunkt}. " +
                    "Vi stoppet på nr $count (i batch $batchNumber) av totalt ${events.size} eventer som var i done-listen."
                throw BackupEventException(msg, e)
            } catch (e: AvroRuntimeException) {
                val msg = "Vi får en feil når vi prøver å konvertere interne Done til schemas.Done. " +
                        "EventId: ${event.eventId}, eventTidspunkt: ${event.eventTidspunkt}. " +
                        "Vi stoppet på nr $count (i batch $batchNumber) av totalt ${events.size} eventer som var i done-listen."
                throw BackupEventException(msg, e)
            } catch (e: FieldValidationException) {
                val msg = "Vi får en valideringsfeil når vi konverterer interne Done til schemas.Done. " +
                        "EventId: ${event.eventId}, systembruker: ${event.systembruker}, eventTidspunkt: ${event.eventTidspunkt}. " +
                        "Vi stoppet på nr $count (i batch $batchNumber) av totalt ${events.size} eventer som var i beskjed-listen."
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
}
