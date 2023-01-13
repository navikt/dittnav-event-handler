package no.nav.personbruker.dittnav.eventhandler.varsel

import no.nav.personbruker.dittnav.eventhandler.common.EventType
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.common.database.getUtcTimeStamp
import no.nav.personbruker.dittnav.eventhandler.common.database.mapList
import java.sql.ResultSet
import java.time.ZoneId
import java.time.ZonedDateTime

class VarselRepository(private val database: Database) {

    suspend fun getInactiveVarsel(fodselsnummer: String): List<Varsel> {
        return getVarsel(fodselsnummer, false)
    }

    suspend fun getActiveVarsel(fodselsnummer: String): List<Varsel> {
        return getVarsel(fodselsnummer, true)
    }

    private suspend fun getVarsel(fodselsnummer: String, active: Boolean): List<Varsel> {
        return database.queryWithExceptionTranslation {
            prepareStatement(
                """
                ${varselQuery("beskjed", active)}
                UNION ALL
                ${varselQuery("oppgave", active)}
                UNION ALL 
                ${varselQuery("innboks", active)}
                """.trimIndent()
            ).also {
                it.setString(1, fodselsnummer)
                it.setString(2, fodselsnummer)
                it.setString(3, fodselsnummer)
            }.executeQuery().mapList { toVarsel() }
        }
    }

    private fun varselQuery(table: String, active: Boolean): String {
        return """
            SELECT
                eventTidspunkt,
                fodselsnummer,
                eventId,
                grupperingsId,
                tekst,
                appnavn,
                link,
                sikkerhetsnivaa,
                sistOppdatert,
                aktiv,
                forstBehandlet,
                frist_utløpt,
                '$table' as type 
            FROM $table
            WHERE fodselsnummer = ?
            AND aktiv = $active
        """
    }

    private fun ResultSet.toVarsel() = Varsel(
        fodselsnummer = getString("fodselsnummer"),
        grupperingsId = getString("grupperingsId"),
        eventId = getString("eventId"),
        eventTidspunkt = ZonedDateTime.ofInstant(
            getUtcTimeStamp("eventTidspunkt").toInstant(),
            ZoneId.of("Europe/Oslo")
        ),
        produsent = getString("appnavn") ?: "",
        sikkerhetsnivaa = getInt("sikkerhetsnivaa"),
        sistOppdatert = ZonedDateTime.ofInstant(
            getUtcTimeStamp("sistOppdatert").toInstant(),
            ZoneId.of("Europe/Oslo")
        ),
        tekst = getString("tekst"),
        link = getString("link"),
        aktiv = getBoolean("aktiv"),
        type = EventType.fromOriginalType(getString("type")),
        forstBehandlet = ZonedDateTime.ofInstant(
            getUtcTimeStamp("forstBehandlet").toInstant(),
            ZoneId.of("Europe/Oslo")
        ),
        fristUtløpt = getBoolean("frist_utløpt").let { if(wasNull()) null else it}
    )
}

