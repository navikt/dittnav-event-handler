package no.nav.personbruker.dittnav.eventhandler.statusoppdatering

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.ZonedDateTime

class Statusoppdatering(
        @JsonIgnore val id: Int,
        val produsent: String,
        @JsonIgnore val systembruker: String,
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
                "systembruker=***, " +
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