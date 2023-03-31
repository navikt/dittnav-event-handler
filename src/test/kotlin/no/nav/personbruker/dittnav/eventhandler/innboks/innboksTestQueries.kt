package no.nav.personbruker.dittnav.eventhandler.innboks

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.database.LocalPostgresDatabase
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.createEksternVarslingStatusInnboks
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.deleteEksternVarslingStatusInnboks
import java.sql.Connection
import java.sql.Types
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

fun Connection.createInnboks(innbokser: List<Innboks>) =
    prepareStatement(
        """INSERT INTO innboks(systembruker, eventTidspunkt, fodselsnummer, eventId, grupperingsId, tekst, link, sikkerhetsnivaa, sistOppdatert, aktiv, namespace, appnavn, forstBehandlet, eksternVarsling, prefererteKanaler)
            VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"""
    )
        .use {
            innbokser.forEach { innboks ->
                run {
                    it.setString(1, innboks.systembruker)
                    it.setObject(2, innboks.eventTidspunkt.utcLocalDate(), Types.TIMESTAMP)
                    it.setString(3, innboks.fodselsnummer)
                    it.setString(4, innboks.eventId)
                    it.setString(5, innboks.grupperingsId)
                    it.setString(6, innboks.tekst)
                    it.setString(7, innboks.link)
                    it.setInt(8, innboks.sikkerhetsnivaa)
                    it.setObject(9, innboks.sistOppdatert.utcLocalDate(), Types.TIMESTAMP)
                    it.setBoolean(10, innboks.aktiv)
                    it.setString(11, innboks.namespace)
                    it.setString(12, innboks.appnavn)
                    it.setObject(13, innboks.forstBehandlet.utcLocalDate(), Types.TIMESTAMP)
                    it.setBoolean(14, innboks.eksternVarsling != null)
                    it.setString(15, innboks.eksternVarsling?.prefererteKanaler?.joinToString() ?: "")
                    it.addBatch()
                }
            }
            it.executeBatch()
        }

private fun Connection.deleteAllInnboks() = prepareStatement("""DELETE FROM innboks""").execute()

fun LocalPostgresDatabase.createInnboks(innboksList: List<Innboks>) = runBlocking {
    dbQuery {
        createInnboks(innboksList)
    }
    dbQuery {
        createEksternVarslingStatuses(innboksList)
    }
}

fun LocalPostgresDatabase.deleteInnboks() = runBlocking {
    dbQuery {
        deleteEksternVarslingStatusInnboks()
    }
    dbQuery {
        deleteAllInnboks()
    }
}

fun LocalPostgresDatabase.createEksternVarslingStatuses(statuses: List<Innboks>) = runBlocking {
    dbQuery {
        statuses.forEach { status ->
            createEksternVarslingStatusInnboks(status)
        }
    }
}

private fun ZonedDateTime.utcLocalDate()= LocalDateTime.ofInstant(Instant.ofEpochSecond(this.toEpochSecond()), ZoneId.of("UTC"))
