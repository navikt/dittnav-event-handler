package no.nav.personbruker.dittnav.eventhandler.innboks

import no.nav.personbruker.dittnav.eventhandler.common.database.Brukernotifikasjon
import java.time.LocalDateTime

data class Innboks (
        override val id: Int,
        override val produsent: String,
        override val eventTidspunkt: LocalDateTime,
        override val aktorId: String,
        override val eventId: String,
        override val dokumentId: String,
        override val tekst: String,
        override val link: String,
        override val sikkerhetsnivaa: Int,
        override val sistOppdatert: LocalDateTime,
        override val aktiv: Boolean
) : Brukernotifikasjon