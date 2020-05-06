import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.ZonedDateTime

data class Beskjed(
        @JsonIgnore val id: Int,
        val uid: String,
        val fodselsnummer: String,
        val grupperingsId: String,
        val eventId: String,
        val eventTidspunkt: ZonedDateTime,
        val produsent: String,
        @JsonIgnore val systembruker: String,
        val sikkerhetsnivaa: Int,
        val sistOppdatert: ZonedDateTime,
        @JsonIgnore val synligFremTil: ZonedDateTime?,
        val tekst: String,
        val link: String,
        val aktiv: Boolean
) {
    override fun toString(): String {
        return "Beskjed(" +
                "id=$id, " +
                "uid=$uid, " +
                "fodselsnummer=***, " +
                "grupperingsId=$grupperingsId, " +
                "eventId=$eventId, " +
                "eventTidspunkt=$eventTidspunkt, " +
                "produsent=$produsent, " +
                "systembruker=***, " +
                "sikkerhetsnivaa=$sikkerhetsnivaa, " +
                "sistOppdatert=$sistOppdatert, " +
                "synligFremTil=$synligFremTil, " +
                "tekst=***, " +
                "link=***, " +
                "aktiv=$aktiv"
    }
}
