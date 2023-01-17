package no.nav.personbruker.dittnav.eventhandler.varsel;

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import no.nav.personbruker.dittnav.eventhandler.apiTestfnr
import no.nav.personbruker.dittnav.eventhandler.asBooleanOrNull
import no.nav.personbruker.dittnav.eventhandler.asDateTime
import no.nav.personbruker.dittnav.eventhandler.assert
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedObjectMother
import no.nav.personbruker.dittnav.eventhandler.beskjed.createBeskjed
import no.nav.personbruker.dittnav.eventhandler.common.VarselType
import no.nav.personbruker.dittnav.eventhandler.common.database.LocalPostgresDatabase
import no.nav.personbruker.dittnav.eventhandler.common.database.createDoknotifikasjon
import no.nav.personbruker.dittnav.eventhandler.comparableTime
import no.nav.personbruker.dittnav.eventhandler.getMedFnrHeader
import no.nav.personbruker.dittnav.eventhandler.innboks.InnboksObjectMother
import no.nav.personbruker.dittnav.eventhandler.innboks.createInnboks
import no.nav.personbruker.dittnav.eventhandler.mockEventHandlerApi
import no.nav.personbruker.dittnav.eventhandler.oppgave.OppgaveObjectMother
import no.nav.personbruker.dittnav.eventhandler.oppgave.createOppgave
import no.nav.tms.token.support.authentication.installer.mock.installMockedAuthenticators
import no.nav.tms.token.support.tokenx.validation.mock.SecurityLevel
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VarselApiTest {

    private val objectMapper = ObjectMapper()
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
                    inaktivOppgave.also {
                    },
                    OppgaveObjectMother.createOppgave(fodselsnummer = "321", aktiv = false, fristUtløpt = null)
                )
            )
            createDoknotifikasjon(inaktivOppgave.eventId, VarselType.OPPGAVE)
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

    @ParameterizedTest
    @ValueSource(strings = ["dittnav-event-handler/fetch/varsel/on-behalf-of/inaktive", "dittnav-event-handler/fetch/event/inaktive"])
    fun `varsel-apiet skal returnere inaktive varsler`(url: String) =
        testApplication {
            mockEventHandlerApi(varselRepository = varselRepository)
            val response =
                client.getMedFnrHeader(url, fodselsnummer)

            response.status shouldBe HttpStatusCode.OK
            val varselListe = objectMapper.readTree(response.bodyAsText())
            varselListe.size() shouldBe antallinaktiveVarselForFnr

            val varselJson = varselListe.find { it["eventId"].asText() == inaktivBeskjed.eventId }
            require(varselJson != null)
            varselJson["eventId"].asText() shouldBe inaktivBeskjed.eventId
            varselJson["sikkerhetsnivaa"].asInt() shouldBe inaktivBeskjed.sikkerhetsnivaa
            varselJson["sistOppdatert"].asDateTime() shouldBe inaktivBeskjed.sistOppdatert.comparableTime()
            varselJson["tekst"].asText() shouldBe inaktivBeskjed.tekst
            varselJson["link"].asText() shouldBe inaktivBeskjed.link
            varselJson["aktiv"].asBoolean() shouldBe false
            varselJson["forstBehandlet"].asDateTime() shouldBe inaktivBeskjed.forstBehandlet.comparableTime()
            varselJson["type"].asText() shouldBe "BESKJED"
            varselJson["fristUtløpt"].asBoolean() shouldBe false
            varselJson["eksternVarslingSendt"].asBoolean() shouldBe false
            varselJson["eksternVarslingKanaler"].toList() shouldBe emptyList<String>()
            varselListe.find { it["eventId"].asText() == inaktivOppgave.eventId }.apply {
                require(this != null)
                get("fristUtløpt").asBooleanOrNull() shouldBe true
                get("eksternVarslingSendt").asBoolean() shouldBe true
                get("eksternVarslingKanaler").toList().map { it.asText() } shouldContainExactly listOf("SMS", "EPOST")
            }
        }


    @ParameterizedTest
    @ValueSource(strings = ["dittnav-event-handler/fetch/varsel/on-behalf-of/aktive", "dittnav-event-handler/fetch/event/aktive"])
    fun `varsel-apiet skal returnere aktive varsler`(url: String) =
        testApplication {
            mockEventHandlerApi(varselRepository = varselRepository)
            val response =
                client.getMedFnrHeader(url, fodselsnummer)

            response.status shouldBe HttpStatusCode.OK
            val varselListe = objectMapper.readTree(response.bodyAsText())
            varselListe.size() shouldBe antallaktiveVarselForFnr

            val varselJson = varselListe.find { it["eventId"].asText() == aktivBeskjed.eventId }
            require(varselJson != null)
            varselJson["eventId"].asText() shouldBe aktivBeskjed.eventId
            varselJson["sikkerhetsnivaa"].asInt() shouldBe aktivBeskjed.sikkerhetsnivaa
            varselJson["sistOppdatert"].asDateTime() shouldBe aktivBeskjed.sistOppdatert.comparableTime()
            varselJson["tekst"].asText() shouldBe aktivBeskjed.tekst
            varselJson["link"].asText() shouldBe aktivBeskjed.link
            varselJson["aktiv"].asBoolean() shouldBe true
            varselJson["forstBehandlet"].asDateTime() shouldBe aktivBeskjed.forstBehandlet.comparableTime()
            varselJson["type"].asText() shouldBe "BESKJED"
            varselJson["fristUtløpt"].asBooleanOrNull() shouldBe null

        }


    @Test
    fun `skal maskeree varsel`() = testApplication {
        mockEventHandlerApi(varselRepository = varselRepository, installAuthenticatorsFunction = {
            installMockedAuthenticators {
                installTokenXAuthMock {
                    setAsDefault = true
                    alwaysAuthenticated = true
                    staticUserPid = apiTestfnr
                    staticSecurityLevel = SecurityLevel.LEVEL_3
                }
                installAzureAuthMock {
                    setAsDefault = false
                    alwaysAuthenticated = true
                }
            }
        })
        client.getMedFnrHeader("dittnav-event-handler/fetch/varsel/on-behalf-of/inaktive", fodselsnummer, 3).assert {
            val varsler = objectMapper.readTree(bodyAsText())
            varsler.all { it["tekst"].textValue() == null } shouldBe true
            varsler.all { it["link"].textValue() == null } shouldBe true
        }

        client.get("dittnav-event-handler/fetch/event/inaktive").assert {
            val varsler = objectMapper.readTree(bodyAsText())
            varsler.all { it["tekst"].textValue() == null } shouldBe true
            varsler.all { it["link"].textValue() == null } shouldBe true
        }

        client.getMedFnrHeader("dittnav-event-handler/fetch/varsel/on-behalf-of/aktive", fodselsnummer, 3).assert {
            val varsler = objectMapper.readTree(bodyAsText())
            varsler.all { it["tekst"].textValue() == null } shouldBe true
            varsler.all { it["link"].textValue() == null } shouldBe true
        }

        client.get("dittnav-event-handler/fetch/event/aktive").assert {
            val varsler = objectMapper.readTree(bodyAsText())
            varsler.all { it["tekst"].textValue() == null } shouldBe true
            varsler.all { it["link"].textValue() == null } shouldBe true

        }
    }
}
