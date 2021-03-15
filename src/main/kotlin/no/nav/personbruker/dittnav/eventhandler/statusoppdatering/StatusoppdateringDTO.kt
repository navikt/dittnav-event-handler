package no.nav.personbruker.dittnav.eventhandler.statusoppdatering

import kotlinx.serialization.Serializable
import no.nav.personbruker.dittnav.eventhandler.common.serializer.ZonedDateTimeSerializer
import java.time.ZonedDateTime

@Serializable
data class StatusoppdateringDTO(
    val produsent: String,
    val eventId: String,
    @Serializable(ZonedDateTimeSerializer::class)
    val eventTidspunkt: ZonedDateTime,
    val fodselsnummer: String,
    val grupperingsId: String,
    val link: String,
    val sikkerhetsnivaa: Int,
    @Serializable(ZonedDateTimeSerializer::class)
    val sistOppdatert: ZonedDateTime,
    val statusGlobal: String,
    val statusIntern: String?,
    val sakstema: String
)

