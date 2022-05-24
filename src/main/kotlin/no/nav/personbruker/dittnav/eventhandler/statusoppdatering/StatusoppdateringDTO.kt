@file:UseSerializers(ZonedDateTimeSerializer::class)
package no.nav.personbruker.dittnav.eventhandler.statusoppdatering

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import no.nav.personbruker.dittnav.eventhandler.common.serializer.ZonedDateTimeSerializer
import java.time.ZonedDateTime

@Serializable
data class StatusoppdateringDTO(
    val produsent: String,
    val eventId: String,
    val eventTidspunkt: ZonedDateTime,
    val forstBehandlet: ZonedDateTime,
    val fodselsnummer: String,
    val grupperingsId: String,
    val link: String,
    val sikkerhetsnivaa: Int,
    val sistOppdatert: ZonedDateTime,
    val statusGlobal: String,
    val statusIntern: String?,
    val sakstema: String
)

