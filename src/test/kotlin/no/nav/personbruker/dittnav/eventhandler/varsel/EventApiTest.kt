package no.nav.personbruker.dittnav.eventhandler.varsel

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.server.testing.*
import no.nav.personbruker.dittnav.eventhandler.apiTestfnr
import no.nav.personbruker.dittnav.eventhandler.asDateTime
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedObjectMother
import no.nav.personbruker.dittnav.eventhandler.beskjed.createBeskjed
import no.nav.personbruker.dittnav.eventhandler.common.database.LocalPostgresDatabase
import no.nav.personbruker.dittnav.eventhandler.comparableTime
import no.nav.personbruker.dittnav.eventhandler.innboks.InnboksObjectMother
import no.nav.personbruker.dittnav.eventhandler.innboks.createInnboks
import no.nav.personbruker.dittnav.eventhandler.mockEventHandlerApi
import no.nav.personbruker.dittnav.eventhandler.oppgave.OppgaveObjectMother
import no.nav.personbruker.dittnav.eventhandler.oppgave.createOppgave
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EventApiTest {

    private val database = LocalPostgresDatabase.cleanDb()
    private val varselRepository = VarselRepository(database)
    private val fodselsnummer = apiTestfnr
    private val aktivBeskjed =
        BeskjedObjectMother.createBeskjed(eventId = "765322", fodselsnummer = fodselsnummer, aktiv = true,)
    private val inaktivBeskjed =
        BeskjedObjectMother.createBeskjed(eventId = "7666622", fodselsnummer = fodselsnummer, aktiv = false,)

    private val antallaktiveVarselForFnr = 2
    private val antallinaktiveVarselForFnr = 4


    @BeforeAll
    fun populate() {
        database.apply {
            createBeskjed(
                listOf(
                    aktivBeskjed,
                    inaktivBeskjed,
                    BeskjedObjectMother.createBeskjed(fodselsnummer = "123", aktiv = true,)
                )
            )
            createOppgave(
                listOf(
                    OppgaveObjectMother.createOppgave(fodselsnummer = fodselsnummer, aktiv = false, fristUtløpt = null),
                    OppgaveObjectMother.createOppgave(fodselsnummer = "321", aktiv = false, fristUtløpt = null)
                )
            )
            createInnboks(
                listOf(
                    InnboksObjectMother.createInnboks(aktiv = true, fodselsnummer = fodselsnummer),
                    InnboksObjectMother.createInnboks(aktiv = true, fodselsnummer = "6655"),
                    InnboksObjectMother.createInnboks(aktiv = false, fodselsnummer = fodselsnummer),
                    InnboksObjectMother.createInnboks(aktiv = false, fodselsnummer = fodselsnummer)
                )
            )
        }
    }


    @Test
    fun `event-apiet skal returnere inaktive varsler`() {

        testApplication {
            mockEventHandlerApi(eventRepository = varselRepository)
            val response = client.get("dittnav-event-handler/fetch/event/inaktive")

            response.status shouldBe HttpStatusCode.OK
            val eventListe = ObjectMapper().readTree(response.bodyAsText())
            eventListe.size() shouldBe antallinaktiveVarselForFnr

            val varselJson = eventListe.find { it["eventId"].asText() == inaktivBeskjed.eventId }
            require(varselJson != null)
            varselJson["grupperingsId"].asText() shouldBe inaktivBeskjed.grupperingsId
            varselJson["eventId"].asText() shouldBe inaktivBeskjed.eventId
            varselJson["eventTidspunkt"].asDateTime() shouldBe inaktivBeskjed.eventTidspunkt.comparableTime()
            varselJson["produsent"].asText() shouldBe inaktivBeskjed.appnavn
            varselJson["sikkerhetsnivaa"].asInt() shouldBe inaktivBeskjed.sikkerhetsnivaa
            varselJson["sistOppdatert"].asDateTime() shouldBe inaktivBeskjed.sistOppdatert.comparableTime()
            varselJson["tekst"].asText() shouldBe inaktivBeskjed.tekst
            varselJson["link"].asText() shouldBe inaktivBeskjed.link
            varselJson["aktiv"].asBoolean() shouldBe false
            varselJson["forstBehandlet"].asDateTime() shouldBe inaktivBeskjed.forstBehandlet.comparableTime()
            varselJson["type"].asText() shouldBe "BESKJED"
        }
    }

    @Test
    fun `event-apiet skal returnere aktive varsler`() {
        testApplication {
            mockEventHandlerApi(eventRepository = varselRepository)
            val response = client.get("dittnav-event-handler/fetch/event/aktive")

            response.status shouldBe HttpStatusCode.OK
            val eventListe = ObjectMapper().readTree(response.bodyAsText())
            eventListe.size() shouldBe antallaktiveVarselForFnr

            val eventJson = eventListe.find { it["eventId"].asText() == aktivBeskjed.eventId }
            require(eventJson != null)
            eventJson["grupperingsId"].asText() shouldBe aktivBeskjed.grupperingsId
            eventJson["eventId"].asText() shouldBe aktivBeskjed.eventId
            eventJson["eventTidspunkt"].asDateTime() shouldBe aktivBeskjed.eventTidspunkt.comparableTime()
            eventJson["produsent"].asText() shouldBe aktivBeskjed.appnavn
            eventJson["sikkerhetsnivaa"].asInt() shouldBe aktivBeskjed.sikkerhetsnivaa
            eventJson["sistOppdatert"].asDateTime() shouldBe aktivBeskjed.sistOppdatert.comparableTime()
            eventJson["tekst"].asText() shouldBe aktivBeskjed.tekst
            eventJson["link"].asText() shouldBe aktivBeskjed.link
            eventJson["aktiv"].asBoolean() shouldBe true
            eventJson["forstBehandlet"].asDateTime() shouldBe aktivBeskjed.forstBehandlet.comparableTime()
            eventJson["type"].asText() shouldBe "BESKJED"
        }
    }
}

