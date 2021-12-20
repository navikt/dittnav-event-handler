package no.nav.personbruker.dittnav.eventhandler.backup

import kotlinx.coroutines.runBlocking
import no.nav.brukernotifikasjon.schemas.builders.exception.FieldValidationException
import no.nav.personbruker.dittnav.eventhandler.common.`with message containing`
import no.nav.personbruker.dittnav.eventhandler.oppgave.OppgaveObjectMother
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should throw`
import org.amshove.kluent.invoking
import org.junit.jupiter.api.Test

internal class oppgaveCreatorTest {

    private val fodselsnummer = "12345678901"
    private val eventId = "11"
    private val systembruker = "x-dittnav"
    private val link = "https://dummy.nav.no"
    private val sikkerhetsnivaa = 4
    private val grupperingsId = "012"
    private val tekst = "tekst"

    @Test
    fun `should create oppgave-event`() {
        val oppgave = OppgaveObjectMother.createOppgave(1, eventId, fodselsnummer, systembruker, tekst, grupperingsId, link, sikkerhetsnivaa)
        runBlocking {
            val oppgaveEvent = createOppgaveEvent(oppgave)
            oppgaveEvent.getFodselsnummer() `should be equal to` fodselsnummer
            oppgaveEvent.getGrupperingsId() `should be equal to` grupperingsId
            oppgaveEvent.getLink() `should be equal to` link
            oppgaveEvent.getSikkerhetsnivaa() `should be equal to` sikkerhetsnivaa
            oppgaveEvent.getTekst() `should be equal to` tekst
        }
    }

    @Test
    fun `should create oppgave-key`() {
        val oppgave = OppgaveObjectMother.createOppgave(1, eventId, fodselsnummer, systembruker, tekst, grupperingsId, link, sikkerhetsnivaa)
        runBlocking {
            val keyEvent = createKeyForEvent(oppgave.eventId, oppgave.systembruker)
            keyEvent.getEventId() `should be equal to` eventId
            keyEvent.getSystembruker() `should be equal to` systembruker
        }
    }

    @Test
    fun `should throw exception if systembruker is empty`() {
        val oppgave = OppgaveObjectMother.createOppgave(1, eventId, fodselsnummer, "", tekst, grupperingsId, link, sikkerhetsnivaa)

        invoking {
            runBlocking {
                createKeyForEvent(oppgave.eventId, oppgave.systembruker)
            }
        } `should throw` FieldValidationException::class `with message containing` "systembruker"
    }


    @Test
    fun `do not allow too long systembruker`() {
        val tooLongSystembruker = "P".repeat(101)
        val oppgave = OppgaveObjectMother.createOppgave(1, eventId, fodselsnummer, tooLongSystembruker, tekst, grupperingsId, link, sikkerhetsnivaa)

        invoking {
            runBlocking {
                createKeyForEvent(oppgave.eventId, oppgave.systembruker)
            }
        } `should throw` FieldValidationException::class `with message containing` "systembruker"
    }

    @Test
    fun `do not allow too long tekst`() {
        val tooLongText = "T".repeat(501)
        val oppgave = OppgaveObjectMother.createOppgave(1, eventId, fodselsnummer, systembruker, tooLongText, grupperingsId, link, sikkerhetsnivaa)

        invoking {
            runBlocking {
                createOppgaveEvent(oppgave)
            }
        } `should throw` FieldValidationException::class `with message containing` "tekst"
    }

    @Test
    fun `do not allow too long fodselsnummer`() {
        val tooLongFnr = "1".repeat(12)
        val oppgave = OppgaveObjectMother.createOppgave(1, eventId, tooLongFnr, systembruker, tekst, grupperingsId, link, sikkerhetsnivaa)
        invoking {
            runBlocking {
                val oppgaveEvent = createOppgaveEvent(oppgave)
                oppgaveEvent.getFodselsnummer() `should be equal to` fodselsnummer
            }
        } `should throw` FieldValidationException::class `with message containing` "fodselsnummer"
    }

    @Test
    fun `do not allow invalid sikkerhetsnivaa`() {
        val invalidSikkerhetsnivaa = 2
        val oppgave = OppgaveObjectMother.createOppgave(1, eventId, fodselsnummer, systembruker, tekst, grupperingsId, link, invalidSikkerhetsnivaa)
        invoking {
            runBlocking {
                val oppgaveEvent = createOppgaveEvent(oppgave)
                oppgaveEvent.getFodselsnummer() `should be equal to` fodselsnummer
            }
        } `should throw` FieldValidationException::class `with message containing` "Sikkerhetsnivaa"
    }

}
