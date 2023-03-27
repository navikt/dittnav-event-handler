package no.nav.personbruker.dittnav.eventhandler.eksternvarsling

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import no.nav.personbruker.dittnav.eventhandler.beskjed.Beskjed
import no.nav.personbruker.dittnav.eventhandler.innboks.Innboks
import no.nav.personbruker.dittnav.eventhandler.oppgave.Oppgave
import org.postgresql.util.PGobject
import java.sql.Connection

private fun insertQuery(type: String) = """
    insert into ekstern_varsling_status_$type (eventId, kanaler, eksternVarslingSendt, renotifikasjonSendt, historikk)
        values (?, ?, ?, ?, ?)
""".trimIndent()

private val insertStatusBeskjed = insertQuery("beskjed")
private val insertStatusOppgave = insertQuery("oppgave")
private val insertStatusInnboks = insertQuery("innboks")

private val objectMapper = jacksonMapperBuilder()
    .addModule(JavaTimeModule())
    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    .build()
    .setSerializationInclusion(JsonInclude.Include.NON_NULL)

fun Connection.createEksternVarslingStatusBeskjed(beskjed: Beskjed) {
    prepareStatement(insertStatusBeskjed).use {
        val eksternVarsling = beskjed.eksternVarsling ?: return

        val historikkBlob = PGobject().apply {
            type = "json"
            value = objectMapper.writeValueAsString(eksternVarsling.historikk)
        }

        it.setString(1, beskjed.eventId)
        it.setString(2, eksternVarsling.sendteKanaler.joinToString())
        it.setBoolean(3, eksternVarsling.sendt)
        it.setBoolean(4, eksternVarsling.renotifikasjonSendt)
        it.setObject(5, historikkBlob)
        it.executeUpdate()
    }
}

fun Connection.createEksternVarslingStatusOppgave(oppgave: Oppgave) {
    prepareStatement(insertStatusOppgave).use {
        val eksternVarsling = oppgave.eksternVarsling ?: return

        val historikkBlob = PGobject().apply {
            type = "json"
            value = objectMapper.writeValueAsString(eksternVarsling.historikk)
        }

        it.setString(1, oppgave.eventId)
        it.setString(2, eksternVarsling.sendteKanaler.joinToString())
        it.setBoolean(3, eksternVarsling.sendt)
        it.setBoolean(4, eksternVarsling.renotifikasjonSendt)
        it.setObject(5, historikkBlob)
        it.executeUpdate()
    }
}

fun Connection.createEksternVarslingStatusInnboks(innboks: Innboks) {
    prepareStatement(insertStatusInnboks).use {
        val eksternVarsling = innboks.eksternVarsling ?: return

        val historikkBlob = PGobject().apply {
            type = "json"
            value = objectMapper.writeValueAsString(eksternVarsling.historikk)
        }

        it.setString(1, innboks.eventId)
        it.setString(2, eksternVarsling.sendteKanaler.joinToString())
        it.setBoolean(3, eksternVarsling.sendt)
        it.setBoolean(4, eksternVarsling.renotifikasjonSendt)
        it.setObject(5, historikkBlob)
        it.executeUpdate()
    }
}

fun Connection.deleteEksternVarslingStatusBeskjed() {
    prepareStatement("delete from ekstern_varsling_status_beskjed").executeUpdate()
}

fun Connection.deleteEksternVarslingStatusOppgave() {
    prepareStatement("delete from ekstern_varsling_status_oppgave").executeUpdate()
}

fun Connection.deleteEksternVarslingStatusInnboks() {
    prepareStatement("delete from ekstern_varsling_status_innboks").executeUpdate()
}
