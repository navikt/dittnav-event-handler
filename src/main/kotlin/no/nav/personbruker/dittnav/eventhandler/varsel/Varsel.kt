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
    fun toVarselDTO(innloggingsnivå: Int): VarselDTO {
        return VarselDTO(
            eventId = eventId,
            sikkerhetsnivaa = sikkerhetsnivaa,
            sistOppdatert = sistOppdatert,
            tekst = innloggingsnivå.let { if (innloggingsnivå < sikkerhetsnivaa) null else tekst },
            link = innloggingsnivå.let { if (innloggingsnivå < sikkerhetsnivaa) null else link },
            aktiv = aktiv,
            type = type,
            forstBehandlet = forstBehandlet,
            fristUtløpt = fristUtløpt,
            eksternVarslingSendt=eksternVarslingSendt,
            eksternVarslingKanaler = eksternVarslingKanaler
        )
    }
}
