package no.nav.personbruker.dittnav.eventhandler.beskjed

import Beskjed
import no.nav.personbruker.dittnav.eventhandler.common.database.getNullableUtcTimeStamp
import no.nav.personbruker.dittnav.eventhandler.common.database.getUtcTimeStamp
import no.nav.personbruker.dittnav.eventhandler.common.database.mapList
import no.nav.personbruker.dittnav.eventhandler.statistics.EventCountForProducer
import java.sql.Connection
import java.sql.ResultSet
import java.time.ZoneId
import java.time.ZonedDateTime

fun Connection.getInaktivBeskjedForInnloggetBruker(fodselsnummer: String): List<Beskjed> =
        getBeskjedForInnloggetBruker(fodselsnummer, false)

fun Connection.getAktivBeskjedForInnloggetBruker(fodselsnummer: String): List<Beskjed> =
        getBeskjedForInnloggetBruker(fodselsnummer, true)

fun Connection.getAllBeskjedForInnloggetBruker(fodselsnummer: String): List<Beskjed> =
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
            |synligFremTil,
            |aktiv,
            |systembruker,
            |namespace,
            |appnavn
            |FROM beskjed WHERE fodselsnummer = ?""".trimMargin())
                .use {
                    it.setString(1, fodselsnummer)
                    it.executeQuery().mapList {
                        toBeskjed()
                    }
                }

fun Connection.getBeskjedByIds(fodselsnummer: String, eventId: String): List<Beskjed> =
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
            |synligFremTil,
            |aktiv,
            |systembruker,
            |namespace,
            |appnavn
            |FROM beskjed WHERE fodselsnummer = ? AND eventId = ?""".trimMargin())
                .use {
                    it.setString(1, fodselsnummer)
                    it.setString(2, eventId)
                    it.executeQuery().mapList {
                        toBeskjed()
                    }
                }

fun Connection.getAllGroupedBeskjedEventsByIds(fodselsnummer: String, grupperingsid: String, appnavn: String): List<Beskjed> =
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
            |synligFremTil,
            |aktiv,
            |systembruker,
            |namespace,
            |appnavn
            |FROM beskjed WHERE fodselsnummer = ? AND grupperingsId = ? AND appnavn = ?""".trimMargin())
                .use {
                    it.setString(1, fodselsnummer)
                    it.setString(2, grupperingsid)
                    it.setString(3, appnavn)
                    it.executeQuery().mapList {
                        toBeskjed()
                    }
                }

fun Connection.getAllGroupedBeskjedEventsBySystemuser(): Map<String, Int> {
    return prepareStatement("SELECT systembruker, COUNT(*) FROM beskjed GROUP BY systembruker",
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
            eventTidspunkt = ZonedDateTime.ofInstant(getUtcTimeStamp("eventTidspunkt").toInstant(), ZoneId.of("Europe/Oslo")),
            produsent = getString("appnavn") ?: "",
            systembruker = getString("systembruker"),
            namespace = getString("namespace"),
            appnavn = getString("appnavn"),
            sikkerhetsnivaa = getInt("sikkerhetsnivaa"),
            sistOppdatert = ZonedDateTime.ofInstant(getUtcTimeStamp("sistOppdatert").toInstant(), ZoneId.of("Europe/Oslo")),
            synligFremTil = getNullableZonedDateTime("synligFremTil"),
            tekst = getString("tekst"),
            link = getString("link"),
            aktiv = getBoolean("aktiv")
    )
}

private fun Connection.getBeskjedForInnloggetBruker(fodselsnummer: String, aktiv: Boolean): List<Beskjed> =
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
            |synligFremTil,
            |aktiv,
            |systembruker,
            |namespace,
            |appnavn
            |FROM beskjed WHERE fodselsnummer = ? AND aktiv = ?""".trimMargin())
                .use {
                    it.setString(1, fodselsnummer)
                    it.setBoolean(2, aktiv)
                    it.executeQuery().mapList {
                        toBeskjed()
                    }
                }

private fun ResultSet.getNullableZonedDateTime(label: String): ZonedDateTime? {
    return getNullableUtcTimeStamp(label)?.let { timestamp -> ZonedDateTime.ofInstant(timestamp.toInstant(), ZoneId.of("Europe/Oslo")) }
}
