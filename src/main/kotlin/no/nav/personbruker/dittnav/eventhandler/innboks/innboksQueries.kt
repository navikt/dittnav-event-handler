package no.nav.personbruker.dittnav.eventhandler.innboks

import no.nav.personbruker.dittnav.eventhandler.common.database.getUtcTimeStamp
import no.nav.personbruker.dittnav.eventhandler.common.database.mapList
import no.nav.personbruker.dittnav.eventhandler.statistics.EventCountForProducer
import java.sql.Connection
import java.sql.ResultSet
import java.time.ZoneId
import java.time.ZonedDateTime

fun Connection.getInaktivInnboksForInnloggetBruker(fodselsnummer: String): List<Innboks> =
        getInnboksForInnloggetBruker(fodselsnummer, false)

fun Connection.getAktivInnboksForInnloggetBruker(fodselsnummer: String): List<Innboks> =
        getInnboksForInnloggetBruker(fodselsnummer, true)

fun Connection.getAllInnboksForInnloggetBruker(fodselsnummer: String): List<Innboks> =
        prepareStatement("""SELECT
            |innboks.id,
            |innboks.eventTidspunkt,
            |innboks.fodselsnummer,
            |innboks.eventId,
            |innboks.grupperingsId,
            |innboks.tekst,
            |innboks.link,
            |innboks.sikkerhetsnivaa,
            |innboks.sistOppdatert,
            |innboks.aktiv,
            |innboks.systembruker,
            |innboks.namespace,
            |innboks.appnavn,
            |systembrukere.produsentnavn AS produsent
            |FROM (SELECT * FROM innboks WHERE fodselsnummer = ?) AS innboks
            |LEFT JOIN systembrukere ON innboks.systembruker = systembrukere.systembruker""".trimMargin())
                .use {
                    it.setString(1, fodselsnummer)
                    it.executeQuery().mapList {
                        toInnboks()
                    }
                }

fun Connection.getAllGroupedInnboksEventsByIds(fodselsnummer: String, grupperingsid: String, produsent: String): List<Innboks> =
        prepareStatement("""SELECT
            |innboks.id,
            |innboks.eventTidspunkt,
            |innboks.fodselsnummer,
            |innboks.eventId,
            |innboks.grupperingsId,
            |innboks.tekst,
            |innboks.link,
            |innboks.sikkerhetsnivaa,
            |innboks.sistOppdatert,
            |innboks.aktiv,
            |innboks.systembruker,
            |innboks.namespace,
            |innboks.appnavn,
            |systembrukere.produsentnavn AS produsent
            |FROM (SELECT * FROM innboks WHERE fodselsnummer = ? AND grupperingsid = ?) AS innboks
            |LEFT JOIN systembrukere ON innboks.systembruker = systembrukere.systembruker WHERE systembrukere.produsentnavn = ?""".trimMargin())
                .use {
                    it.setString(1, fodselsnummer)
                    it.setString(2, grupperingsid)
                    it.setString(3, produsent)
                    it.executeQuery().mapList {
                        toInnboks()
                    }
                }

private fun ResultSet.toInnboks(): Innboks {
    return Innboks(
            id = getInt("id"),
            produsent = getString("produsent") ?: "",
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
            aktiv = getBoolean("aktiv")
    )
}

private fun Connection.getInnboksForInnloggetBruker(fodselsnummer: String, aktiv: Boolean): List<Innboks> =
        prepareStatement("""SELECT
            |innboks.id,
            |innboks.eventTidspunkt,
            |innboks.fodselsnummer,
            |innboks.eventId,
            |innboks.grupperingsId,
            |innboks.tekst,
            |innboks.link,
            |innboks.sikkerhetsnivaa,
            |innboks.sistOppdatert,
            |innboks.aktiv,
            |innboks.systembruker,
            |innboks.namespace,
            |innboks.appnavn,
            |systembrukere.produsentnavn AS produsent
            |FROM (SELECT * FROM innboks WHERE fodselsnummer = ? AND aktiv = ?) AS innboks
            |LEFT JOIN systembrukere ON innboks.systembruker = systembrukere.systembruker""".trimMargin())
                .use {
                    it.setString(1, fodselsnummer)
                    it.setBoolean(2, aktiv)
                    it.executeQuery().mapList {
                        toInnboks()
                    }
                }

fun Connection.getAllGroupedInnboksEventsBySystemuser(): Map<String, Int> {
    return prepareStatement("SELECT systembruker, COUNT(*) FROM innboks GROUP BY systembruker",
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
