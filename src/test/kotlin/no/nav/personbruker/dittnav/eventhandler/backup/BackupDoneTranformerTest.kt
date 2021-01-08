package no.nav.personbruker.dittnav.eventhandler.backup

import no.nav.personbruker.dittnav.eventhandler.done.Done
import no.nav.personbruker.dittnav.eventhandler.done.DoneObjectMother
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test

class BackupDoneTranformerTest {

    private val doneTransformer = BackupDoneTranformer()

    @Test
    fun `Skal transformere fra intern Done til Avro-Done`() {
        val beskjedList = getDoneList()
        val avroDone = doneTransformer.toSchemasDone(1, beskjedList)
        avroDone.size `should be equal to` beskjedList.size
    }

    fun getDoneList(): MutableList<Done> {
        return mutableListOf(
                DoneObjectMother.createDone("1", "12345678901"),
                DoneObjectMother.createDone("2", "12345678901"),
                DoneObjectMother.createDone("3", "23456789012"))
    }

}
