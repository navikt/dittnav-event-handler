package no.nav.personbruker.dittnav.eventhandler.statusoppdatering

import java.time.ZonedDateTime

class Statusoppdatering(
        val id: Int,
        val produsent: String? = null,
        val systembruker: String? = null,
        val namespace: String,
        val appnavn: String,
        val eventId: String,
        val eventTidspunkt: ZonedDateTime,
        val fodselsnummer: String,
        val grupperingsId: String,
        val link: String,
        val sikkerhetsnivaa: Int,
        val sistOppdatert: ZonedDateTime,
        val statusGlobal: String,
        val statusIntern: String?,
        val sakstema: String
) {
    override fun toString(): String {
        return "Statusoppdatering(" +
                "id=$id, " +
                "systembruker=$systembruker, " +
                "namespace=$namespace, " +
                "appnavn=$appnavn, " +
                "eventId=$eventId, " +
                "eventTidspunkt=$eventTidspunkt, " +
                "fodselsnummer=***, " +
                "grupperingsId=$grupperingsId, " +
                "link=***, " +
                "sikkerhetsnivaa=$sikkerhetsnivaa, " +
                "sistOppdatert=$sistOppdatert, " +
                "statusGlobal=$statusGlobal, " +
                "statusIntern=$statusIntern, " +
                "sakstema=$sakstema, "
    }
}
