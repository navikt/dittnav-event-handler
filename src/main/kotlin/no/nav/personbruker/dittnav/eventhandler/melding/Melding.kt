package no.nav.personbruker.dittnav.eventhandler.melding

import no.nav.personbruker.dittnav.eventhandler.common.database.Brukernotifikasjon
import java.time.ZonedDateTime

data class Melding (
        override val id: Int,
        override val produsent: String,
        override val eventTidspunkt: ZonedDateTime,
        override val aktorId: String,
        override val eventId: String,
        override val dokumentId: String,
        override val tekst: String,
        override val link: String,
        override val sikkerhetsnivaa: Int,
        override val sistOppdatert: ZonedDateTime,
        override val aktiv: Boolean
) : Brukernotifikasjon