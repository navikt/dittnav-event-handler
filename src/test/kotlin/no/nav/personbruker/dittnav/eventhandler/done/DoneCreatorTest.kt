package no.nav.personbruker.dittnav.eventhandler.done

import kotlinx.coroutines.runBlocking
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*

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
            doneEvent.getTidspunkt() `should be equal to` eventTidspunkt.toInstant().toEpochMilli()
        }
    }

    @Test
    fun `should create done-key`() {
        runBlocking {
            val doneNokkel = createKeyForEvent(eventId, grupperingsId, fodselsnummer, namespace, appnavn)
            doneNokkel.getEventId() `should be equal to` eventId
            doneNokkel.getGrupperingsId() `should be equal to` grupperingsId
            doneNokkel.getFodselsnummer() `should be equal to` fodselsnummer
            doneNokkel.getNamespace() `should be equal to` namespace
            doneNokkel.getAppnavn() `should be equal to` appnavn
        }
    }
}
