package no.nav.personbruker.dittnav.eventhandler.innboks

import no.nav.personbruker.dittnav.eventhandler.common.database.map
import java.sql.Connection
import java.sql.ResultSet
import java.time.ZoneId
import java.time.ZonedDateTime

fun Connection.getAllInnboksByFodselsnummer(fodselsnummer: String): List<Innboks> =
        prepareStatement("""SELECT * FROM INNBOKS WHERE fodselsnummer = ?""")
            .use {
                it.setString(1, fodselsnummer)
                it.executeQuery().map {
                    toInnboks()
                }
            }

fun Connection.getActiveInnboksByFodselsnummer(fodselsnummer: String): List<Innboks> =
        prepareStatement("""SELECT * FROM INNBOKS WHERE aktiv = true AND fodselsnummer = ?""")
            .use {
                it.setString(1, fodselsnummer)
                it.executeQuery().map {
                    toInnboks()
                }
            }

private fun ResultSet.toInnboks(): Innboks {
    return Innboks(
            id = getInt("id"),
            produsent = getString("produsent"),
            eventTidspunkt = ZonedDateTime.ofInstant(getTimestamp("eventTidspunkt").toInstant(), ZoneId.of("Europe/Oslo")),
            fodselsnummer = getString("fodselsnummer"),
            eventId = getString("eventId"),
            grupperingsId = getString("grupperingsId"),
            tekst = getString("tekst"),
            link = getString("link"),
            sikkerhetsnivaa = getInt("sikkerhetsnivaa"),
            sistOppdatert = ZonedDateTime.ofInstant(getTimestamp("sistOppdatert").toInstant(), ZoneId.of("Europe/Oslo")),
            aktiv = getBoolean("aktiv")
    )
}