package no.nav.personbruker.dittnav.eventhandler.backup

import no.nav.personbruker.dittnav.eventhandler.common.`with message containing`
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.BackupEventException
import no.nav.personbruker.dittnav.eventhandler.done.Done
import no.nav.personbruker.dittnav.eventhandler.done.DoneObjectMother
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should throw`
import org.amshove.kluent.invoking
import org.junit.jupiter.api.Test

internal class BackupDoneTranformerTest {
    @Test
    fun `Skal transformere fra intern Done til Avro-Done`() {
        val beskjedList = getDoneList()
        val avroDone = BackupDoneTranformer.toSchemasDone(1, beskjedList)
        avroDone.size `should be equal to` beskjedList.size
    }

    @Test
    fun `Skal kaste exception med informasjon om hvor vi stoppet i transformeringen ved valideringsfeil for Done`() {
        val doneList = getDoneList()
        doneList.add(DoneObjectMother.createDone(eventId = "123", fodselsnummer = ""))
        invoking {
            BackupDoneTranformer.toSchemasDone(1, doneList)
        } `should throw` BackupEventException::class `with message containing` "Vi stoppet på nr 4 (i batch 1) av totalt 4"
    }

    private fun getDoneList(): MutableList<Done> {
        return mutableListOf(
                DoneObjectMother.createDone("1", "12345678901"),
                DoneObjectMother.createDone("2", "12345678901"),
                DoneObjectMother.createDone("3", "23456789012"))
    }
}
