package no.nav.personbruker.dittnav.eventhandler.beskjed

import Beskjed
import no.nav.personbruker.dittnav.eventhandler.common.database.getListFromSeparatedString
import no.nav.personbruker.dittnav.eventhandler.common.database.getNullableUtcTimeStamp
import no.nav.personbruker.dittnav.eventhandler.common.database.getUtcTimeStamp
import no.nav.personbruker.dittnav.eventhandler.common.database.mapList
import no.nav.personbruker.dittnav.eventhandler.done.DoneEventService
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.EksternVarslingInfo
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.EksternVarslingStatus
import no.nav.personbruker.dittnav.eventhandler.statistics.EventCountForProducer
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.ResultSet
import java.time.ZoneId
import java.time.ZonedDateTime

val log = LoggerFactory.getLogger(DoneEventService::class.java)
private val baseSelectQuery = """
    SELECT 
        beskjed.*,
        dok_status.status as doknotifikasjon_status,
        dok_status.kanaler as doknotifikasjon_kanaler
    FROM beskjed
        LEFT JOIN DOKNOTIFIKASJON_STATUS_BESKJED as dok_status on beskjed.eventId = dok_status.eventId
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

fun Connection.getBeskjedByIds(fodselsnummer: String, eventId: String): List<Beskjed> =
    prepareStatement("""$baseSelectQuery WHERE fodselsnummer = ? AND beskjed.eventId = ?""".trimMargin())
        .use {
            it.setString(1, fodselsnummer)
            it.setString(2, eventId)
            it.executeQuery().mapList {
                toBeskjed()
            }
        }

fun Connection.getAllGroupedBeskjedEventsByIds(
    fodselsnummer: String,
    grupperingsid: String,
    appnavn: String
): List<Beskjed> =
    prepareStatement("""$baseSelectQuery WHERE fodselsnummer = ? AND grupperingsId = ? AND appnavn = ?""".trimMargin())
        .use {
            it.setString(1, fodselsnummer)
            it.setString(2, grupperingsid)
            it.setString(3, appnavn)
            it.executeQuery().mapList {
                toBeskjed()
            }
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

fun ResultSet.toBeskjed(): Beskjed {
    return Beskjed(
        id = getInt("id"),
        fodselsnummer = getString("fodselsnummer"),
        grupperingsId = getString("grupperingsId"),
        eventId = getString("eventId"),
        eventTidspunkt = ZonedDateTime.ofInstant(
            getUtcTimeStamp("eventTidspunkt").toInstant(),
            ZoneId.of("Europe/Oslo")
        ),
        produsent = getString("appnavn") ?: "",
        systembruker = getString("systembruker"),
        namespace = getString("namespace"),
        appnavn = getString("appnavn"),
        sikkerhetsnivaa = getInt("sikkerhetsnivaa"),
        sistOppdatert = ZonedDateTime.ofInstant(getUtcTimeStamp("sistOppdatert").toInstant(), ZoneId.of("Europe/Oslo")),
        synligFremTil = getNullableZonedDateTime("synligFremTil"),
        tekst = getString("tekst"),
        link = getString("link"),
        aktiv = getBoolean("aktiv"),
        forstBehandlet = ZonedDateTime.ofInstant(
            getUtcTimeStamp("forstBehandlet").toInstant(),
            ZoneId.of("Europe/Oslo")
        ),
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

private fun ResultSet.getNullableZonedDateTime(label: String): ZonedDateTime? {
    return getNullableUtcTimeStamp(label)?.let { timestamp ->
        ZonedDateTime.ofInstant(
            timestamp.toInstant(),
            ZoneId.of("Europe/Oslo")
        )
    }
}

fun Connection.setBeskjedInaktiv(fodselsnummer: String, eventId: String): Int {
    log.info("Setter $eventId til inaktiv i db")
    return prepareStatement("""UPDATE beskjed  SET aktiv=FALSE WHERE fodselsnummer = ? AND eventId = ?""".trimMargin())
        .use {
            it.setString(1, fodselsnummer)
            it.setString(2, eventId)
            it.executeUpdate()
        }
}
