package no.nav.personbruker.dittnav.eventhandler.beskjed

import kotlinx.serialization.Serializable
import no.nav.personbruker.dittnav.eventhandler.common.serializer.ZonedDateTimeSerializer
import java.time.ZonedDateTime

@Serializable
data class BeskjedDTO(
    val uid: String,
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
