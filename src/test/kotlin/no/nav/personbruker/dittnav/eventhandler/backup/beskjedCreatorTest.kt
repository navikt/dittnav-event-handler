package no.nav.personbruker.dittnav.eventhandler.beskjed

import kotlinx.coroutines.runBlocking
import no.nav.brukernotifikasjon.schemas.builders.exception.FieldValidationException
import no.nav.personbruker.dittnav.common.test.`with message containing`
import no.nav.personbruker.dittnav.eventhandler.backup.createBeskjedEvent
import no.nav.personbruker.dittnav.eventhandler.backup.createKeyForEvent
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should throw`
import org.amshove.kluent.invoking
import org.junit.jupiter.api.Test
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

class beskjedCreatorTest {
    private val fodselsnummer = "12345678901"
    private val eventId = "11"
    private val systembruker = "x-dittnav"
    private val link = "https://dummy.nav.no"
    private val sikkerhetsnivaa = 4
    private val grupperingsId = "012"
    private val tekst = "tekst"
    private val zonedDateTime = ZonedDateTime.now(ZoneId.of("Europe/Oslo"))

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
                createKeyForEvent(beskjed.eventId, beskjed.systembruker)
            }
        } `should throw` FieldValidationException::class `with message containing` "systembruker"
    }


    @Test
    fun `do not allow too long systembruker`() {
        val tooLongSystembruker = "P".repeat(101)
        val beskjed = BeskjedObjectMother.createBeskjed(1, eventId, fodselsnummer, tooLongSystembruker, tekst, grupperingsId, link, sikkerhetsnivaa, zonedDateTime)

        invoking {
            runBlocking {
                createKeyForEvent(beskjed.eventId, beskjed.systembruker)
            }
        } `should throw` FieldValidationException::class `with message containing` "systembruker"
    }

    @Test
    fun `do not allow too long tekst`() {
        val tooLongText = "T".repeat(501)
        val beskjed = BeskjedObjectMother.createBeskjed(1, eventId, fodselsnummer, systembruker, tooLongText, grupperingsId, link, sikkerhetsnivaa, zonedDateTime)

        invoking {
            runBlocking {
                createBeskjedEvent(beskjed)
            }
        } `should throw` FieldValidationException::class `with message containing` "tekst"
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
        } `should throw` FieldValidationException::class `with message containing` "fodselsnummer"
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
        } `should throw` FieldValidationException::class `with message containing` "Sikkerhetsnivaa"
    }

    @Test
    fun `should convert ZonedDateTime to LocalDateTime`() {
        val expectedDate = zonedDateTime.toLocalDateTime().toInstant(ZoneOffset.UTC).toEpochMilli()
        val beskjed = BeskjedObjectMother.createBeskjed(1, eventId, fodselsnummer, systembruker, tekst, grupperingsId, link, sikkerhetsnivaa, zonedDateTime)
        runBlocking {
            val beskjedEvent = createBeskjedEvent(beskjed)
            beskjedEvent.getTidspunkt() `should be equal to` expectedDate
        }
    }
}
