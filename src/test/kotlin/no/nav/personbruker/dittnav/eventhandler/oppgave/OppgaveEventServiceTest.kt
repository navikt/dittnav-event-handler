package no.nav.personbruker.dittnav.eventhandler.oppgave

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.brukernotifikasjon.schemas.builders.exception.FieldValidationException
import no.nav.personbruker.dittnav.eventhandler.common.TokenXUserObjectMother
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OppgaveEventServiceTest {

    private val database = mockk<Database>()
    private val oppgaveEventService = OppgaveEventService(database)
    private val bruker = TokenXUserObjectMother.createInnloggetBruker("123")
    private val produsent = "dittnav"
    private val grupperingsid = "100${bruker.ident}"

    @Test
    fun `Should return all events that are grouped together by ids`() {
        val innloggetbruker = TokenXUserObjectMother.createInnloggetBruker("100")
        val grupperingsid = "100${innloggetbruker.ident}"
        val oppgaveEvents = listOf(
            OppgaveObjectMother.createOppgave(fodselsnummer = innloggetbruker.ident, grupperingsId = grupperingsid),
            OppgaveObjectMother.createOppgave(fodselsnummer = innloggetbruker.ident, grupperingsId = grupperingsid)
        )
        runBlocking {
            coEvery {
                database.queryWithExceptionTranslation<List<Oppgave>>(any())
            }.returns(oppgaveEvents)

            val actualOppgaveEvents = oppgaveEventService.getAllGroupedEventsFromCacheForUser(innloggetbruker, grupperingsid, produsent)
            actualOppgaveEvents.size shouldBe 2
        }
    }

    @Test
    fun `Kaster FieldValidationException hvis grupperingsid er null`() {
        val grupperingsidSomErNull = null
        shouldThrow<FieldValidationException> {
            runBlocking {
                oppgaveEventService.getAllGroupedEventsFromCacheForUser(bruker, grupperingsidSomErNull, produsent)
            }
        }
    }

    @Test
    fun `Kaster FieldValidationException hvis grupperingsid er tomt`() {
        val grupperingsidSomErTom = ""
        shouldThrow<FieldValidationException> {
            runBlocking {
                oppgaveEventService.getAllGroupedEventsFromCacheForUser(bruker, grupperingsidSomErTom, produsent)
            }
        }
    }

    @Test
    fun `Kaster FieldValidationException hvis grupperingsid er for lang`() {
        val grupperingsidForLang = "g".repeat(101)
        shouldThrow<FieldValidationException> {
            runBlocking {
                oppgaveEventService.getAllGroupedEventsFromCacheForUser(bruker, grupperingsidForLang, produsent)
            }
        }
    }

    @Test
    fun `Kaster FieldValidationException hvis produsent er null`() {
        val produsentSomErNull = null
        shouldThrow<FieldValidationException> {
            runBlocking {
                oppgaveEventService.getAllGroupedEventsFromCacheForUser(bruker, grupperingsid, produsentSomErNull)
            }
        }
    }

    @Test
    fun `Kaster FieldValidationException hvis produsent er tomt`() {
        val produsentSomErTom = ""
        shouldThrow<FieldValidationException> {
            runBlocking {
                oppgaveEventService.getAllGroupedEventsFromCacheForUser(bruker, grupperingsid, produsentSomErTom)
            }
        }
    }

    @Test
    fun `Kaster FieldValidationException hvis produsent er for lang`() {
        val produsentForLang = "p".repeat(101)
        shouldThrow<FieldValidationException> {
            runBlocking {
                oppgaveEventService.getAllGroupedEventsFromCacheForUser(bruker, grupperingsid, produsentForLang)
            }
        }
    }
}
