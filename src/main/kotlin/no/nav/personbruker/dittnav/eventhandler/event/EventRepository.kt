package no.nav.personbruker.dittnav.eventhandler.event

import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.common.database.getUtcTimeStamp
import no.nav.personbruker.dittnav.eventhandler.common.database.mapList
import java.time.ZoneId
import java.time.ZonedDateTime

class EventRepository(private val database: Database) {

    suspend fun getInactiveEvents(fodselsnummer: String): List<Event> {
        return database.queryWithExceptionTranslation {
            prepareStatement("""
                ${eventQuery("beskjed")}
                UNION ALL
                ${eventQuery("oppgave")}
                UNION ALL 
                ${eventQuery("innboks")}
            """.trimIndent()
            ).also {
                it.setString(1, fodselsnummer)
                it.setString(2, fodselsnummer)
                it.setString(3, fodselsnummer)
            }.executeQuery().mapList {
                Event(
                    fodselsnummer = getString("fodselsnummer"),
                    grupperingsId = getString("grupperingsId"),
                    eventId = getString("eventId"),
                    eventTidspunkt = ZonedDateTime.ofInstant(
                        getUtcTimeStamp("eventTidspunkt").toInstant(),
                        ZoneId.of("Europe/Oslo")
                    ),
                    produsent = getString("appnavn") ?: "",
                    sikkerhetsnivaa = getInt("sikkerhetsnivaa"),
                    sistOppdatert = ZonedDateTime.ofInstant(
                        getUtcTimeStamp("sistOppdatert").toInstant(),
                        ZoneId.of("Europe/Oslo")
                    ),
                    tekst = getString("tekst"),
                    link = getString("link"),
                    aktiv = getBoolean("aktiv"),
                    type = EventType.fromOriginalType(getString("type"))
                )
            }
        }
    }

    private fun eventQuery(table: String): String {
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
            WHERE fodselsnummer = ?
            AND aktiv = false
        """
    }
}