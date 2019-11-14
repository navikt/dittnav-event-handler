package no.nav.personbruker.dittnav.eventhandler.melding

import no.nav.personbruker.dittnav.eventhandler.common.database.map
import java.sql.Connection
import java.sql.ResultSet
import java.time.ZoneId
import java.time.ZonedDateTime

fun Connection.getAllMeldingByAktorId(aktorId: String): List<Melding> =
        prepareStatement("""SELECT * FROM MELDING WHERE aktorId = ?""")
            .use {
                it.setString(1, aktorId)
                it.executeQuery().map {
                    toMelding()
                }
            }

fun Connection.getActiveMeldingByAktorId(aktorId: String): List<Melding> =
        prepareStatement("""SELECT * FROM MELDING WHERE aktiv = true AND aktorId = ?""")
            .use {
                it.setString(1, aktorId)
                it.executeQuery().map {
                    toMelding()
                }
            }

private fun ResultSet.toMelding(): Melding {
    return Melding(
            id = getInt("id"),
            produsent = getString("produsent"),
            eventTidspunkt = ZonedDateTime.ofInstant(getTimestamp("eventTidspunkt").toInstant(), ZoneId.of("Europe/Oslo")),
            aktorId = getString("aktorId"),
            eventId = getString("eventId"),
            dokumentId = getString("dokumentId"),
            tekst = getString("tekst"),
            link = getString("link"),
            sikkerhetsnivaa = getInt("sikkerhetsnivaa"),
            sistOppdatert = ZonedDateTime.ofInstant(getTimestamp("sistOppdatert").toInstant(), ZoneId.of("Europe/Oslo")),
            aktiv = getBoolean("aktiv")
    )
}