package no.nav.personbruker.dittnav.eventhandler.varsel;

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.shouldBe
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import no.nav.personbruker.dittnav.eventhandler.apiTestfnr
import no.nav.personbruker.dittnav.eventhandler.asBooleanOrNull
import no.nav.personbruker.dittnav.eventhandler.asDateTime
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedObjectMother
import no.nav.personbruker.dittnav.eventhandler.beskjed.createBeskjed
import no.nav.personbruker.dittnav.eventhandler.common.database.LocalPostgresDatabase
import no.nav.personbruker.dittnav.eventhandler.comparableTime
import no.nav.personbruker.dittnav.eventhandler.getMedFnrHeader
import no.nav.personbruker.dittnav.eventhandler.innboks.InnboksObjectMother
import no.nav.personbruker.dittnav.eventhandler.innboks.createInnboks
import no.nav.personbruker.dittnav.eventhandler.mockEventHandlerApi
import no.nav.personbruker.dittnav.eventhandler.oppgave.OppgaveObjectMother
import no.nav.personbruker.dittnav.eventhandler.oppgave.createOppgave
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VarselApiTest {

    private val database = LocalPostgresDatabase.cleanDb()
    private val varselRepository = VarselRepository(database)
    private val fodselsnummer = apiTestfnr
    private val aktivBeskjed =
        BeskjedObjectMother.createBeskjed(eventId = "765322", fodselsnummer = fodselsnummer, aktiv = true)
    private val inaktivBeskjed =
        BeskjedObjectMother.createBeskjed(
            eventId = "7666622",
            aktiv = false,
            fodselsnummer = fodselsnummer,
            fristUtløpt = false
        )
    private val inaktivOppgave =
        OppgaveObjectMother.createOppgave(
            eventId = "88",
            aktiv = false,
            fodselsnummer = fodselsnummer,
            fristUtløpt = true
        )


    private val antallaktiveVarselForFnr = 2
    private val antallinaktiveVarselForFnr = 4


    @BeforeAll
    fun populate() {
        database.apply {
            createBeskjed(
                listOf(
                    aktivBeskjed,
                    inaktivBeskjed,
                    BeskjedObjectMother.createBeskjed(fodselsnummer = "123", aktiv = true)
                )
            )
            createOppgave(
                listOf(
                    inaktivOppgave,
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
    fun `varsel-apiet skal returnere inaktive varsler`() =
        testApplication {
            mockEventHandlerApi(eventRepository = varselRepository)
            val response =
                client.getMedFnrHeader("dittnav-event-handler/fetch/varsel/on-behalf-of/inaktive", fodselsnummer)

            response.status shouldBe HttpStatusCode.OK
            val varselListe = ObjectMapper().readTree(response.bodyAsText())
            varselListe.size() shouldBe antallinaktiveVarselForFnr

            val varselJson = varselListe.find { it["eventId"].asText() == inaktivBeskjed.eventId }
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
            varselJson["fristUtløpt"].asBoolean() shouldBe false
            varselListe.find { it["eventId"].asText() == inaktivOppgave.eventId }.apply {
                require(this != null)
                this.get("fristUtløpt").asBooleanOrNull() shouldBe true
            }
        }


    @Test
    fun `varsel-apiet skal returnere aktive varsler`() {
        testApplication {
            mockEventHandlerApi(eventRepository = varselRepository)
            val response =
                client.getMedFnrHeader("dittnav-event-handler/fetch/varsel/on-behalf-of/aktive", fodselsnummer)

            response.status shouldBe HttpStatusCode.OK
            val varselListe = ObjectMapper().readTree(response.bodyAsText())
            varselListe.size() shouldBe antallaktiveVarselForFnr

            val varselJson = varselListe.find { it["eventId"].asText() == aktivBeskjed.eventId }
            require(varselJson != null)
            varselJson["grupperingsId"].asText() shouldBe aktivBeskjed.grupperingsId
            varselJson["eventId"].asText() shouldBe aktivBeskjed.eventId
            varselJson["eventTidspunkt"].asDateTime() shouldBe aktivBeskjed.eventTidspunkt.comparableTime()
            varselJson["produsent"].asText() shouldBe aktivBeskjed.appnavn
            varselJson["sikkerhetsnivaa"].asInt() shouldBe aktivBeskjed.sikkerhetsnivaa
            varselJson["sistOppdatert"].asDateTime() shouldBe aktivBeskjed.sistOppdatert.comparableTime()
            varselJson["tekst"].asText() shouldBe aktivBeskjed.tekst
            varselJson["link"].asText() shouldBe aktivBeskjed.link
            varselJson["aktiv"].asBoolean() shouldBe true
            varselJson["forstBehandlet"].asDateTime() shouldBe aktivBeskjed.forstBehandlet.comparableTime()
            varselJson["type"].asText() shouldBe "BESKJED"
            varselJson["fristUtløpt"].asBooleanOrNull() shouldBe null

        }
    }
}

