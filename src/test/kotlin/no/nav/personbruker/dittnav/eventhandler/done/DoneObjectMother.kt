package no.nav.personbruker.dittnav.eventhandler.done

import java.time.ZoneId
import java.time.ZonedDateTime

object DoneObjectMother {

    fun createDone(eventId: String, fodselsnummer: String): Done {
        return Done(
                eventId = eventId,
                systembruker = "x-dittnav",
                fodselsnummer = fodselsnummer,
                grupperingsId = "100$fodselsnummer",
                eventTidspunkt = ZonedDateTime.now(ZoneId.of("Europe/Oslo"))
        )
    }

    fun createDone(systembruker: String, eventTidspunkt: ZonedDateTime, fodselsnummer: String, eventId: String, grupperingsId: String): Done {
        return Done(
                systembruker = systembruker,
                eventTidspunkt = eventTidspunkt,
                fodselsnummer = fodselsnummer,
                eventId = eventId,
                grupperingsId = grupperingsId)
    }
}
