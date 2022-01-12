package no.nav.personbruker.dittnav.eventhandler.done

import java.time.ZonedDateTime

object DoneObjectMother {

    fun createDone(systembruker: String, eventTidspunkt: ZonedDateTime, fodselsnummer: String, eventId: String, grupperingsId: String): Done {
        return Done(
                systembruker = systembruker,
                eventTidspunkt = eventTidspunkt,
                fodselsnummer = fodselsnummer,
                eventId = eventId,
                grupperingsId = grupperingsId,
                namespace = "dummyNamespace",
                appnavn = "dummyAppnavn")
    }
}
