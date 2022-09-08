package no.nav.personbruker.dittnav.eventhandler.beskjed

import Beskjed
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.database.LocalPostgresDatabase
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.DoknotifikasjonStatusDto
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.createDoknotStatusBeskjed
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.deleteDoknotStatusBeskjed
import java.sql.Connection
import java.sql.Types

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
                    it.setObject(3, beskjed.eventTidspunkt.toLocalDateTime(), Types.TIMESTAMP)
                    it.setString(4, beskjed.fodselsnummer)
                    it.setString(5, beskjed.eventId)
                    it.setString(6, beskjed.grupperingsId)
                    it.setString(7, beskjed.tekst)
                    it.setString(8, beskjed.link)
                    it.setInt(9, beskjed.sikkerhetsnivaa)
                    it.setObject(10, beskjed.sistOppdatert.toLocalDateTime(), Types.TIMESTAMP)
                    it.setBoolean(11, beskjed.aktiv)
                    it.setObject(12, beskjed.synligFremTil?.toLocalDateTime(), Types.TIMESTAMP)
                    it.setString(13, beskjed.namespace)
                    it.setString(14, beskjed.appnavn)
                    it.setObject(15, beskjed.forstBehandlet.toLocalDateTime(), Types.TIMESTAMP)
                    it.setBoolean(16, beskjed.eksternVarslingInfo.bestilt)
                    it.setString(17, beskjed.eksternVarslingInfo.prefererteKanaler.joinToString(","))
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
internal fun LocalPostgresDatabase.createDoknotStatuses(statuses: List<DoknotifikasjonStatusDto>) = runBlocking {
    dbQuery {
        statuses.forEach { status -> createDoknotStatusBeskjed(status) }
    }
}