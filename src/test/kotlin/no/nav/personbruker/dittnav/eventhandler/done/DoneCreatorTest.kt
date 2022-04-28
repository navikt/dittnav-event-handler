package no.nav.personbruker.dittnav.eventhandler.done

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.UUID

internal class doneCreatorTest {

    private val fodselsnummer = "12345678901"
    private val grupperingsId = "789"
    private val eventId = UUID.randomUUID().toString()
    private val eventTidspunkt = ZonedDateTime.now(ZoneOffset.UTC)
    private val namespace = "local"
    private val appnavn = "eventhandler"

    @Test
    fun `should create done-event`() {
        runBlocking {
            val doneEvent = createDoneEvent(eventTidspunkt)
            doneEvent.getTidspunkt() shouldBe eventTidspunkt.toInstant().toEpochMilli()
        }
    }

    @Test
    fun `should create done-key`() {
        runBlocking {
            val doneNokkel = createKeyForEvent(eventId, grupperingsId, fodselsnummer, namespace, appnavn)
            doneNokkel.getEventId() shouldBe eventId
            doneNokkel.getGrupperingsId() shouldBe grupperingsId
            doneNokkel.getFodselsnummer() shouldBe fodselsnummer
            doneNokkel.getNamespace() shouldBe namespace
            doneNokkel.getAppnavn() shouldBe appnavn
        }
    }
}
