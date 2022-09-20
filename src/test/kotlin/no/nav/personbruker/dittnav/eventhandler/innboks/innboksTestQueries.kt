package no.nav.personbruker.dittnav.eventhandler.innboks

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.database.LocalPostgresDatabase
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.DoknotifikasjonTestStatus
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.createDoknotStatusInnboks
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.deleteDoknotStatusInnboks
import java.sql.Connection
import java.sql.Types
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

fun Connection.createInnboks(innbokser: List<Innboks>) =
    prepareStatement(
        """INSERT INTO innboks(id, systembruker, eventTidspunkt, fodselsnummer, eventId, grupperingsId, tekst, link, sikkerhetsnivaa, sistOppdatert, aktiv, namespace, appnavn, forstBehandlet, eksternVarsling, prefererteKanaler)
            VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"""
    )
        .use {
            innbokser.forEach { innboks ->
                run {
                    it.setInt(1, innboks.id)
                    it.setString(2, innboks.systembruker)
                    it.setObject(3, innboks.eventTidspunkt.utcLocalDate(), Types.TIMESTAMP)
                    it.setString(4, innboks.fodselsnummer)
                    it.setString(5, innboks.eventId)
                    it.setString(6, innboks.grupperingsId)
                    it.setString(7, innboks.tekst)
                    it.setString(8, innboks.link)
                    it.setInt(9, innboks.sikkerhetsnivaa)
                    it.setObject(10, innboks.sistOppdatert.utcLocalDate(), Types.TIMESTAMP)
                    it.setBoolean(11, innboks.aktiv)
                    it.setString(12, innboks.namespace)
                    it.setString(13, innboks.appnavn)
                    it.setObject(14, innboks.forstBehandlet.utcLocalDate(), Types.TIMESTAMP)
                    it.setObject(15, innboks.eksternVarslingInfo.bestilt)
                    it.setObject(16, innboks.eksternVarslingInfo.prefererteKanaler.joinToString(","))
                    it.addBatch()
                }
            }
            it.executeBatch()
        }

fun Connection.deleteInnboks(innbokser: List<Innboks>) =
    prepareStatement("""DELETE FROM innboks WHERE eventId = ?""")
        .use {
            innbokser.forEach { innboks ->
                run {
                    it.setString(1, innboks.eventId)
                    it.addBatch()
                }
            }
            it.executeBatch()
        }

internal fun LocalPostgresDatabase.createInnboks(innboks: List<Innboks>) {
    runBlocking {
        dbQuery { createInnboks(innboks) }
    }
}

internal fun LocalPostgresDatabase.deleteInnboks(innboks: List<Innboks>) {
    runBlocking {
        dbQuery { deleteInnboks(innboks) }
    }
}

internal fun LocalPostgresDatabase.createDoknotStatuses(statuses: List<DoknotifikasjonTestStatus>) = runBlocking {
    dbQuery {
        statuses.forEach { status ->
            createDoknotStatusInnboks(status)
        }
    }
}

internal fun LocalPostgresDatabase.deleteAllDoknotStatusInnboks() = runBlocking {
    dbQuery {
        deleteDoknotStatusInnboks()
    }
}

private fun ZonedDateTime.utcLocalDate()= LocalDateTime.ofInstant(Instant.ofEpochSecond(this.toEpochSecond()), ZoneId.of("UTC"))