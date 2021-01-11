package no.nav.personbruker.dittnav.eventhandler.backup

import Beskjed
import no.nav.personbruker.dittnav.common.test.`with message containing`
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedObjectMother
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.BackupEventException
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should throw`
import org.amshove.kluent.invoking
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

internal class BackupBeskjedTransformerTest {

    @Test
    fun `Skal transformere fra intern Beskjed til Avro-Beskjed`() {
        val beskjedList = getBeskjedList()
        val transformed = BackupBeskjedTransformer.toSchemasBeskjed(1, beskjedList)
        transformed.size `should be equal to` beskjedList.size
    }

    @Test
    fun `Skal kaste exception med informasjon om hvor vi stoppet i transformeringen ved valideringsfeil for Beskjed`() {
        val beskjedList = getBeskjedList()
        beskjedList.add(BeskjedObjectMother.createBeskjed(id = 1, eventId = "123", fodselsnummer = "", synligFremTil = ZonedDateTime.now(),  uid = "123uid", aktiv = true))
        invoking {
            BackupBeskjedTransformer.toSchemasBeskjed(1, beskjedList)
        } `should throw` BackupEventException::class `with message containing` "Vi stoppet på nr 4 (i batch 1) av totalt 4"
    }

    @Test
    fun `Skal transformere til Avro-Done`() {
        val beskjedList = getBeskjedList()
        val transformed = BackupBeskjedTransformer.toSchemasDone(1, beskjedList)
        transformed.size `should be equal to` beskjedList.size
    }

    @Test
    fun `Skal kaste exception med informasjon om hvor vi stoppet i transformeringen ved valideringsfeil for Done`() {
        val beskjedList = getBeskjedList()
        beskjedList.add(BeskjedObjectMother.createBeskjed(id = 1, eventId = "123", fodselsnummer = "", synligFremTil = ZonedDateTime.now(),  uid = "123uid", aktiv = true))
        invoking {
            BackupBeskjedTransformer.toSchemasDone(1, beskjedList)
        } `should throw` BackupEventException::class `with message containing` "Vi stoppet på nr 4 (i batch 1) av totalt 4"
    }

    private fun getBeskjedList(): MutableList<Beskjed> {
        return mutableListOf(
                BeskjedObjectMother.createBeskjed(1, "1", "12345678901", null, "123", true),
                BeskjedObjectMother.createBeskjed(2, "2", "23456789012", ZonedDateTime.now().minusDays(2), "124", true),
                BeskjedObjectMother.createBeskjed(3, "3", "12345678901", null, "125", true))
    }
}
