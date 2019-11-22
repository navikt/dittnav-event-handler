package no.nav.personbruker.dittnav.eventhandler.oppgave

import no.nav.personbruker.dittnav.eventhandler.common.database.Brukernotifikasjon
import java.time.LocalDateTime

data class Oppgave(
        override val aktiv: Boolean,
        override val aktorId: String,
        override val dokumentId: String,
        override val eventId: String,
        override val eventTidspunkt: LocalDateTime,
        override val id: Int?,
        override val produsent: String,
        override val sikkerhetsnivaa: Int,
        override val sistOppdatert: LocalDateTime,
        override val tekst: String,
        override val link: String
) : Brukernotifikasjon
