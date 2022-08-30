package no.nav.personbruker.dittnav.eventhandler.event

import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.common.database.getUtcTimeStamp
import no.nav.personbruker.dittnav.eventhandler.common.database.mapList
import no.nav.personbruker.dittnav.eventhandler.common.daysAgo
import java.sql.ResultSet
import java.time.ZoneId
import java.time.ZonedDateTime

class EventRepository(private val database: Database) {

    suspend fun getInactiveEvents(fodselsnummer: String): List<Event> {
        return getEvents(fodselsnummer, false)
    }

    suspend fun getActiveEvents(fodselsnummer: String): List<Event> {
        return getEvents(fodselsnummer, true)
    }

    private suspend fun getEvents(fodselsnummer: String, active: Boolean): List<Event> {
        return database.queryWithExceptionTranslation {
            prepareStatement("""
                ${eventQuery("beskjed", active)}
                UNION ALL
                ${eventQuery("oppgave", active)}
                UNION ALL 
                ${eventQuery("innboks", active)}
            """.trimIndent()
            ).also {
                it.setString(1, fodselsnummer)
                it.setString(2, fodselsnummer)
                it.setString(3, fodselsnummer)
            }.executeQuery().mapList { toEvent() }
        }
    }

    private fun eventQuery(table: String, active: Boolean): String {
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
                forstBehandlet,
                '$table' as type 
            FROM $table
            WHERE fodselsnummer = ?
            AND aktiv = $active
        """
    }

    private fun ResultSet.toEvent(): Event = Event(
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
        type = EventType.fromOriginalType(getString("type")),
        forstBehandlet = ZonedDateTime.ofInstant(
            getUtcTimeStamp("forstBehandlet").toInstant(),
            ZoneId.of("Europe/Oslo")
        )
    )
}
