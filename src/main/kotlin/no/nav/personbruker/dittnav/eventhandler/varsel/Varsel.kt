@file:UseSerializers(ZonedDateTimeSerializer::class)

package no.nav.personbruker.dittnav.eventhandler.varsel

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import no.nav.personbruker.dittnav.eventhandler.common.VarselType
import no.nav.personbruker.dittnav.eventhandler.common.serializer.ZonedDateTimeSerializer
import java.time.ZonedDateTime


@Serializable
data class VarselDTO(
    val eventId: String,
    val sikkerhetsnivaa: Int,
    val sistOppdatert: ZonedDateTime,
    val tekst: String?,
    val link: String?,
    val aktiv: Boolean,
    val type: VarselType,
    val forstBehandlet: ZonedDateTime,
    val fristUtløpt: Boolean?,
    val eksternVarslingSendt: Boolean,
    val eksternVarslingKanaler: List<String>,
    val isMasked: Boolean
)

class Varsel(
    val eventId: String,
    val sikkerhetsnivaa: Int,
    val sistOppdatert: ZonedDateTime,
    val tekst: String,
    val link: String,
    val aktiv: Boolean,
    val type: VarselType,
    val forstBehandlet: ZonedDateTime,
    val fristUtløpt: Boolean?,
    val eksternVarslingSendt: Boolean,
    val eksternVarslingKanaler: List<String>
) {
    fun toVarselDTO() = toVarselDTO(tekst, link,false)
    fun toVarselDTO(innloggingsnivå: Int) =
        toVarselDTO(
            tekst.withSikkerhetsnivaa(innloggingsnivå),
            link.withSikkerhetsnivaa(innloggingsnivå),
            isMasked = innloggingsnivå < sikkerhetsnivaa
        )

    private fun toVarselDTO(tekst: String?, link: String?, isMasked: Boolean) =
        VarselDTO(
            eventId = eventId,
            sikkerhetsnivaa = sikkerhetsnivaa,
            sistOppdatert = sistOppdatert,
            tekst = tekst,
            link = link,
            aktiv = aktiv,
            type = type,
            forstBehandlet = forstBehandlet,
            fristUtløpt = fristUtløpt,
            eksternVarslingSendt = eksternVarslingSendt,
            eksternVarslingKanaler = eksternVarslingKanaler,
            isMasked = isMasked
        )

    private fun String.withSikkerhetsnivaa(innloggingsnivå: Int): String? =
        if (innloggingsnivå >= sikkerhetsnivaa) {
            this
        } else {
            null
        }
}

