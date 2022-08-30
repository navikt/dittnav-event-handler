package no.nav.personbruker.dittnav.eventhandler.done


import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

data class Done(
    val eventId: String,
    val systembruker: String,
    val namespace: String,
    val appnavn: String,
    val fodselsnummer: String,
    val grupperingsId: String,
    val eventTidspunkt: ZonedDateTime,
    val forstBehandlet: ZonedDateTime
) {
    override fun toString(): String {
        return "Done(" +
            "eventId=$eventId, " +
            "systembruker=$systembruker, " +
            "namespace=$namespace, " +
            "appnavn=$appnavn, " +
            "fodselsnummer=***, " +
            "grupperingsId=$grupperingsId, " +
            "eventTidspunkt=$eventTidspunkt" +
            "forstBehandlet=$forstBehandlet"
    }
}


@Serializable
data class DoneDTO(
    val eventId: String
)