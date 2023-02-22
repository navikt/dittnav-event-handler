package no.nav.personbruker.dittnav.eventhandler.varsel

import no.nav.personbruker.dittnav.eventhandler.common.VarselType
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.common.database.getUtcTimeStamp
import no.nav.personbruker.dittnav.eventhandler.common.database.mapList
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.EksternVarslingStatus
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.EksternVarslingStatus.FERDIGSTILT
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
                ds.kanaler as doknot_kanaler,
                ds.status as doknot_status
            FROM $table as v
            LEFT JOIN doknotifikasjon_status_$table ds ON v.eventId=ds.eventId
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
        eksternVarslingSendt = EksternVarslingStatus[getString("doknot_status")] == FERDIGSTILT,
        eksternVarslingKanaler = getString("doknot_kanaler").let { if (wasNull()) emptyList() else it.split(",") }
    )
}

