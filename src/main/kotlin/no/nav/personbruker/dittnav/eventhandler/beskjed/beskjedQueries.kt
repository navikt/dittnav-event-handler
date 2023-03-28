package no.nav.personbruker.dittnav.eventhandler.beskjed

import no.nav.personbruker.dittnav.eventhandler.common.database.*
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.EksternVarslingInfo
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.getEksternVarslingHistorikk
import no.nav.personbruker.dittnav.eventhandler.statistics.EventCountForProducer
import java.sql.Connection
import java.sql.ResultSet

private val baseSelectQuery = """
    SELECT 
        beskjed.*,
        evs.kanaler as ekstern_varsling_kanaler,
        evs.eksternVarslingSendt as ekstern_varsling_sendt,
        evs.renotifikasjonSendt as ekstern_varsling_renotifikasjon,
        evs.historikk as ekstern_varsling_historikk
    FROM beskjed
        LEFT JOIN ekstern_varsling_status_beskjed as evs on beskjed.eventId = evs.eventId
""".trimMargin()

fun Connection.getInaktivBeskjedForFodselsnummer(fodselsnummer: String): List<Beskjed> =
    getBeskjedForFodselsnummerByAktiv(fodselsnummer, false)

fun Connection.getAktivBeskjedForFodselsnummer(fodselsnummer: String): List<Beskjed> =
    getBeskjedForFodselsnummerByAktiv(fodselsnummer, true)

private fun Connection.getBeskjedForFodselsnummerByAktiv(fodselsnummer: String, aktiv: Boolean): List<Beskjed> =
    prepareStatement("""$baseSelectQuery WHERE fodselsnummer = ? AND aktiv = ?""".trimMargin())
        .use {
            it.setString(1, fodselsnummer)
            it.setBoolean(2, aktiv)
            it.executeQuery().mapList {
                toBeskjed()
            }
        }

fun Connection.getAllBeskjedForFodselsnummer(fodselsnummer: String): List<Beskjed> =
    prepareStatement("""$baseSelectQuery WHERE fodselsnummer = ?""".trimMargin())
        .use {
            it.setString(1, fodselsnummer)
            it.executeQuery().mapList {
                toBeskjed()
            }
        }

private fun ResultSet.toBeskjed(): Beskjed {
    val rawEventTidspunkt = getUtcTimeStamp("eventTidspunkt")
    val verifiedEventTidspunkt = convertIfUnlikelyDate(rawEventTidspunkt)
    return Beskjed(
        fodselsnummer = getString("fodselsnummer"),
        grupperingsId = getString("grupperingsId"),
        eventId = getString("eventId"),
        eventTidspunkt = verifiedEventTidspunkt,
        forstBehandlet = getZonedDateTime("forstBehandlet"),
        produsent = getString("appnavn") ?: "",
        systembruker = getString("systembruker"),
        namespace = getString("namespace"),
        appnavn = getString("appnavn"),
        sikkerhetsnivaa = getInt("sikkerhetsnivaa"),
        sistOppdatert = getZonedDateTime("sistOppdatert"),
        tekst = getString("tekst"),
        link = getString("link"),
        aktiv = getBoolean("aktiv"),
        synligFremTil = getNullableZonedDateTime("synligFremTil"),
        fristUtløpt = getBoolean("frist_utløpt").let { if(wasNull()) null else it },
        eksternVarslingSendt = getBoolean("ekstern_varsling_sendt"),
        eksternVarslingKanaler = getListFromString("ekstern_varsling_kanaler"),
        eksternVarsling = getEksternVarslingInfo()
    )
}

private fun ResultSet.getEksternVarslingInfo(): EksternVarslingInfo? {

    if (!getBoolean("eksternVarsling")) {
        return null
    }

    val historikk = getEksternVarslingHistorikk("ekstern_varsling_historikk")

    return EksternVarslingInfo(
        prefererteKanaler = getListFromString("prefererteKanaler"),
        sendt = getBoolean("ekstern_varsling_sendt"),
        renotifikasjonSendt = getBoolean("ekstern_varsling_renotifikasjon"),
        sendteKanaler = getListFromString("ekstern_varsling_kanaler"),
        historikk = historikk
    )
}

fun Connection.getAllGroupedBeskjedEventsBySystemuser(): Map<String, Int> {
    return prepareStatement(
        "SELECT systembruker, COUNT(*) FROM beskjed GROUP BY systembruker",
        ResultSet.TYPE_SCROLL_INSENSITIVE,
        ResultSet.CONCUR_READ_ONLY
    )
        .use { statement ->
            val resultSet = statement.executeQuery()
            mutableMapOf<String, Int>().apply {
                while (resultSet.next()) {
                    put(resultSet.getString(1), resultSet.getInt(2))
                }
            }
        }
}

fun Connection.getAllGroupedBeskjedEventsByProducer(): List<EventCountForProducer> {
    return prepareStatement("SELECT namespace, appnavn, COUNT(*) FROM beskjed GROUP BY namespace, appnavn")
        .use { statement ->
            statement.executeQuery().mapList {
                EventCountForProducer(
                    namespace = getString(1),
                    appName = getString(2),
                    count = getInt(3),
                )
            }
        }
}
