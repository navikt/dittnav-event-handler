package no.nav.personbruker.dittnav.eventhandler.backup

import no.nav.personbruker.dittnav.common.test.`with message containing`
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.BackupEventException
import no.nav.personbruker.dittnav.eventhandler.oppgave.Oppgave
import no.nav.personbruker.dittnav.eventhandler.oppgave.OppgaveObjectMother
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should throw`
import org.amshove.kluent.invoking
import org.junit.jupiter.api.Test

internal class BackupOppgaveTransformerTest {

    private val oppgaveTransformer = BackupOppgaveTransformer()

    @Test
    fun `Skal transformere fra intern Oppgave til Avro-Oppgave`() {
        val oppgaveList = getOppgaveList()
        val avroOppgave = oppgaveTransformer.toSchemasOppgave(1, oppgaveList)
        avroOppgave.size `should be equal to` oppgaveList.size
    }

    @Test
    fun `Skal kaste exception med informasjon om hvor vi stoppet i transformeringen ved valideringsfeil for Oppgave`() {
        val oppgaveList = getOppgaveList()
        oppgaveList.add(OppgaveObjectMother.createOppgave(id = 1, eventId = "123", fodselsnummer = "", aktiv = true))
        invoking {
            oppgaveTransformer.toSchemasOppgave(1, oppgaveList)
        } `should throw` BackupEventException::class `with message containing` "Vi stoppet på nr 4 (i batch nr. 1) av totalt 4"
    }

    @Test
    fun `Skal transformere til Avro-Done`() {
        val oppgaveList = getOppgaveList()
        val transformed = oppgaveTransformer.toSchemasDone(1, oppgaveList)
        transformed.size `should be equal to` oppgaveList.size
    }

    @Test
    fun `Skal kaste exception med informasjon om hvor vi stoppet i transformeringen ved valideringsfeil for Done`() {
        val oppgaveList = getOppgaveList()
        oppgaveList.add(OppgaveObjectMother.createOppgave(id = 1, eventId = "123", fodselsnummer = "", aktiv = true))
        invoking {
            oppgaveTransformer.toSchemasDone(1, oppgaveList)
        } `should throw` BackupEventException::class `with message containing` "Vi stoppet på nr 4 (i batch nr. 1) av totalt 4"
    }

    private fun getOppgaveList(): MutableList<Oppgave> {
        return mutableListOf(
                OppgaveObjectMother.createOppgave(1, "1", "12345678901", true),
                OppgaveObjectMother.createOppgave(2, "2", "23456789012", true),
                OppgaveObjectMother.createOppgave(3, "3", "12345678901", true))
    }
}
