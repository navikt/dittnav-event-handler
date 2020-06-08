package no.nav.personbruker.dittnav.eventhandler.beskjed

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.BackupEventException
import no.nav.personbruker.dittnav.eventhandler.done.createKeyForEvent
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should throw`
import org.amshove.kluent.invoking
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.time.ZoneId
import java.time.ZonedDateTime

class beskjedCreator {
    private val fodselsnummer = "123"
    private val eventId = "11"
    private val systembruker = "x-dittnav"
    private val link = "testlink"
    private val sikkerhetsnivaa = 4
    private val grupperingsId = "012"
    private val tekst = "tekst"
    private val zonedDateTime = ZonedDateTime.now(ZoneId.of("Europe/Oslo"))

    private val log = LoggerFactory.getLogger(BeskjedProducer::class.java)

    @Test
    fun `should create beskjed-event`() {
        val beskjed = BeskjedObjectMother.createBeskjed(1, eventId, fodselsnummer, systembruker, tekst, grupperingsId, link, sikkerhetsnivaa, zonedDateTime)
        runBlocking {
            val beskjedEvent = createBeskjedEvent(beskjed)
            beskjedEvent.getFodselsnummer() `should be equal to` fodselsnummer
            beskjedEvent.getGrupperingsId() `should be equal to` grupperingsId
            beskjedEvent.getLink() `should be equal to` link
            beskjedEvent.getSikkerhetsnivaa() `should be equal to` sikkerhetsnivaa
            beskjedEvent.getTekst() `should be equal to` tekst
        }
    }

    @Test
    fun `should create beskjed-key`() {
        val beskjed = BeskjedObjectMother.createBeskjed(1, eventId, fodselsnummer, systembruker, tekst, grupperingsId, link, sikkerhetsnivaa, zonedDateTime)
        runBlocking {
            val keyEvent = createKeyForEvent(beskjed.eventId, beskjed.systembruker)
            keyEvent.getEventId() `should be equal to` eventId
            keyEvent.getSystembruker() `should be equal to` systembruker
        }
    }

    @Test
    fun `should throw exception if systembruker is empty`() {
        val beskjed = BeskjedObjectMother.createBeskjed(1, eventId, fodselsnummer, "", tekst, grupperingsId, link, sikkerhetsnivaa, zonedDateTime)

        invoking {
            runBlocking {
                val key = createKeyForEvent(beskjed.eventId, beskjed.systembruker)
            }
        } `should throw` BackupEventException::class
    }


    @Test
    fun `do not allow too long systembruker`() {
        val tooLongSystembruker = "P".repeat(101)
        val beskjed = BeskjedObjectMother.createBeskjed(1, eventId, fodselsnummer, tooLongSystembruker, tekst, grupperingsId, link, sikkerhetsnivaa, zonedDateTime)

        invoking {
            runBlocking {
                val key = createKeyForEvent(beskjed.eventId, beskjed.systembruker)
            }
        } `should throw` BackupEventException::class
    }

    @Test
    fun `do not allow too long tekst`() {
        val tooLongText = "T".repeat(501)
        val beskjed = BeskjedObjectMother.createBeskjed(1, eventId, fodselsnummer, systembruker, tooLongText, grupperingsId, link, sikkerhetsnivaa, zonedDateTime)

        invoking {
            runBlocking {
                val beskjedEvent = createBeskjedEvent(beskjed)
            }
        } `should throw` BackupEventException::class
    }

    @Test
    fun `do not allow too long fodselsnummer`() {
        val tooLongFnr = "1".repeat(12)
        val beskjed = BeskjedObjectMother.createBeskjed(1, eventId, tooLongFnr, systembruker, tekst, grupperingsId, link, sikkerhetsnivaa, zonedDateTime)
        invoking {
            runBlocking {
                val beskjedEvent = createBeskjedEvent(beskjed)
                beskjedEvent.getFodselsnummer() `should be equal to` fodselsnummer
            }
        } `should throw` BackupEventException::class
    }

    @Test
    fun `do not allow invalid sikkerhetsnivaa`() {
        val invalidSikkerhetsnivaa = 2
        val beskjed = BeskjedObjectMother.createBeskjed(1, eventId, fodselsnummer, systembruker, tekst, grupperingsId, link, invalidSikkerhetsnivaa, zonedDateTime)
        invoking {
            runBlocking {
                val beskjedEvent = createBeskjedEvent(beskjed)
                beskjedEvent.getFodselsnummer() `should be equal to` fodselsnummer
            }
        } `should throw` BackupEventException::class
    }

    @Test
    fun `should convert date to EpocMilli`() {
        val expectedDate = zonedDateTime.toInstant().toEpochMilli()
        val beskjed = BeskjedObjectMother.createBeskjed(1, eventId, fodselsnummer, systembruker, tekst, grupperingsId, link, sikkerhetsnivaa, zonedDateTime)
        runBlocking {
            val beskjedEvent = createBeskjedEvent(beskjed)
            beskjedEvent.getTidspunkt() `should be equal to` expectedDate
        }
    }
}
