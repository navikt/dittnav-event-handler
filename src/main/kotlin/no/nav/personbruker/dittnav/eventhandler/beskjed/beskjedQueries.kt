package no.nav.personbruker.dittnav.eventhandler.beskjed

import Beskjed
import no.nav.personbruker.dittnav.eventhandler.common.InnloggetBruker
import no.nav.personbruker.dittnav.eventhandler.common.database.map
import java.sql.Connection
import java.sql.ResultSet
import java.time.ZoneId
import java.time.ZonedDateTime

fun Connection.getInaktivBeskjedForInnloggetBruker(bruker: InnloggetBruker): List<Beskjed> =
        getBeskjedForInnloggetBruker(bruker, false)

fun Connection.getAktivBeskjedForInnloggetBruker(bruker: InnloggetBruker): List<Beskjed> =
        getBeskjedForInnloggetBruker(bruker, true)

fun Connection.getAllBeskjedForInnloggetBruker(bruker: InnloggetBruker): List<Beskjed> =
        prepareStatement("""SELECT 
            |beskjed.id, 
            |beskjed.uid, 
            |beskjed.eventTidspunkt,
            |beskjed.fodselsnummer,
            |beskjed.eventId, 
            |beskjed.grupperingsId,
            |beskjed.tekst,
            |beskjed.link,
            |beskjed.sikkerhetsnivaa,
            |beskjed.sistOppdatert,
            |beskjed.synligFremTil,
            |beskjed.aktiv,
            |systembrukere.produsentnavn
            |FROM beskjed INNER JOIN systembrukere ON beskjed.produsent = systembrukere.systembruker
            |WHERE beskjed.fodselsnummer = ?""".trimMargin())
                .use {
                    it.setString(1, bruker.ident)
                    it.executeQuery().map {
                        toBeskjed()
                    }
                }

fun Connection.getActiveBeskjedByIds(fodselsnummer: String, uid: String, eventId: String): List<Beskjed> =
        prepareStatement("""SELECT * FROM BESKJED WHERE fodselsnummer = ? AND uid = ? AND eventId = ? AND aktiv = true""")
                .use {
                    it.setString(1, fodselsnummer)
                    it.setString(2, uid)
                    it.setString(3, eventId)
                    it.executeQuery().map {
                        toDoneBeskjed()
                    }
                }

fun ResultSet.toBeskjed(): Beskjed {
    return Beskjed(
            id = getInt("id"),
            uid = getString("uid"),
            produsent = getString("produsentnavn"),
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

fun ResultSet.toDoneBeskjed(): Beskjed {
    return Beskjed(
            id = getInt("id"),
            uid = getString("uid"),
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

private fun Connection.getBeskjedForInnloggetBruker(bruker: InnloggetBruker, aktiv: Boolean): List<Beskjed> =
        prepareStatement("""SELECT 
            |beskjed.id, 
            |beskjed.uid, 
            |beskjed.eventTidspunkt,
            |beskjed.fodselsnummer,
            |beskjed.eventId, 
            |beskjed.grupperingsId,
            |beskjed.tekst,
            |beskjed.link,
            |beskjed.sikkerhetsnivaa,
            |beskjed.sistOppdatert,
            |beskjed.synligFremTil,
            |beskjed.aktiv,
            |systembrukere.produsentnavn
            |FROM beskjed INNER JOIN systembrukere ON beskjed.produsent = systembrukere.systembruker
            |WHERE beskjed.fodselsnummer = ? AND beskjed.aktiv = ?""".trimMargin())
                .use {
                    it.setString(1, bruker.ident)
                    it.setBoolean(2, aktiv)
                    it.executeQuery().map {
                        toBeskjed()
                    }
                }

private fun ResultSet.getNullableZonedDateTime(label: String) : ZonedDateTime? {
    return getTimestamp(label)?.let { timestamp -> ZonedDateTime.ofInstant(timestamp.toInstant(), ZoneId.of("Europe/Oslo")) }
}
