package no.nav.personbruker.dittnav.eventhandler.done

import kotlinx.coroutines.runBlocking
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

class doneCreatorTest {

    private val fodselsnummer = "12345678901"
    private val grupperingsId = "789"
    private val eventId = "11"
    private val produsent = "DittNAV"
    private val eventTidspunkt = ZonedDateTime.now(ZoneOffset.UTC)

    @Test
    fun `should create done-event`() {
        runBlocking {
            val doneEvent = createDoneEvent(fodselsnummer, grupperingsId)
            doneEvent.getFodselsnummer() `should be equal to` fodselsnummer
            doneEvent.getGrupperingsId() `should be equal to` grupperingsId
        }
    }

    @Test
    fun `should create done-key`() {
        runBlocking {
            val doneNnokkel = createKeyForEvent(eventId, produsent)
            doneNnokkel.getEventId() `should be equal to` eventId
            doneNnokkel.getSystembruker() `should be equal to` produsent
        }
    }

    @Test
    fun `should create backupDone-event`() {
        runBlocking {
            val doneEvent = createBackupDoneEvent(fodselsnummer, grupperingsId, eventTidspunkt)
            doneEvent.getFodselsnummer() `should be equal to` fodselsnummer
            doneEvent.getGrupperingsId() `should be equal to` grupperingsId
            doneEvent.getTidspunkt() `should be equal to` eventTidspunkt.toLocalDateTime().toEpochSecond(ZoneOffset.UTC)
        }
    }
}
