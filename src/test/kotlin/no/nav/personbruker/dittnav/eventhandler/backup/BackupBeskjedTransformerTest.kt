package no.nav.personbruker.dittnav.eventhandler.backup

import Beskjed
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedObjectMother
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

class BackupBeskjedTransformerTest {

    private val beskjedTransformer = BackupBeskjedTransformer()

    @Test
    fun `Skal transformere fra intern Beskjed til Avro-Beskjed`() {
        val beskjedList = getBeskjedList()
        val avroBeskjed = beskjedTransformer.toSchemasBeskjed(1, beskjedList)
        avroBeskjed.size `should be equal to` beskjedList.size
    }

    fun getBeskjedList(): MutableList<Beskjed> {
        return mutableListOf(
                BeskjedObjectMother.createBeskjed(1, "1", "12345678901", null, "123", true),
                BeskjedObjectMother.createBeskjed(2, "2", "23456789012", ZonedDateTime.now().minusDays(2), "124", true),
                BeskjedObjectMother.createBeskjed(3, "3", "12345678901", null, "125", true))
    }
}
