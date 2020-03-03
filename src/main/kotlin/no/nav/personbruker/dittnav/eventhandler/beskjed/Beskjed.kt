import com.fasterxml.jackson.annotation.JsonIgnore
import no.nav.personbruker.dittnav.eventhandler.common.database.Brukernotifikasjon
import java.time.ZonedDateTime

data class Beskjed(
        @JsonIgnore override val id: Int?,
        val uid: String,
        override val aktiv: Boolean,
        override val fodselsnummer: String,
        override val grupperingsId: String,
        override val eventId: String,
        override val eventTidspunkt: ZonedDateTime,
        @JsonIgnore override val produsent: String,
        override val sikkerhetsnivaa: Int,
        override val sistOppdatert: ZonedDateTime,
        @JsonIgnore val synligFremTil: ZonedDateTime?,
        override val tekst: String,
        override val link: String
) : Brukernotifikasjon
