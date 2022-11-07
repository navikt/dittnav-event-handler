package no.nav.personbruker.dittnav.eventhandler.beskjed

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.database.LocalPostgresDatabase
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.DoknotifikasjonTestStatus
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.createDoknotStatusBeskjed
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.deleteDoknotStatusBeskjed
import java.sql.Connection
import java.sql.Types
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

fun Connection.createBeskjed(beskjeder: List<Beskjed>) =
    prepareStatement(
        """INSERT INTO beskjed(id, systembruker, eventTidspunkt, fodselsnummer, eventId, grupperingsId, tekst, link, sikkerhetsnivaa, sistOppdatert, aktiv, synligFremTil, namespace, appnavn, forstBehandlet, eksternVarsling, prefererteKanaler)
            VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"""
    )
        .use {
            beskjeder.forEach { beskjed ->
                run {
                    it.setInt(1, beskjed.id)
                    it.setString(2, beskjed.systembruker)
                    it.setObject(3, beskjed.eventTidspunkt.utcLocalDate(), Types.TIMESTAMP)
                    it.setString(4, beskjed.fodselsnummer)
                    it.setString(5, beskjed.eventId)
                    it.setString(6, beskjed.grupperingsId)
                    it.setString(7, beskjed.tekst)
                    it.setString(8, beskjed.link)
                    it.setInt(9, beskjed.sikkerhetsnivaa)
                    it.setObject(10, beskjed.sistOppdatert.utcLocalDate(), Types.TIMESTAMP)
                    it.setBoolean(11, beskjed.aktiv)
                    it.setObject(12, beskjed.synligFremTil?.utcLocalDate(), Types.TIMESTAMP)
                    it.setString(13, beskjed.namespace)
                    it.setString(14, beskjed.appnavn)
                    it.setObject(15, beskjed.forstBehandlet.utcLocalDate(), Types.TIMESTAMP)
                    it.setBoolean(16, beskjed.eksternVarslingSendt)
                    it.setString(17, beskjed.eksternVarslingKanaler.joinToString(","))
                    it.addBatch()
                }
            }
            it.executeBatch()
        }

fun Connection.deleteBeskjed(beskjeder: List<Beskjed>) =
    prepareStatement("""DELETE FROM beskjed WHERE eventId = ?""")
        .use {
            beskjeder.forEach { beskjed ->
                run {
                    it.setString(1, beskjed.eventId)
                    it.addBatch()
                }
            }
            it.executeBatch()
        }



internal fun LocalPostgresDatabase.createBeskjed(beskjeder: List<Beskjed>) = runBlocking { dbQuery { createBeskjed(beskjeder) } }
internal fun LocalPostgresDatabase.deleteBeskjed(beskjeder: List<Beskjed>) = runBlocking { dbQuery { deleteBeskjed(beskjeder) } }

internal fun LocalPostgresDatabase.deleteAllDoknotStatusBeskjed() = runBlocking { dbQuery { deleteDoknotStatusBeskjed() }}
internal fun LocalPostgresDatabase.createDoknotStatuses(statuses: List<DoknotifikasjonTestStatus>) = runBlocking {
    dbQuery {
        statuses.forEach { status -> createDoknotStatusBeskjed(status) }
    }
}

private fun ZonedDateTime.utcLocalDate()= LocalDateTime.ofInstant(Instant.ofEpochSecond(this.toEpochSecond()), ZoneId.of("UTC"))