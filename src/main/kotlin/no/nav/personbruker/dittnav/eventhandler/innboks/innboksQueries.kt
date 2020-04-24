package no.nav.personbruker.dittnav.eventhandler.innboks

import no.nav.personbruker.dittnav.eventhandler.common.InnloggetBruker
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
            |systembrukere.produsentnavn
            |FROM innboks INNER JOIN systembrukere ON innboks.produsent = systembrukere.systembruker
            |WHERE innboks.fodselsnummer = ?""".trimMargin())
                .use {
                    it.setString(1, bruker.ident)
                    it.executeQuery().map {
                        toInnboks()
                    }
                }

private fun ResultSet.toInnboks(): Innboks {
    return Innboks(
            id = getInt("id"),
            produsent = getString("produsentnavn"),
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
            |systembrukere.produsentnavn
            |FROM innboks INNER JOIN systembrukere ON innboks.produsent = systembrukere.systembruker
            |WHERE fodselsnummer = ? AND aktiv = ?""".trimMargin())
                .use {
                    it.setString(1, bruker.ident)
                    it.setBoolean(2, aktiv)
                    it.executeQuery().map {
                        toInnboks()
                    }
                }
