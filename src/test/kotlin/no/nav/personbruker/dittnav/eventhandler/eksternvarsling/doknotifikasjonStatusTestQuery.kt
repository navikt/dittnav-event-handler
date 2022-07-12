package no.nav.personbruker.dittnav.eventhandler.eksternvarsling

import java.sql.Connection

private fun insertQuery(type: String) = """
    insert into doknotifikasjon_status_$type (eventId, status, melding, distribusjonsId, kanaler, antall_oppdateringer)
        values (?, ?, ?, ?, ?, 1)
""".trimIndent()

private val insertStatusBeskjed = insertQuery("beskjed")
private val insertStatusOppgave = insertQuery("oppgave")
private val insertStatusInnboks = insertQuery("innboks")

fun Connection.createDoknotStatusBeskjed(status: DoknotifikasjonStatusDto) {
    prepareStatement(insertStatusBeskjed).use {
        it.setString(1, status.eventId)
        it.setString(2, status.status)
        it.setString(3, status.melding)
        it.setObject(4, status.distribusjonsId)
        it.setString(5, status.kanaler)
        it.executeUpdate()
    }
}

fun Connection.createDoknotStatusOppgave(status: DoknotifikasjonStatusDto) {
    prepareStatement(insertStatusOppgave).use {
        it.setString(1, status.eventId)
        it.setString(2, status.status)
        it.setString(3, status.melding)
        it.setObject(4, status.distribusjonsId)
        it.setString(5, status.kanaler)
        it.executeUpdate()
    }
}

fun Connection.createDoknotStatusInnboks(status: DoknotifikasjonStatusDto) {
    prepareStatement(insertStatusInnboks).use {
        it.setString(1, status.eventId)
        it.setString(2, status.status)
        it.setString(3, status.melding)
        it.setObject(4, status.distribusjonsId)
        it.setString(5, status.kanaler)
        it.executeUpdate()
    }
}

fun Connection.deleteDoknotStatusBeskjed() {
    prepareStatement("delete from doknotifikasjon_status_beskjed").executeUpdate()
}

fun Connection.deleteDoknotStatusOppgave() {
    prepareStatement("delete from doknotifikasjon_status_oppgave").executeUpdate()
}

fun Connection.deleteDoknotStatusInnboks() {
    prepareStatement("delete from doknotifikasjon_status_innboks").executeUpdate()
}
