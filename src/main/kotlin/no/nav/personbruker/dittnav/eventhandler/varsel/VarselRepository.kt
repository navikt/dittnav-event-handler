package no.nav.personbruker.dittnav.eventhandler.varsel

import no.nav.personbruker.dittnav.eventhandler.common.VarselType
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.common.database.getListFromString
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
                v.eventTidspunkt,
                v.fodselsnummer,
                v.eventId,
                v.grupperingsId,
                v.tekst,
                v.appnavn,
                v.link,
                v.sikkerhetsnivaa,
                v.sistOppdatert,
                v.aktiv,
                v.forstBehandlet,
                v.frist_utløpt,
                '$table' as type,
                evs.kanaler as ekstern_varsling_kanaler,
                evs.eksternVarslingSendt as ekstern_varsling_sendt
            FROM $table as v
            LEFT JOIN ekstern_varsling_status_$table evs ON v.eventId=evs.eventId
            WHERE fodselsnummer = ? AND aktiv = $active
        """
    }

    private fun ResultSet.toVarsel() = Varsel(
        eventId = getString("eventId"),
        sikkerhetsnivaa = getInt("sikkerhetsnivaa"),
        sistOppdatert = ZonedDateTime.ofInstant(
            getUtcTimeStamp("sistOppdatert").toInstant(),
            ZoneId.of("Europe/Oslo")
        ),
        tekst = getString("tekst"),
        link = getString("link"),
        aktiv = getBoolean("aktiv"),
        type = VarselType.fromOriginalType(getString("type")),
        forstBehandlet = ZonedDateTime.ofInstant(
            getUtcTimeStamp("forstBehandlet").toInstant(),
            ZoneId.of("Europe/Oslo")
        ),
        fristUtløpt = getBoolean("frist_utløpt").let { if (wasNull()) null else it },
        eksternVarslingSendt = getBoolean("ekstern_varsling_sendt"),
        eksternVarslingKanaler = getListFromString("ekstern_varsling_kanaler")
    )
}

