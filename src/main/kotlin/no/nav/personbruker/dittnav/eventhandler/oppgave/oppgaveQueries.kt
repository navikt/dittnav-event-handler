package no.nav.personbruker.dittnav.eventhandler.oppgave

import no.nav.personbruker.dittnav.eventhandler.common.database.convertIfUnlikelyDate
import no.nav.personbruker.dittnav.eventhandler.common.database.getUtcTimeStamp
import no.nav.personbruker.dittnav.eventhandler.common.database.mapList
import no.nav.tms.token.support.tokenx.validation.user.TokenXUser
import java.sql.Connection
import java.sql.ResultSet
import java.time.ZoneId
import java.time.ZonedDateTime

fun Connection.getInaktivOppgaveForInnloggetBruker(bruker: TokenXUser): List<Oppgave> =
        getOppgaveForInnloggetBruker(bruker, false)

fun Connection.getAktivOppgaveForInnloggetBruker(bruker: TokenXUser): List<Oppgave> =
        getOppgaveForInnloggetBruker(bruker, true)

fun Connection.getAllOppgaveForInnloggetBruker(bruker: TokenXUser): List<Oppgave> =
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
            |oppgave.systembruker,
            |oppgave.namespace,
            |oppgave.appnavn,
            |systembrukere.produsentnavn AS produsent
            |FROM (SELECT * FROM oppgave WHERE fodselsnummer = ?) AS oppgave
            |LEFT JOIN systembrukere ON oppgave.systembruker = systembrukere.systembruker""".trimMargin())
                .use {
                    it.setString(1, bruker.ident)
                    it.executeQuery().mapList {
                        toOppgave()
                    }
                }

fun Connection.getAllGroupedOppgaveEventsByIds(bruker: TokenXUser, grupperingsid: String, produsent: String): List<Oppgave> =
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
            |oppgave.systembruker,
            |oppgave.namespace,
            |oppgave.appnavn,
            |systembrukere.produsentnavn AS produsent
            |FROM (SELECT * FROM oppgave WHERE fodselsnummer = ? AND grupperingsid = ?) AS oppgave
            |LEFT JOIN systembrukere ON oppgave.systembruker = systembrukere.systembruker WHERE systembrukere.produsentnavn = ?""".trimMargin())
                .use {
                    it.setString(1, bruker.ident)
                    it.setString(2, grupperingsid)
                    it.setString(3, produsent)
                    it.executeQuery().mapList {
                        toOppgave()
                    }
                }

private fun ResultSet.toOppgave(): Oppgave {
    val rawEventTidspunkt = getUtcTimeStamp("eventTidspunkt")
    val verifiedEventTidspunkt = convertIfUnlikelyDate(rawEventTidspunkt)
    return Oppgave(
            id = getInt("id"),
            produsent = getString("produsent") ?: "",
            systembruker = getString("systembruker"),
            namespace = getString("namespace"),
            appnavn = getString("appnavn"),
            eventTidspunkt = verifiedEventTidspunkt,
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

private fun Connection.getOppgaveForInnloggetBruker(bruker: TokenXUser, aktiv: Boolean): List<Oppgave> =
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
            |oppgave.systembruker,
            |oppgave.namespace,
            |oppgave.appnavn,
            |systembrukere.produsentnavn AS produsent
            |FROM (SELECT * FROM oppgave WHERE fodselsnummer = ? AND aktiv = ?) AS oppgave
            |LEFT JOIN systembrukere ON oppgave.systembruker = systembrukere.systembruker""".trimMargin())
                .use {
                    it.setString(1, bruker.ident)
                    it.setBoolean(2, aktiv)
                    it.executeQuery().mapList {
                        toOppgave()
                    }
                }

fun Connection.getAllGroupedOppgaveEventsBySystemuser(): Map<String, Int> {
    return prepareStatement("SELECT systembruker, COUNT(*) FROM oppgave GROUP BY systembruker",
            ResultSet.TYPE_SCROLL_INSENSITIVE,
            ResultSet.CONCUR_READ_ONLY)
            .use { statement ->
                val resultSet = statement.executeQuery()
                mutableMapOf<String, Int>().apply {
                    while (resultSet.next()) {
                        put(resultSet.getString(1), resultSet.getInt(2))
                    }
                }
            }
}
