package no.nav.personbruker.dittnav.eventhandler.innboks

import no.nav.personbruker.dittnav.eventhandler.common.InnloggetBruker
import no.nav.personbruker.dittnav.eventhandler.common.database.getUtcTimeStamp
import no.nav.personbruker.dittnav.eventhandler.common.database.map
import java.sql.Connection
import java.sql.ResultSet
import java.time.ZoneId
import java.time.ZonedDateTime

fun Connection.getInaktivInnboksForInnloggetBruker(bruker: InnloggetBruker): List<Innboks> =
        getInnboksForInnloggetBruker(bruker, false)

fun Connection.getAktivInnboksForInnloggetBruker(bruker: InnloggetBruker): List<Innboks> =
        getInnboksForInnloggetBruker(bruker, true)

fun Connection.getAllInnboksForInnloggetBruker(bruker: InnloggetBruker): List<Innboks> =
        prepareStatement("""SELECT
            |innboks.id,
            |innboks.eventTidspunkt,
            |innboks.fodselsnummer,
            |innboks.eventId,
            |innboks.grupperingsId,
            |innboks.tekst,
            |innboks.link,
            |innboks.sikkerhetsnivaa,
            |innboks.sistOppdatert,
            |innboks.aktiv,
            |innboks.systembruker,
            |systembrukere.produsentnavn AS produsent
            |FROM (SELECT * FROM innboks WHERE fodselsnummer = ?) AS innboks
            |LEFT JOIN systembrukere ON innboks.systembruker = systembrukere.systembruker""".trimMargin())
                .use {
                    it.setString(1, bruker.ident)
                    it.executeQuery().map {
                        toInnboks()
                    }
                }

fun Connection.getAllGroupedInnboksEventsByIds(bruker: InnloggetBruker, grupperingsid: String, produsent: String): List<Innboks> =
        prepareStatement("""SELECT
            |innboks.id,
            |innboks.eventTidspunkt,
            |innboks.fodselsnummer,
            |innboks.eventId,
            |innboks.grupperingsId,
            |innboks.tekst,
            |innboks.link,
            |innboks.sikkerhetsnivaa,
            |innboks.sistOppdatert,
            |innboks.aktiv,
            |innboks.systembruker,
            |systembrukere.produsentnavn AS produsent
            |FROM (SELECT * FROM innboks WHERE fodselsnummer = ? AND grupperingsid = ?) AS innboks
            |LEFT JOIN systembrukere ON innboks.systembruker = systembrukere.systembruker WHERE systembrukere.produsentnavn = ?""".trimMargin())
                .use {
                    it.setString(1, bruker.ident)
                    it.setString(2, grupperingsid)
                    it.setString(3, produsent)
                    it.executeQuery().map {
                        toInnboks()
                    }
                }

private fun ResultSet.toInnboks(): Innboks {
    return Innboks(
            id = getInt("id"),
            produsent = getString("produsent") ?: "",
            systembruker = getString("systembruker"),
            eventTidspunkt = ZonedDateTime.ofInstant(getUtcTimeStamp("eventTidspunkt").toInstant(), ZoneId.of("Europe/Oslo")),
            fodselsnummer = getString("fodselsnummer"),
            eventId = getString("eventId"),
            grupperingsId = getString("grupperingsId"),
            tekst = getString("tekst"),
            link = getString("link"),
            sikkerhetsnivaa = getInt("sikkerhetsnivaa"),
            sistOppdatert = ZonedDateTime.ofInstant(getUtcTimeStamp("sistOppdatert").toInstant(), ZoneId.of("Europe/Oslo")),
            aktiv = getBoolean("aktiv")
    )
}

private fun Connection.getInnboksForInnloggetBruker(bruker: InnloggetBruker, aktiv: Boolean): List<Innboks> =
        prepareStatement("""SELECT
            |innboks.id,
            |innboks.eventTidspunkt,
            |innboks.fodselsnummer,
            |innboks.eventId,
            |innboks.grupperingsId,
            |innboks.tekst,
            |innboks.link,
            |innboks.sikkerhetsnivaa,
            |innboks.sistOppdatert,
            |innboks.aktiv,
            |innboks.systembruker,
            |systembrukere.produsentnavn AS produsent
            |FROM (SELECT * FROM innboks WHERE fodselsnummer = ? AND aktiv = ?) AS innboks
            |LEFT JOIN systembrukere ON innboks.systembruker = systembrukere.systembruker""".trimMargin())
                .use {
                    it.setString(1, bruker.ident)
                    it.setBoolean(2, aktiv)
                    it.executeQuery().map {
                        toInnboks()
                    }
                }
