package no.nav.personbruker.dittnav.eventhandler.beskjed

import Beskjed
import no.nav.personbruker.dittnav.eventhandler.common.InnloggetBruker
import no.nav.personbruker.dittnav.eventhandler.common.database.map
import java.sql.Connection
import java.sql.ResultSet
import java.time.ZoneId
import java.time.ZonedDateTime

fun Connection.getAllBeskjedByFodselsnummer(bruker: InnloggetBruker): List<Beskjed> =
        prepareStatement("""SELECT * FROM BESKJED WHERE fodselsnummer = ?""")
                .use {
                    it.setString(1, bruker.getIdent())
                    it.executeQuery().map {
                        toBeskjed()
                    }
                }

fun Connection.getActiveBeskjedByFodselsnummer(bruker: InnloggetBruker): List<Beskjed> =
        prepareStatement("""SELECT * FROM BESKJED WHERE fodselsnummer = ? AND aktiv = true""")
                .use {
                    it.setString(1, bruker.getIdent())
                    it.executeQuery().map {
                        toBeskjed()
                    }
                }

fun ResultSet.toBeskjed(): Beskjed {
    return Beskjed(
            id = getInt("id"),
            uid = getInt("uid"),
            produsent = getString("produsent"),
            eventTidspunkt = ZonedDateTime.ofInstant(getTimestamp("eventTidspunkt").toInstant(), ZoneId.of("Europe/Oslo")),
            fodselsnummer = getString("fodselsnummer"),
            eventId = getString("eventId"),
            grupperingsId = getString("grupperingsId"),
            tekst = getString("tekst"),
            link = getString("link"),
            sikkerhetsnivaa = getInt("sikkerhetsnivaa"),
            sistOppdatert = ZonedDateTime.ofInstant(getTimestamp("sistOppdatert").toInstant(), ZoneId.of("Europe/Oslo")),
            synligFremTil = getNullableZonedDateTime("synligFremTil"),
            aktiv = getBoolean("aktiv")
    )
}

private fun ResultSet.getNullableZonedDateTime(label: String) : ZonedDateTime? {
    return getTimestamp(label)?.let { timestamp -> ZonedDateTime.ofInstant(timestamp.toInstant(), ZoneId.of("Europe/Oslo")) }
}