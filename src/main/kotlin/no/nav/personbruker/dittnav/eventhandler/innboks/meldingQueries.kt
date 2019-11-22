package no.nav.personbruker.dittnav.eventhandler.innboks

import no.nav.personbruker.dittnav.eventhandler.common.database.map
import java.sql.Connection
import java.sql.ResultSet
import java.time.ZoneId
import java.time.LocalDateTime

fun Connection.getAllInnboksByAktorId(aktorId: String): List<Innboks> =
        prepareStatement("""SELECT * FROM INNBOKS WHERE aktorId = ?""")
            .use {
                it.setString(1, aktorId)
                it.executeQuery().map {
                    toInnboks()
                }
            }

fun Connection.getActiveInnboksByAktorId(aktorId: String): List<Innboks> =
        prepareStatement("""SELECT * FROM INNBOKS WHERE aktiv = true AND aktorId = ?""")
            .use {
                it.setString(1, aktorId)
                it.executeQuery().map {
                    toInnboks()
                }
            }

private fun ResultSet.toInnboks(): Innboks {
    return Innboks(
            id = getInt("id"),
            produsent = getString("produsent"),
            eventTidspunkt = LocalDateTime.ofInstant(getTimestamp("eventTidspunkt").toInstant(), ZoneId.of("Europe/Oslo")),
            aktorId = getString("aktorId"),
            eventId = getString("eventId"),
            dokumentId = getString("dokumentId"),
            tekst = getString("tekst"),
            link = getString("link"),
            sikkerhetsnivaa = getInt("sikkerhetsnivaa"),
            sistOppdatert = LocalDateTime.ofInstant(getTimestamp("sistOppdatert").toInstant(), ZoneId.of("Europe/Oslo")),
            aktiv = getBoolean("aktiv")
    )
}