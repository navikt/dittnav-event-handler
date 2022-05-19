package no.nav.personbruker.dittnav.eventhandler.oppgave

import no.nav.personbruker.dittnav.eventhandler.common.database.convertIfUnlikelyDate
import no.nav.personbruker.dittnav.eventhandler.common.database.getUtcTimeStamp
import no.nav.personbruker.dittnav.eventhandler.common.database.mapList
import no.nav.personbruker.dittnav.eventhandler.statistics.EventCountForProducer
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Types
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

fun Connection.getInaktivOppgaveForFodselsnummer(fodselsnummer: String): List<Oppgave> =
        getOppgaveForFodselsnummerByAktiv(fodselsnummer, false)

fun Connection.getAktivOppgaveForFodselsnummer(fodselsnummer: String): List<Oppgave> =
        getOppgaveForFodselsnummerByAktiv(fodselsnummer, true)

private fun Connection.getOppgaveForFodselsnummerByAktiv(fodselsnummer: String, aktiv: Boolean): List<Oppgave> =
    prepareStatement("""SELECT
            |id,
            |eventTidspunkt,
            |fodselsnummer,
            |eventId,
            |grupperingsId,
            |tekst,
            |link,
            |sikkerhetsnivaa,
            |sistOppdatert,
            |aktiv,
            |systembruker,
            |namespace,
            |appnavn,
            |forstBehandlet
            |FROM oppgave WHERE fodselsnummer = ? AND aktiv = ?""".trimMargin())
        .use {
            it.setString(1, fodselsnummer)
            it.setBoolean(2, aktiv)
            it.executeQuery().mapList {
                toOppgave()
            }
        }

fun Connection.getAllOppgaveForFodselsnummer(fodselsnummer: String): List<Oppgave> =
        prepareStatement("""SELECT 
            |id,
            |eventTidspunkt,
            |fodselsnummer,
            |eventId,
            |grupperingsId,
            |tekst,
            |link,
            |sikkerhetsnivaa,
            |sistOppdatert,
            |aktiv,
            |systembruker,
            |namespace,
            |appnavn,
            |forstBehandlet
            |FROM oppgave WHERE fodselsnummer = ?""".trimMargin())
                .use {
                    it.setString(1, fodselsnummer)
                    it.executeQuery().mapList {
                        toOppgave()
                    }
                }

fun Connection.getAllGroupedOppgaveEventsByIds(fodselsnummer: String, grupperingsid: String, appnavn: String): List<Oppgave> =
        prepareStatement("""SELECT
            |id,
            |eventTidspunkt,
            |fodselsnummer,
            |eventId,
            |grupperingsId,
            |tekst,
            |link,
            |sikkerhetsnivaa,
            |sistOppdatert,
            |aktiv,
            |systembruker,
            |namespace,
            |appnavn,
            |forstBehandlet
            |FROM oppgave WHERE fodselsnummer = ? AND grupperingsid = ? AND appnavn = ?""".trimMargin())
                .use {
                    it.setString(1, fodselsnummer)
                    it.setString(2, grupperingsid)
                    it.setString(3, appnavn)
                    it.executeQuery().mapList {
                        toOppgave()
                    }
                }

private fun ResultSet.toOppgave(): Oppgave {
    val rawEventTidspunkt = getUtcTimeStamp("eventTidspunkt")
    val verifiedEventTidspunkt = convertIfUnlikelyDate(rawEventTidspunkt)
    return Oppgave(
            id = getInt("id"),
            produsent = getString("appnavn") ?: "",
            systembruker = getString("systembruker"),
            namespace = getString("namespace"),
            appnavn = getString("appnavn"),
            eventTidspunkt = verifiedEventTidspunkt,
            fodselsnummer = getString("fodselsnummer"),
            eventId = getString("eventId"),
            grupperingsId = getString("grupperingsId"),
            tekst = getString("tekst"),
            link = getString("link"),
            sikkerhetsnivaa = getInt("sikkerhetsnivaa"),
            sistOppdatert = ZonedDateTime.ofInstant(getUtcTimeStamp("sistOppdatert").toInstant(), ZoneId.of("Europe/Oslo")),
            aktiv = getBoolean("aktiv"),
            forstBehandlet = ZonedDateTime.ofInstant(getUtcTimeStamp("forstBehandlet").toInstant(), ZoneId.of("Europe/Oslo"))
    )
}

fun Connection.getAllGroupedOppgaveEventsBySystemuser(): Map<String, Int> {
    return prepareStatement("SELECT systembruker, COUNT(*) FROM oppgave GROUP BY systembruker",
            ResultSet.TYPE_SCROLL_INSENSITIVE,
            ResultSet.CONCUR_READ_ONLY)
            .use { statement ->
                val resultSet = statement.executeQuery()
                mutableMapOf<String, Int>().apply {
                    while (resultSet.next()) {
                        put(resultSet.getString(1), resultSet.getInt(2))
                    }
                }
            }
}

fun Connection.getAllGroupedOppgaveEventsByProducer(): List<EventCountForProducer> {
    return prepareStatement("SELECT namespace, appnavn, COUNT(*) FROM oppgave GROUP BY namespace, appnavn")
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

fun Connection.getRecentInaktivOppgaveForFodselsnummer(fodselsnummer: String, fromDate: LocalDate): List<Oppgave> =
    getRecentOppgaveForFodselsnummerByAktiv(fodselsnummer, false, fromDate)

fun Connection.getRecentAktivOppgaveForFodselsnummer(fodselsnummer: String, fromDate: LocalDate): List<Oppgave> =
    getRecentOppgaveForFodselsnummerByAktiv(fodselsnummer, true, fromDate)

private fun Connection.getRecentOppgaveForFodselsnummerByAktiv(fodselsnummer: String, aktiv: Boolean, fromDate: LocalDate): List<Oppgave> =
    prepareStatement("""SELECT
            |id,
            |eventTidspunkt,
            |fodselsnummer,
            |eventId,
            |grupperingsId,
            |tekst,
            |link,
            |sikkerhetsnivaa,
            |sistOppdatert,
            |aktiv,
            |systembruker,
            |namespace,
            |appnavn,
            |forstBehandlet
            |FROM oppgave WHERE fodselsnummer = ? AND aktiv = ? AND forstBehandlet > ?""".trimMargin())
        .use {
            it.setString(1, fodselsnummer)
            it.setBoolean(2, aktiv)
            it.setObject(3, fromDate, Types.TIMESTAMP)
            it.executeQuery().mapList {
                toOppgave()
            }
        }

fun Connection.getAllRecentOppgaveForFodselsnummer(fodselsnummer: String, fromDate: LocalDate): List<Oppgave> =
    prepareStatement("""SELECT 
            |id,
            |eventTidspunkt,
            |fodselsnummer,
            |eventId,
            |grupperingsId,
            |tekst,
            |link,
            |sikkerhetsnivaa,
            |sistOppdatert,
            |aktiv,
            |systembruker,
            |namespace,
            |appnavn,
            |forstBehandlet
            |FROM oppgave WHERE fodselsnummer = ?
            |AND forstBehandlet > ?""".trimMargin())
        .use {
            it.setString(1, fodselsnummer)
            it.setObject(2, fromDate, Types.TIMESTAMP)
            it.executeQuery().mapList {
                toOppgave()
            }
        }
