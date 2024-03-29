@file:UseSerializers(ZonedDateTimeSerializer::class)
package no.nav.personbruker.dittnav.eventhandler.eksternvarsling

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import no.nav.personbruker.dittnav.eventhandler.common.serializer.ZonedDateTimeSerializer
import java.time.ZonedDateTime

@Serializable
data class EksternVarsling(
    val sendt: Boolean,
    val renotifikasjonSendt: Boolean,
    val prefererteKanaler: List<String>,
    val sendteKanaler: List<String>,
    val historikk: List<EksternVarslingHistorikkEntry>
)

@Serializable
data class EksternVarslingHistorikkEntry(
    val melding: String,
    val status: String,
    val distribusjonsId: Long? = null,
    val kanal: String? = null,
    val renotifikasjon: Boolean? = null,
    val tidspunkt: ZonedDateTime
)
