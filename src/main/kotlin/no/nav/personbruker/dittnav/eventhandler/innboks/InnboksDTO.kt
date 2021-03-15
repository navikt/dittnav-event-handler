package no.nav.personbruker.dittnav.eventhandler.innboks

import kotlinx.serialization.Serializable
import no.nav.personbruker.dittnav.eventhandler.common.serializer.ZonedDateTimeSerializer
import java.time.ZonedDateTime

@Serializable
data class InnboksDTO(
    val produsent: String,
    @Serializable(ZonedDateTimeSerializer::class)
    val eventTidspunkt: ZonedDateTime,
    val fodselsnummer: String,
    val eventId: String,
    val grupperingsId: String,
    val tekst: String,
    val link: String,
    val sikkerhetsnivaa: Int,
    @Serializable(ZonedDateTimeSerializer::class)
    val sistOppdatert: ZonedDateTime,
    val aktiv: Boolean
)
