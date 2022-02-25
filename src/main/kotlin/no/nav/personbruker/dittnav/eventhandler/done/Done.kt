package no.nav.personbruker.dittnav.eventhandler.done

import java.time.ZonedDateTime

data class Done(
        val eventId: String,
        val systembruker: String? = null,
        val namespace: String,
        val appnavn: String,
        val fodselsnummer: String,
        val grupperingsId: String,
        val eventTidspunkt: ZonedDateTime
) {
    override fun toString(): String {
        return "Done(" +
                "eventId=$eventId, " +
                "systembruker=$systembruker, " +
                "namespace=$namespace, " +
                "appnavn=$appnavn, " +
                "fodselsnummer=***, " +
                "grupperingsId=$grupperingsId, " +
                "eventTidspunkt=$eventTidspunkt"
    }
}
