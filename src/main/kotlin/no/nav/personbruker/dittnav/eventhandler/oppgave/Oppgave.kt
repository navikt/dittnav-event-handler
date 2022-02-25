package no.nav.personbruker.dittnav.eventhandler.oppgave

import java.time.ZonedDateTime

data class Oppgave(
        val id: Int,
        val fodselsnummer: String,
        val grupperingsId: String,
        val eventId: String,
        val eventTidspunkt: ZonedDateTime,
        val produsent: String? = null,
        val systembruker: String? = null,
        val namespace: String,
        val appnavn: String,
        val sikkerhetsnivaa: Int,
        val sistOppdatert: ZonedDateTime,
        val tekst: String,
        val link: String,
        val aktiv: Boolean
) {
    override fun toString(): String {
        return "Oppgave(" +
                "id=$id, " +
                "fodselsnummer=***, " +
                "grupperingsId=$grupperingsId, " +
                "eventId=$eventId, " +
                "eventTidspunkt=$eventTidspunkt, " +
                "produsent=$produsent, " +
                "systembruker=$systembruker, " +
                "namespace=$namespace, " +
                "appnavn=$appnavn, " +
                "sikkerhetsnivaa=$sikkerhetsnivaa, " +
                "sistOppdatert=$sistOppdatert, " +
                "tekst=***, " +
                "link=***, " +
                "aktiv=$aktiv"
    }
}
