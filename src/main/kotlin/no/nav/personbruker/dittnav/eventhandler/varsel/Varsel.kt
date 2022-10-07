@file:UseSerializers(ZonedDateTimeSerializer::class)

package no.nav.personbruker.dittnav.eventhandler.varsel

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import no.nav.personbruker.dittnav.eventhandler.common.EventType
import no.nav.personbruker.dittnav.eventhandler.common.serializer.ZonedDateTimeSerializer
import java.time.ZonedDateTime

@Serializable
data class VarselDTO(
    val grupperingsId: String,
    val eventId: String,
    val eventTidspunkt: ZonedDateTime,
    val produsent: String,
    val sikkerhetsnivaa: Int,
    val sistOppdatert: ZonedDateTime,
    val tekst: String,
    val link: String,
    val aktiv: Boolean,
    val type: EventType,
    val forstBehandlet: ZonedDateTime
)

class Varsel(
    private val fodselsnummer: String,
    private val grupperingsId: String,
    private val eventId: String,
    private val eventTidspunkt: ZonedDateTime,
    private val produsent: String,
    private val sikkerhetsnivaa: Int,
    private val sistOppdatert: ZonedDateTime,
    private val tekst: String,
    private val link: String,
    private val aktiv: Boolean,
    private val type: EventType,
    private val forstBehandlet: ZonedDateTime
) {
    fun toEventDTO(): VarselDTO {
        return VarselDTO(
            grupperingsId = grupperingsId,
            eventId = eventId,
            eventTidspunkt = eventTidspunkt,
            produsent = produsent,
            sikkerhetsnivaa = sikkerhetsnivaa,
            sistOppdatert = sistOppdatert,
            tekst = tekst,
            link = link,
            aktiv = aktiv,
            type = type,
            forstBehandlet = forstBehandlet
        )
    }
}