package no.nav.personbruker.dittnav.eventhandler.oppgave

import no.nav.personbruker.dittnav.eventhandler.common.InnloggetBruker
import no.nav.personbruker.dittnav.eventhandler.common.database.convertIfUnlikelyDate
import no.nav.personbruker.dittnav.eventhandler.common.database.getUtcTimeStamp
import no.nav.personbruker.dittnav.eventhandler.common.database.map
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.EventCacheException
import no.nav.personbruker.dittnav.eventhandler.config.Kafka.BACKUP_EVENT_CHUNCK_SIZE
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
            |oppgave.systembruker,
            |systembrukere.produsentnavn AS produsent
            |FROM (SELECT * FROM oppgave WHERE fodselsnummer = ?) AS oppgave
            |LEFT JOIN systembrukere ON oppgave.systembruker = systembrukere.systembruker""".trimMargin())
                .use {
                    it.setString(1, bruker.ident)
                    it.executeQuery().map {
                        toOppgave()
                    }
                }

fun Connection.getAllOppgaveEvents(): List<Oppgave> =
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
            |systembrukere.produsentnavn AS produsent
            |FROM oppgave LEFT JOIN systembrukere ON oppgave.systembruker = systembrukere.systembruker""".trimMargin())
                .use {
                    it.fetchSize = BACKUP_EVENT_CHUNCK_SIZE
                    it.executeQuery().map {
                        toOppgave()
                    }
                }

fun Connection.getAllInactiveOppgaveEvents(): List<Oppgave> =
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
            |systembrukere.produsentnavn AS produsent
            |FROM (SELECT * FROM oppgave WHERE aktiv = false) AS oppgave
            |LEFT JOIN systembrukere ON oppgave.systembruker = systembrukere.systembruker""".trimMargin())
                .use {
                    it.fetchSize = BACKUP_EVENT_CHUNCK_SIZE
                    it.executeQuery().map {
                        toOppgave()
                    }
                }

fun Connection.getAllGroupedOppgaveEventsByIds(bruker: InnloggetBruker, grupperingsid: String, produsent: String): List<Oppgave> =
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
            |systembrukere.produsentnavn AS produsent
            |FROM (SELECT * FROM oppgave WHERE fodselsnummer = ? AND grupperingsid = ?) AS oppgave
            |LEFT JOIN systembrukere ON oppgave.systembruker = systembrukere.systembruker WHERE systembrukere.produsentnavn = ?""".trimMargin())
                .use {
                    it.setString(1, bruker.ident)
                    it.setString(2, grupperingsid)
                    it.setString(3, produsent)
                    it.executeQuery().map {
                        toOppgave()
                    }
                }

private fun ResultSet.toOppgave(): Oppgave {
    val rawEventTidspunkt = getUtcTimeStamp("eventTidspunkt") ?: throw EventCacheException("Eventtidspunkt ble ikke funnet i databasen")
    val verifiedEventTidspunkt = convertIfUnlikelyDate(rawEventTidspunkt)
    return Oppgave(
            id = getInt("id"),
            produsent = getString("produsent") ?: "",
            systembruker = getString("systembruker"),
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
            |oppgave.systembruker,
            |systembrukere.produsentnavn AS produsent
            |FROM (SELECT * FROM oppgave WHERE fodselsnummer = ? AND aktiv = ?) AS oppgave
            |LEFT JOIN systembrukere ON oppgave.systembruker = systembrukere.systembruker""".trimMargin())
                .use {
                    it.setString(1, bruker.ident)
                    it.setBoolean(2, aktiv)
                    it.executeQuery().map {
                        toOppgave()
                    }
                }
