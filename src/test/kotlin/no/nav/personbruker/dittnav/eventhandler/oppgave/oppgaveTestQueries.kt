package no.nav.personbruker.dittnav.eventhandler.oppgave

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.database.LocalPostgresDatabase
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.DoknotifikasjonTestStatus
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.createDoknotStatusOppgave
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.deleteDoknotStatusOppgave
import java.sql.Connection
import java.sql.Types

fun Connection.createOppgave(oppgaver: List<Oppgave>) =
    prepareStatement(
        """INSERT INTO oppgave(id, systembruker, eventTidspunkt, fodselsnummer, eventId, grupperingsId, tekst, link, sikkerhetsnivaa, sistOppdatert, aktiv, namespace, appnavn, forstBehandlet, eksternVarsling, prefererteKanaler)
            VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"""
    )
        .use {
            oppgaver.forEach { oppgave ->
                run {
                    it.setInt(1, oppgave.id)
                    it.setString(2, oppgave.systembruker)
                    it.setObject(3, oppgave.eventTidspunkt.toLocalDateTime(), Types.TIMESTAMP)
                    it.setString(4, oppgave.fodselsnummer)
                    it.setString(5, oppgave.eventId)
                    it.setString(6, oppgave.grupperingsId)
                    it.setString(7, oppgave.tekst)
                    it.setString(8, oppgave.link)
                    it.setInt(9, oppgave.sikkerhetsnivaa)
                    it.setObject(10, oppgave.sistOppdatert.toLocalDateTime(), Types.TIMESTAMP)
                    it.setBoolean(11, oppgave.aktiv)
                    it.setString(12, oppgave.namespace)
                    it.setString(13, oppgave.appnavn)
                    it.setObject(14, oppgave.forstBehandlet.toLocalDateTime(), Types.TIMESTAMP)
                    it.setObject(15, oppgave.eksternVarslingInfo.bestilt)
                    it.setObject(16, oppgave.eksternVarslingInfo.prefererteKanaler.joinToString(","))
                    it.addBatch()
                }
            }
            it.executeBatch()
        }

fun Connection.deleteOppgave(oppgaver: List<Oppgave>) =
    prepareStatement("""DELETE FROM oppgave WHERE eventId = ?""")
        .use {
            oppgaver.forEach { oppgave ->
                run {
                    it.setString(1, oppgave.eventId)
                    it.addBatch()
                }
            }
            it.executeBatch()
        }

internal fun LocalPostgresDatabase.createOppgave(oppgaver: List<Oppgave>) {
    runBlocking {
        dbQuery { createOppgave(oppgaver) }
    }
}

internal fun LocalPostgresDatabase.deleteOppgave(oppgaver: List<Oppgave>) {
    runBlocking {
        dbQuery { deleteOppgave(oppgaver) }
    }
}

internal fun LocalPostgresDatabase.createDoknotStatuses(statuses: List<DoknotifikasjonTestStatus>) = runBlocking {
    dbQuery {
        statuses.forEach { status ->
            createDoknotStatusOppgave(status)
        }
    }
}

internal fun LocalPostgresDatabase.deleteAllDoknotStatusOppgave() = runBlocking {
    dbQuery {
        deleteDoknotStatusOppgave()
    }
}