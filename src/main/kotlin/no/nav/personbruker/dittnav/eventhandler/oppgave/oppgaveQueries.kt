package no.nav.personbruker.dittnav.eventhandler.oppgave

import no.nav.personbruker.dittnav.eventhandler.common.InnloggetBruker
import no.nav.personbruker.dittnav.eventhandler.common.database.map
import java.sql.Connection
import java.sql.ResultSet
import java.time.ZoneId
import java.time.ZonedDateTime

fun Connection.getInaktivOppgaveForInnloggetBruker(bruker: InnloggetBruker): List<Oppgave> =
        getOppgaveForInnloggetBruker(bruker, false)

fun Connection.getAktivOppgaveForInnloggetBruker(bruker: InnloggetBruker): List<Oppgave> =
        getOppgaveForInnloggetBruker(bruker, true)

fun Connection.getAllOppgaveForInnloggetBruker(bruker: InnloggetBruker): List<Oppgave> =
        prepareStatement("""SELECT 
            |oppgave.id,
            |oppgave.eventTidspunkt,
            |oppgave.fodselsnummer,
            |oppgave.eventId,
            |oppgave.grupperingsId,
            |oppgave.tekst,
            |oppgave.link,
            |oppgave.sikkerhetsnivaa,
            |oppgave.sistOppdatert,
            |oppgave.aktiv,
            |systembrukere.produsentnavn
            |FROM oppgave INNER JOIN systembrukere ON oppgave.produsent = systembrukere.systembruker
            |WHERE fodselsnummer = ?""".trimMargin())
                .use {
                    it.setString(1, bruker.ident)
                    it.executeQuery().map {
                        toOppgave()
                    }
                }

private fun ResultSet.toOppgave(): Oppgave {
    return Oppgave(
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

private fun Connection.getOppgaveForInnloggetBruker(bruker: InnloggetBruker, aktiv: Boolean): List<Oppgave> =
        prepareStatement("""SELECT
            |oppgave.id,
            |oppgave.eventTidspunkt,
            |oppgave.fodselsnummer,
            |oppgave.eventId,
            |oppgave.grupperingsId,
            |oppgave.tekst,
            |oppgave.link,
            |oppgave.sikkerhetsnivaa,
            |oppgave.sistOppdatert,
            |oppgave.aktiv,
            |systembrukere.produsentnavn
            |FROM oppgave INNER JOIN systembrukere ON oppgave.produsent = systembrukere.systembruker
            |WHERE fodselsnummer = ? AND aktiv = ?""".trimMargin())
                .use {
                    it.setString(1, bruker.ident)
                    it.setBoolean(2, aktiv)
                    it.executeQuery().map {
                        toOppgave()
                    }
                }
