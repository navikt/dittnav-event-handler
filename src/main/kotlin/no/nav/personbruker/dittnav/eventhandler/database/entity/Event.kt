package no.nav.personbruker.dittnav.eventhandler.database.entity

import java.time.ZonedDateTime

data class Event(
        val id: Int?,
        val produsent: String,
        val eventTidspunkt: ZonedDateTime,
        val aktorId: String,
        val eventId: String,
        val dokumentId: String,
        val tekst: String,
        val link: String,
        val sikkerhetsnivaa: Int,
        val sistOppdatert: ZonedDateTime,
        val aktiv: Boolean
) {
    constructor(produsent: String,
                eventTidspunkt: ZonedDateTime,
                aktorId: String,
                eventId: String,
                dokumentId: String,
                tekst: String,
                link: String,
                sikkerhetsnivaa: Int,
                sistOppdatert: ZonedDateTime,
                aktiv: Boolean) : this(null, produsent, eventTidspunkt, aktorId, eventId, dokumentId, tekst, link,
            sikkerhetsnivaa, sistOppdatert, aktiv)
}
