package no.nav.personbruker.dittnav.eventhandler.oppgave

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.database.LocalPostgresDatabase
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.createEksternVarslingStatusOppgave
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.deleteEksternVarslingStatusOppgave
import java.sql.Connection
import java.sql.Types
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

fun Connection.createOppgave(oppgaveer: List<Oppgave>) =
    prepareStatement(
        """INSERT INTO oppgave(systembruker, eventTidspunkt, fodselsnummer, eventId, grupperingsId, tekst, link, sikkerhetsnivaa, sistOppdatert, aktiv, synligFremTil, namespace, appnavn, forstBehandlet, eksternVarsling, prefererteKanaler,frist_utløpt)
            VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)"""
    )
        .use {
            oppgaveer.forEach { oppgave ->
                run {
                    it.setString(1, oppgave.systembruker)
                    it.setObject(2, oppgave.eventTidspunkt.utcLocalDate(), Types.TIMESTAMP)
                    it.setString(3, oppgave.fodselsnummer)
                    it.setString(4, oppgave.eventId)
                    it.setString(5, oppgave.grupperingsId)
                    it.setString(6, oppgave.tekst)
                    it.setString(7, oppgave.link)
                    it.setInt(8, oppgave.sikkerhetsnivaa)
                    it.setObject(9, oppgave.sistOppdatert.utcLocalDate(), Types.TIMESTAMP)
                    it.setBoolean(10, oppgave.aktiv)
                    it.setObject(11, oppgave.synligFremTil?.utcLocalDate(), Types.TIMESTAMP)
                    it.setString(12, oppgave.namespace)
                    it.setString(13, oppgave.appnavn)
                    it.setObject(14, oppgave.forstBehandlet.utcLocalDate(), Types.TIMESTAMP)
                    it.setBoolean(15, oppgave.eksternVarsling != null)
                    it.setString(16, oppgave.eksternVarsling?.prefererteKanaler?.joinToString() ?: "")
                    oppgave.fristUtløpt?.let { fristUtløpt -> it.setBoolean(17, fristUtløpt) } ?: it.setNull(17, Types.BOOLEAN)
                    it.addBatch()
                }
            }
            it.executeBatch()
        }

private fun Connection.deleteAllOppgave() = prepareStatement("""DELETE FROM oppgave""").execute()

fun LocalPostgresDatabase.createOppgave(oppgaveList: List<Oppgave>) = runBlocking {
    dbQuery {
        createOppgave(oppgaveList)
    }
    dbQuery {
        createEksternVarslingStatuses(oppgaveList)
    }
}

fun LocalPostgresDatabase.deleteOppgave() = runBlocking {
    dbQuery {
        deleteEksternVarslingStatusOppgave()
    }
    dbQuery {
        deleteAllOppgave()
    }
}

fun LocalPostgresDatabase.createEksternVarslingStatuses(statuses: List<Oppgave>) = runBlocking {
    dbQuery {
        statuses.forEach { status ->
            createEksternVarslingStatusOppgave(status)
        }
    }
}

private fun ZonedDateTime.utcLocalDate()= LocalDateTime.ofInstant(Instant.ofEpochSecond(this.toEpochSecond()), ZoneId.of("UTC"))
