package no.nav.personbruker.dittnav.eventhandler.statusoppdatering

import no.nav.personbruker.dittnav.eventhandler.common.database.getUtcTimeStamp
import no.nav.personbruker.dittnav.eventhandler.common.database.mapList
import no.nav.personbruker.dittnav.eventhandler.statistics.EventCountForProducer
import no.nav.tms.token.support.tokenx.validation.user.TokenXUser
import java.sql.Connection
import java.sql.ResultSet
import java.time.ZoneId
import java.time.ZonedDateTime

fun Connection.getAllGroupedStatusoppdateringEventsByIds(bruker: TokenXUser, grupperingsid: String, appnavn: String): List<Statusoppdatering> =
        prepareStatement("""SELECT 
            |id,
            |eventTidspunkt,
            |fodselsnummer,
            |eventId, 
            |grupperingsId,
            |link,
            |sikkerhetsnivaa,
            |sistOppdatert,
            |statusGlobal,
            |statusIntern,
            |sakstema,
            |systembruker,
            |namespace,
            |appnavn
            |FROM statusoppdatering WHERE fodselsnummer = ? AND grupperingsid = ? AND appnavn = ?""".trimMargin())
                .use {
                    it.setString(1, bruker.ident)
                    it.setString(2, grupperingsid)
                    it.setString(3, appnavn)
                    it.executeQuery().mapList {
                        toStatusoppdatering()
                    }
                }

fun ResultSet.toStatusoppdatering(): Statusoppdatering {
    return Statusoppdatering(
            id = getInt("id"),
            fodselsnummer = getString("fodselsnummer"),
            grupperingsId = getString("grupperingsId"),
            eventId = getString("eventId"),
            eventTidspunkt = ZonedDateTime.ofInstant(getUtcTimeStamp("eventTidspunkt").toInstant(), ZoneId.of("Europe/Oslo")),
            produsent = getString("appnavn") ?: "",
            systembruker = getString("systembruker"),
            sikkerhetsnivaa = getInt("sikkerhetsnivaa"),
            sistOppdatert = ZonedDateTime.ofInstant(getUtcTimeStamp("sistOppdatert").toInstant(), ZoneId.of("Europe/Oslo")),
            link = getString("link"),
            statusGlobal = getString("statusGlobal"),
            statusIntern = getString("statusIntern"),
            sakstema = getString("sakstema"),
            namespace = getString("namespace"),
            appnavn = getString("appnavn")
    )
}

fun Connection.getAllGroupedStatusoppdateringEventsBySystemuser(): Map<String, Int> {
    return prepareStatement("SELECT systembruker, COUNT(*) FROM statusoppdatering GROUP BY systembruker",
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

fun Connection.getAllGroupedStatusoppdateringEventsByProducer(): List<EventCountForProducer> {
    return prepareStatement("SELECT namespace, appnavn, COUNT(*) FROM statusoppdatering GROUP BY namespace, appnavn")
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
