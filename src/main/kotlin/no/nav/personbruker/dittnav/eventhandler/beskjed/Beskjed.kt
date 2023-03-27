@file:UseSerializers(ZonedDateTimeSerializer::class)
package no.nav.personbruker.dittnav.eventhandler.beskjed

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import no.nav.personbruker.dittnav.eventhandler.common.serializer.ZonedDateTimeSerializer
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.EksternVarslingInfo
import java.time.ZonedDateTime

@Serializable
data class Beskjed(
    val fodselsnummer: String,
    val grupperingsId: String,
    val eventId: String,
    val eventTidspunkt: ZonedDateTime,
    val forstBehandlet: ZonedDateTime,
    val produsent: String,
    val systembruker: String,
    val namespace: String,
    val appnavn: String,
    val sikkerhetsnivaa: Int,
    val sistOppdatert: ZonedDateTime,
    val synligFremTil: ZonedDateTime?,
    val tekst: String,
    val link: String,
    val aktiv: Boolean,
    val eksternVarslingSendt: Boolean,
    val eksternVarslingKanaler: List<String>,
    val eksternVarsling: EksternVarslingInfo?,
    val fristUtløpt: Boolean?
)
