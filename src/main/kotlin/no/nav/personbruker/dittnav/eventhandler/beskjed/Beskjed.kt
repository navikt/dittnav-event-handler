import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.EksternVarslingInfo
import java.time.ZonedDateTime

data class Beskjed(
        val id: Int,
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
        val eksternVarslingInfo: EksternVarslingInfo
) {
    override fun toString(): String {
        return "Beskjed(" +
                "id=$id, " +
                "fodselsnummer=***, " +
                "grupperingsId=$grupperingsId, " +
                "eventId=$eventId, " +
                "eventTidspunkt=$eventTidspunkt, " +
                "forstBehandlet=$forstBehandlet, " +
                "produsent=$produsent, " +
                "systembruker=$systembruker, " +
                "namespace=$namespace, " +
                "appnavn=$appnavn, " +
                "sikkerhetsnivaa=$sikkerhetsnivaa, " +
                "sistOppdatert=$sistOppdatert, " +
                "synligFremTil=$synligFremTil, " +
                "tekst=***, " +
                "link=***, " +
                "aktiv=$aktiv"
    }
}
