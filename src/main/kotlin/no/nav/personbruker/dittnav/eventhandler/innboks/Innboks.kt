package no.nav.personbruker.dittnav.eventhandler.innboks

import com.fasterxml.jackson.annotation.JsonIgnore
import no.nav.personbruker.dittnav.eventhandler.common.database.Brukernotifikasjon
import java.time.ZonedDateTime

data class Innboks (
        @JsonIgnore override val id: Int,
        override val produsent: String,
        override val eventTidspunkt: ZonedDateTime,
        override val fodselsnummer: String,
        override val eventId: String,
        override val grupperingsId: String,
        override val tekst: String,
        override val link: String,
        override val sikkerhetsnivaa: Int,
        override val sistOppdatert: ZonedDateTime,
        override val aktiv: Boolean
) : Brukernotifikasjon