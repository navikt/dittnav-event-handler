package no.nav.personbruker.dittnav.eventhandler.done

import java.time.ZonedDateTime

data class Done(
        val eventId: String,
        val systembruker: String,
        val fodselsnummer: String,
        val grupperingsId: String,
        val eventTidspunkt: ZonedDateTime
) {
    override fun toString(): String {
        return "Done(" +
                "eventId=$eventId, " +
                "systembruker=$systembruker, " +
                "fodselsnummer=***, " +
                "grupperingsId=$grupperingsId, " +
                "eventTidspunkt=$eventTidspunkt"
    }
}
