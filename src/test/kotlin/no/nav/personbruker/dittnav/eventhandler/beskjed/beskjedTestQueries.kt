package no.nav.personbruker.dittnav.eventhandler.beskjed

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.database.LocalPostgresDatabase
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.createEksternVarslingStatusBeskjed
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.deleteEksternVarslingStatusBeskjed
import java.sql.Connection
import java.sql.Types
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

fun Connection.createBeskjed(beskjeder: List<Beskjed>) =
    prepareStatement(
        """INSERT INTO beskjed(systembruker, eventTidspunkt, fodselsnummer, eventId, grupperingsId, tekst, link, sikkerhetsnivaa, sistOppdatert, aktiv, synligFremTil, namespace, appnavn, forstBehandlet, eksternVarsling, prefererteKanaler,frist_utløpt)
            VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)"""
    )
        .use {
            beskjeder.forEach { beskjed ->
                run {
                    it.setString(1, beskjed.systembruker)
                    it.setObject(2, beskjed.eventTidspunkt.utcLocalDate(), Types.TIMESTAMP)
                    it.setString(3, beskjed.fodselsnummer)
                    it.setString(4, beskjed.eventId)
                    it.setString(5, beskjed.grupperingsId)
                    it.setString(6, beskjed.tekst)
                    it.setString(7, beskjed.link)
                    it.setInt(8, beskjed.sikkerhetsnivaa)
                    it.setObject(9, beskjed.sistOppdatert.utcLocalDate(), Types.TIMESTAMP)
                    it.setBoolean(10, beskjed.aktiv)
                    it.setObject(11, beskjed.synligFremTil?.utcLocalDate(), Types.TIMESTAMP)
                    it.setString(12, beskjed.namespace)
                    it.setString(13, beskjed.appnavn)
                    it.setObject(14, beskjed.forstBehandlet.utcLocalDate(), Types.TIMESTAMP)
                    it.setBoolean(15, beskjed.eksternVarsling != null)
                    it.setString(16, beskjed.eksternVarsling?.prefererteKanaler?.joinToString() ?: "")
                    beskjed.fristUtløpt?.let { fristUtløpt -> it.setBoolean(17, fristUtløpt) } ?: it.setNull(17, Types.BOOLEAN)
                    it.addBatch()
                }
            }
            it.executeBatch()
        }

private fun Connection.deleteAllBeskjed() = prepareStatement("""DELETE FROM beskjed""").execute()

fun LocalPostgresDatabase.createBeskjed(beskjedList: List<Beskjed>) = runBlocking {
    dbQuery {
        createBeskjed(beskjedList)
    }
    dbQuery {
        createEksternVarslingStatuses(beskjedList)
    }
}

fun LocalPostgresDatabase.deleteBeskjed() = runBlocking {
    dbQuery {
        deleteEksternVarslingStatusBeskjed()
    }
    dbQuery {
        deleteAllBeskjed()
    }
}

fun LocalPostgresDatabase.createEksternVarslingStatuses(statuses: List<Beskjed>) = runBlocking {
    dbQuery {
        statuses.forEach { status ->
            createEksternVarslingStatusBeskjed(status)
        }
    }
}

private fun ZonedDateTime.utcLocalDate() =
    LocalDateTime.ofInstant(Instant.ofEpochSecond(this.toEpochSecond()), ZoneId.of("UTC"))
