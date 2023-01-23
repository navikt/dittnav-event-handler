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
    val eksternVarslingKanaler: List<String>
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
    private val type: VarselType,
    private val forstBehandlet: ZonedDateTime,
    private val fristUtløpt: Boolean?,
    val eksternVarslingSendt: Boolean,
    val eksternVarslingKanaler: List<String>
) {
    fun toVarselDTO() = toVarselDTO(tekst, link)
    fun toVarselDTO(innloggingsnivå: Int) =
        toVarselDTO(tekst.withSikkerhetsnivaa(innloggingsnivå), link.withSikkerhetsnivaa(innloggingsnivå))

    private fun toVarselDTO(tekst: String?, link: String?) =
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
            eksternVarslingKanaler = eksternVarslingKanaler
        )


    private fun forInnlogingsnivå(innloggingsnivå: Int, text: String) = when {
        innloggingsnivå < sikkerhetsnivaa -> null
        innloggingsnivå >= sikkerhetsnivaa -> text
        else -> {
            throw IllegalArgumentException("Kunne ikke avgjøre tekst for innloggingsnivå for varsel $eventId med innlogingsnivå $innloggingsnivå")
        }
    }

    private fun String.withSikkerhetsnivaa(innloggingsnivå: Int): String? =
        if (innloggingsnivå >= sikkerhetsnivaa) {
            this
        } else {
            null
        }
}

