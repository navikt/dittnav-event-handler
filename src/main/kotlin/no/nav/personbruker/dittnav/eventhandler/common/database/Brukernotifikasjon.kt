package no.nav.personbruker.dittnav.eventhandler.common.database

import java.time.ZonedDateTime

interface Brukernotifikasjon {
    val id: Int?
    val produsent: String
    val eventTidspunkt: ZonedDateTime
    val fodselsnummer: String
    val eventId: String
    val grupperingsId: String
    val sikkerhetsnivaa: Int
    val sistOppdatert: ZonedDateTime
    val aktiv: Boolean
    val tekst: String
    val link: String
}
