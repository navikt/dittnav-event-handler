package no.nav.personbruker.dittnav.eventhandler.innboks

import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.EksternVarslingInfo
import java.time.ZonedDateTime

data class Innboks(
        val id: Int,
        val produsent: String,
        val systembruker: String,
        val namespace: String,
        val appnavn: String,
        val eventTidspunkt: ZonedDateTime,
        val forstBehandlet: ZonedDateTime,
        val fodselsnummer: String,
        val eventId: String,
        val grupperingsId: String,
        val tekst: String,
        val link: String,
        val sikkerhetsnivaa: Int,
        val sistOppdatert: ZonedDateTime,
        val aktiv: Boolean,
        val eksternVarslingInfo: EksternVarslingInfo
) {
    override fun toString(): String {
        return "Innboks(" +
                "id=$id, " +
                "produsent=$produsent, " +
                "systembruker=$systembruker, " +
                "namespace=$namespace, " +
                "appnavn=$appnavn, " +
                "eventTidspunkt=$eventTidspunkt, " +
                "forstBehandlet=$forstBehandlet, " +
                "fodselsnummer=***, " +
                "eventId=$eventId, " +
                "grupperingsId=$grupperingsId, " +
                "tekst=***, " +
                "link=***, " +
                "sikkerhetsnivaa=$sikkerhetsnivaa, " +
                "sistOppdatert=$sistOppdatert, " +
                "aktiv=$aktiv"
    }
}
