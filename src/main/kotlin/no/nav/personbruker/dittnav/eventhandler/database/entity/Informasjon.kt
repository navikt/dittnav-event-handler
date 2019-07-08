package no.nav.personbruker.dittnav.eventhandler.database.entity

import org.joda.time.DateTime

data class Informasjon(
        val id: Int?,
        val produsent: String,
        val eventTidspunkt: DateTime,
        val aktorId: String,
        val eventId: String,
        val dokumentId: String,
        val tekst: String,
        val link: String,
        val sikkerhetsnivaa: Int,
        val sistOppdatert: DateTime,
        val aktiv: Boolean
) {
    constructor(produsent: String,
                eventTidspunkt: DateTime,
                aktorId: String,
                eventId: String,
                dokumentId: String,
                tekst: String,
                link: String,
                sikkerhetsnivaa: Int,
                sistOppdatert: DateTime,
                aktiv: Boolean) : this(null, produsent, eventTidspunkt, aktorId, eventId, dokumentId, tekst, link,
            sikkerhetsnivaa, sistOppdatert, aktiv)
}