package no.nav.personbruker.dittnav.eventhandler.oppgave

import no.nav.personbruker.dittnav.eventhandler.common.database.convertIfUnlikelyDate
import no.nav.personbruker.dittnav.eventhandler.common.database.getUtcTimeStamp
import no.nav.personbruker.dittnav.eventhandler.common.database.mapList
import no.nav.personbruker.dittnav.eventhandler.common.statistics.EventCountForProducer
import java.sql.Connection
import java.sql.ResultSet
import java.time.ZoneId
import java.time.ZonedDateTime

fun Connection.getInaktivOppgaveForInnloggetBruker(fodselsnummer: String): List<Oppgave> =
        getOppgaveForInnloggetBruker(fodselsnummer, false)

fun Connection.getAktivOppgaveForInnloggetBruker(fodselsnummer: String): List<Oppgave> =
        getOppgaveForInnloggetBruker(fodselsnummer, true)

fun Connection.getAllOppgaveForInnloggetBruker(fodselsnummer: String): List<Oppgave> =
        prepareStatement("""SELECT 
            |oppgave.id,
            |oppgave.eventTidspunkt,
            |oppgave.fodselsnummer,
            |oppgave.eventId,
            |oppgave.grupperingsId,
            |oppgave.tekst,
            |oppgave.link,
            |oppgave.sikkerhetsnivaa,
            |oppgave.sistOppdatert,
            |oppgave.aktiv,
            |oppgave.systembruker,
            |oppgave.namespace,
            |oppgave.appnavn,
            |systembrukere.produsentnavn AS produsent
            |FROM (SELECT * FROM oppgave WHERE fodselsnummer = ?) AS oppgave
            |LEFT JOIN systembrukere ON oppgave.systembruker = systembrukere.systembruker""".trimMargin())
                .use {
                    it.setString(1, fodselsnummer)
                    it.executeQuery().mapList {
                        toOppgave()
                    }
                }

fun Connection.getAllGroupedOppgaveEventsByIds(fodselsnummer: String, grupperingsid: String, produsent: String): List<Oppgave> =
        prepareStatement("""SELECT
            |oppgave.id,
            |oppgave.eventTidspunkt,
            |oppgave.fodselsnummer,
            |oppgave.eventId,
            |oppgave.grupperingsId,
            |oppgave.tekst,
            |oppgave.link,
            |oppgave.sikkerhetsnivaa,
            |oppgave.sistOppdatert,
            |oppgave.aktiv,
            |oppgave.systembruker,
            |oppgave.namespace,
            |oppgave.appnavn,
            |systembrukere.produsentnavn AS produsent
            |FROM (SELECT * FROM oppgave WHERE fodselsnummer = ? AND grupperingsid = ?) AS oppgave
            |LEFT JOIN systembrukere ON oppgave.systembruker = systembrukere.systembruker WHERE systembrukere.produsentnavn = ?""".trimMargin())
                .use {
                    it.setString(1, fodselsnummer)
                    it.setString(2, grupperingsid)
                    it.setString(3, produsent)
                    it.executeQuery().mapList {
                        toOppgave()
                    }
                }

private fun ResultSet.toOppgave(): Oppgave {
    val rawEventTidspunkt = getUtcTimeStamp("eventTidspunkt")
    val verifiedEventTidspunkt = convertIfUnlikelyDate(rawEventTidspunkt)
    return Oppgave(
            id = getInt("id"),
            produsent = getString("produsent") ?: "",
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
            aktiv = getBoolean("aktiv")
    )
}

private fun Connection.getOppgaveForInnloggetBruker(fodselsnummer: String, aktiv: Boolean): List<Oppgave> =
        prepareStatement("""SELECT
            |oppgave.id,
            |oppgave.eventTidspunkt,
            |oppgave.fodselsnummer,
            |oppgave.eventId,
            |oppgave.grupperingsId,
            |oppgave.tekst,
            |oppgave.link,
            |oppgave.sikkerhetsnivaa,
            |oppgave.sistOppdatert,
            |oppgave.aktiv,
            |oppgave.systembruker,
            |oppgave.namespace,
            |oppgave.appnavn,
            |systembrukere.produsentnavn AS produsent
            |FROM (SELECT * FROM oppgave WHERE fodselsnummer = ? AND aktiv = ?) AS oppgave
            |LEFT JOIN systembrukere ON oppgave.systembruker = systembrukere.systembruker""".trimMargin())
                .use {
                    it.setString(1, fodselsnummer)
                    it.setBoolean(2, aktiv)
                    it.executeQuery().mapList {
                        toOppgave()
                    }
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
