package no.nav.personbruker.dittnav.eventhandler.oppgave

import kotlinx.serialization.Serializable
import no.nav.personbruker.dittnav.eventhandler.common.serializer.ZonedDateTimeSerializer
import java.time.ZonedDateTime

@Serializable
data class OppgaveDTO(
    val fodselsnummer: String,
    val grupperingsId: String,
    val eventId: String,
    @Serializable(ZonedDateTimeSerializer::class)
    val eventTidspunkt: ZonedDateTime,
    val produsent: String,
    val sikkerhetsnivaa: Int,
    @Serializable(ZonedDateTimeSerializer::class)
    val sistOppdatert: ZonedDateTime,
    val tekst: String,
    val link: String,
    val aktiv: Boolean
)

