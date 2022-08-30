package no.nav.personbruker.dittnav.eventhandler.innboks

import no.nav.personbruker.dittnav.eventhandler.common.database.getListFromSeparatedString
import no.nav.personbruker.dittnav.eventhandler.common.database.getUtcTimeStamp
import no.nav.personbruker.dittnav.eventhandler.common.database.mapList
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.EksternVarslingInfo
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.EksternVarslingStatus
import no.nav.personbruker.dittnav.eventhandler.statistics.EventCountForProducer
import java.sql.Connection
import java.sql.ResultSet
import java.time.ZoneId
import java.time.ZonedDateTime

private val baseSelectQuery = """
    SELECT 
        innboks.*,
        dok_status.status as doknotifikasjon_status,
        dok_status.kanaler as doknotifikasjon_kanaler
    FROM innboks
        LEFT JOIN DOKNOTIFIKASJON_STATUS_INNBOKS as dok_status on innboks.eventId = dok_status.eventId
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
    return Innboks(
        id = getInt("id"),
        produsent = getString("appnavn") ?: "",
        systembruker = getString("systembruker"),
        namespace = getString("namespace"),
        appnavn = getString("appnavn"),
        eventTidspunkt = ZonedDateTime.ofInstant(getUtcTimeStamp("eventTidspunkt").toInstant(), ZoneId.of("Europe/Oslo")),
        fodselsnummer = getString("fodselsnummer"),
        eventId = getString("eventId"),
        grupperingsId = getString("grupperingsId"),
        tekst = getString("tekst"),
        link = getString("link"),
        sikkerhetsnivaa = getInt("sikkerhetsnivaa"),
        sistOppdatert = ZonedDateTime.ofInstant(getUtcTimeStamp("sistOppdatert").toInstant(), ZoneId.of("Europe/Oslo")),
        aktiv = getBoolean("aktiv"),
        forstBehandlet = ZonedDateTime.ofInstant(getUtcTimeStamp("forstBehandlet").toInstant(), ZoneId.of("Europe/Oslo")),
        eksternVarslingInfo = toEksternVarslingInfo()
    )
}

private fun ResultSet.toEksternVarslingInfo(): EksternVarslingInfo {
    val eksternVarslingSendt = getString("doknotifikasjon_status") == EksternVarslingStatus.OVERSENDT.name

    return EksternVarslingInfo(
        bestilt = getBoolean("eksternVarsling"),
        prefererteKanaler = getListFromSeparatedString("prefererteKanaler"),
        sendt = eksternVarslingSendt,
        sendteKanaler = getListFromSeparatedString("doknotifikasjon_kanaler")
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
