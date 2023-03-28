package no.nav.personbruker.dittnav.eventhandler.innboks

import no.nav.personbruker.dittnav.eventhandler.common.database.*
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.EksternVarslingInfo
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.getEksternVarslingHistorikk
import no.nav.personbruker.dittnav.eventhandler.statistics.EventCountForProducer
import java.sql.Connection
import java.sql.ResultSet

private val baseSelectQuery = """
    SELECT 
        innboks.*,
        evs.kanaler as ekstern_varsling_kanaler,
        evs.eksternVarslingSendt as ekstern_varsling_sendt,
        evs.renotifikasjonSendt as ekstern_varsling_renotifikasjon,
        evs.historikk as ekstern_varsling_historikk
    FROM innboks
        LEFT JOIN ekstern_varsling_status_innboks as evs on innboks.eventId = evs.eventId
""".trimMargin()

fun Connection.getInaktivInnboksForFodselsnummer(fodselsnummer: String): List<Innboks> =
    getInnboksForFodselsnummerByAktiv(fodselsnummer, false)

fun Connection.getAktivInnboksForFodselsnummer(fodselsnummer: String): List<Innboks> =
    getInnboksForFodselsnummerByAktiv(fodselsnummer, true)

private fun Connection.getInnboksForFodselsnummerByAktiv(fodselsnummer: String, aktiv: Boolean): List<Innboks> =
    prepareStatement("""$baseSelectQuery WHERE fodselsnummer = ? AND aktiv = ?""".trimMargin())
        .use {
            it.setString(1, fodselsnummer)
            it.setBoolean(2, aktiv)
            it.executeQuery().mapList {
                toInnboks()
            }
        }

fun Connection.getAllInnboksForFodselsnummer(fodselsnummer: String): List<Innboks> =
    prepareStatement("""$baseSelectQuery WHERE fodselsnummer = ?""".trimMargin())
        .use {
            it.setString(1, fodselsnummer)
            it.executeQuery().mapList {
                toInnboks()
            }
        }

fun Connection.getAllGroupedInnboksEventsByIds(fodselsnummer: String, grupperingsid: String, appnavn: String): List<Innboks> =
    prepareStatement("""$baseSelectQuery WHERE fodselsnummer = ? AND grupperingsid = ? AND appnavn = ?""".trimMargin())
        .use {
            it.setString(1, fodselsnummer)
            it.setString(2, grupperingsid)
            it.setString(3, appnavn)
            it.executeQuery().mapList {
                toInnboks()
            }
        }

private fun ResultSet.toInnboks(): Innboks {
    val rawEventTidspunkt = getUtcTimeStamp("eventTidspunkt")
    val verifiedEventTidspunkt = convertIfUnlikelyDate(rawEventTidspunkt)
    return Innboks(
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

fun Connection.getAllGroupedInnboksEventsBySystemuser(): Map<String, Int> {
    return prepareStatement(
        "SELECT systembruker, COUNT(*) FROM innboks GROUP BY systembruker",
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

fun Connection.getAllGroupedInnboksEventsByProducer(): List<EventCountForProducer> {
    return prepareStatement("SELECT namespace, appnavn, COUNT(*) FROM innboks GROUP BY namespace, appnavn")
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
