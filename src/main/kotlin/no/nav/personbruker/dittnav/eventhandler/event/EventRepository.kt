package no.nav.personbruker.dittnav.eventhandler.event

import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.common.database.getUtcTimeStamp
import no.nav.personbruker.dittnav.eventhandler.common.database.mapList
import java.time.ZoneId
import java.time.ZonedDateTime

class EventRepository(private val database: Database) {
    suspend fun getInactiveEvents(fodselsnummer: String): List<Event> {
        return database.queryWithExceptionTranslation {
            prepareStatement(
                    """
                    ${eventQuery(fodselsnummer, "beskjed")}
                    UNION ALL
                    ${eventQuery(fodselsnummer, "oppgave")}
                    UNION ALL 
                    ${eventQuery(fodselsnummer, "innboks")}
                    """.trimIndent()).executeQuery().mapList {
                Event(
                        fodselsnummer = getString("fodselsnummer"),
                        grupperingsId = getString("grupperingsId"),
                        eventId = getString("eventId"),
                        eventTidspunkt = ZonedDateTime.ofInstant(getUtcTimeStamp("eventTidspunkt").toInstant(), ZoneId.of("Europe/Oslo")),
                        produsent = getString("appnavn") ?: "",
                        sikkerhetsnivaa = getInt("sikkerhetsnivaa"),
                        sistOppdatert = ZonedDateTime.ofInstant(getUtcTimeStamp("sistOppdatert").toInstant(), ZoneId.of("Europe/Oslo")),
                        tekst = getString("tekst"),
                        link = getString("link"),
                        aktiv = getBoolean("aktiv"),
                        type = EventType.fromOriginalType(getString("type"))
                )
            }
        }
    }

    private fun eventQuery(fodselsnummer: String, table: String): String {
        return """
                SELECT
                    eventTidspunkt,
                    fodselsnummer,
                    eventId,
                    grupperingsId,
                    tekst,
                    appnavn,
                    link,
                    sikkerhetsnivaa,
                    sistOppdatert,
                    aktiv,
                    '$table' as type 
                FROM $table
                WHERE fodselsnummer = '$fodselsnummer'
                AND aktiv = false
                """
    }
}