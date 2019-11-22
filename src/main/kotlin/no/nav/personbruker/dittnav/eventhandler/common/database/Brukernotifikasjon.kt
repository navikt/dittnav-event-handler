package no.nav.personbruker.dittnav.eventhandler.common.database

import java.time.LocalDateTime

interface Brukernotifikasjon {
    val id: Int?
    val produsent: String
    val eventTidspunkt: LocalDateTime
    val aktorId: String
    val eventId: String
    val dokumentId: String
    val sikkerhetsnivaa: Int
    val sistOppdatert: LocalDateTime
    val aktiv: Boolean
    val tekst: String
    val link: String
}
