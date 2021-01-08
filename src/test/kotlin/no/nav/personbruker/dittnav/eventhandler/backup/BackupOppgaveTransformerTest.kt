package no.nav.personbruker.dittnav.eventhandler.backup

import no.nav.personbruker.dittnav.eventhandler.oppgave.Oppgave
import no.nav.personbruker.dittnav.eventhandler.oppgave.OppgaveObjectMother
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test

internal class BackupOppgaveTransformerTest {

    private val oppgaveTransformer = BackupOppgaveTransformer()

    @Test
    fun `Skal transformere fra intern Oppgave til Avro-Oppgave`() {
        val oppgaveList = getOppgaveList()
        val avroOppgave = oppgaveTransformer.toSchemasOppgave(1, oppgaveList)
        avroOppgave.size `should be equal to` oppgaveList.size
    }

    fun getOppgaveList(): MutableList<Oppgave> {
        return mutableListOf(
                OppgaveObjectMother.createOppgave(1, "1", "12345678901", true),
                OppgaveObjectMother.createOppgave(2, "2", "23456789012", true),
                OppgaveObjectMother.createOppgave(3, "3", "12345678901", true))
    }
}
