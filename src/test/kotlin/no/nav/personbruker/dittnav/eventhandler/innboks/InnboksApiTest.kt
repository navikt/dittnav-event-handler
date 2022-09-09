package no.nav.personbruker.dittnav.eventhandler.innboks

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationBuilder
import io.ktor.server.testing.testApplication
import no.nav.personbruker.dittnav.eventhandler.ComparableVarsel
import no.nav.personbruker.dittnav.eventhandler.asZonedDateTime
import no.nav.personbruker.dittnav.eventhandler.common.database.LocalPostgresDatabase
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.DoknotifikasjonTestStatus
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.EksternVarslingStatus
import no.nav.personbruker.dittnav.eventhandler.mockEventHandlerApi
import no.nav.tms.token.support.authentication.installer.mock.installMockedAuthenticators
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InnboksApiTest {
    private val database = LocalPostgresDatabase.cleanDb()
    private val objectMapper = ObjectMapper()
    private val innboksEndpoint = "/dittnav-event-handler/fetch/innboks"

    @BeforeAll
    fun `populer test-data`() {
        database.createInnboks(listOf(innboks1Aktiv, innboks2Aktiv, innboks3Aktiv, innboks4Inaktiv))
        database.createDoknotStatuses(listOf(doknotStatusForInnboks1, doknotStatusForInnboks2))
    }

    @AfterAll
    fun `slett Innboks-eventer fra tabellen`() {
        database.deleteAllDoknotStatusInnboks()
        database.deleteInnboks(listOf(innboks1Aktiv, innboks2Aktiv, innboks3Aktiv, innboks4Inaktiv))
    }

    @Test
    fun `henter aktive innboks varsler`() {
        testApplication {
            val expected = listOf(
                innboks1Aktiv.updateWith(doknotStatusForInnboks1),
                innboks2Aktiv.updateWith(doknotStatusForInnboks2)
            )
            mockApiWithFnr(database, innboksTestFnr1)
            val aktiveVarsler = client.get("$innboksEndpoint/aktive")
            aktiveVarsler.status shouldBe HttpStatusCode.OK
            objectMapper.readTree(aktiveVarsler.bodyAsText()) shouldContainExactly expected
        }
        testApplication {
            val expected = listOf(innboks3Aktiv.updateWith(null))
            mockApiWithFnr(database, innboksTestFnr2)
            val aktiveVarsler = client.get("$innboksEndpoint/aktive")
            aktiveVarsler.status shouldBe HttpStatusCode.OK
            objectMapper.readTree(aktiveVarsler.bodyAsText()) shouldContainExactly expected
        }
    }

    @Test
    fun `henter inaktive innboks varsler`() {
        testApplication {
            mockApiWithFnr(database, innboksTestFnr1)
            val inaktiveVarsler = client.get("$innboksEndpoint/inaktive")
            inaktiveVarsler.status shouldBe HttpStatusCode.OK
            objectMapper.readTree(inaktiveVarsler.bodyAsText()).toList().size shouldBe 0
        }
        testApplication {
            val expected = listOf(innboks4Inaktiv.updateWith(null))
            mockApiWithFnr(database, innboksTestFnr2)
            val inaktiveVarsler = client.get("$innboksEndpoint/inaktive")
            inaktiveVarsler.status shouldBe HttpStatusCode.OK
            objectMapper.readTree(inaktiveVarsler.bodyAsText()) shouldContainExactly expected
        }
    }

    @Test
    fun `henter alle innboks varsler`() {
        testApplication {
            mockApiWithFnr(database, innboksTestFnr1)
            val expected = listOf(
                innboks1Aktiv.updateWith(doknotStatusForInnboks1),
                innboks2Aktiv.updateWith(doknotStatusForInnboks2)
            )
            val alleVarsler = client.get("$innboksEndpoint/all")
            alleVarsler.status shouldBe HttpStatusCode.OK
            objectMapper.readTree(alleVarsler.bodyAsText()) shouldContainExactly expected
        }
        testApplication {
            val expected = listOf(innboks4Inaktiv.updateWith(null), innboks3Aktiv.updateWith(null))
            mockApiWithFnr(database, innboksTestFnr2)
            val alleVarsler = client.get("$innboksEndpoint/all")
            alleVarsler.status shouldBe HttpStatusCode.OK
            objectMapper.readTree(alleVarsler.bodyAsText()) shouldContainExactly expected
        }

    }


    /*
    TODO
    * "/fetch/innboks/grouped"
    * /fetch/grouped/producer/innboks
    * /fetch/modia/innboks/aktive
    * /fetch/modia/innboks/inaktive
    * /fetch/modia/innboks/all
    *
    *
    * */

}


private fun Innboks.updateWith(doknotStatus: DoknotifikasjonTestStatus?) =
    if (doknotStatus != null) {
        this.copy(
            eksternVarslingInfo = this.eksternVarslingInfo.copy(
                sendt = doknotStatus.status == EksternVarslingStatus.OVERSENDT.name,
                sendteKanaler = if (doknotStatus.kanaler != "") listOf(doknotStatus.kanaler) else emptyList()
            )
        )
    } else {
        this.copy(
            eksternVarslingInfo = this.eksternVarslingInfo.copy(
                sendt = false,
                sendteKanaler = emptyList()
            )
        )
    }

private infix fun JsonNode.shouldContainExactly(expected: List<Innboks>) {
    val comparableResultList = this.map { it.toComparableInnboks() }
    comparableResultList.size shouldBe expected.size
    comparableResultList.forEach { comparableResult ->
        val comparableExpected = expected.find { it.eventId == comparableResult.eventId }
        require(comparableExpected != null)
        comparableResult shouldEqual comparableExpected
    }
}


private fun JsonNode.toComparableInnboks(): ComparableVarsel = ComparableVarsel(
    fodselsnummer = this["fodselsnummer"].asText(),
    grupperingsId = this["grupperingsId"].asText(),
    eventId = this["eventId"].asText(),
    forstBehandlet = this["forstBehandlet"].asZonedDateTime(),
    produsent = this["produsent"].asText(),
    sikkerhetsnivaa = this["sikkerhetsnivaa"].asInt(),
    sistOppdatert = this["sistOppdatert"].asZonedDateTime(),
    tekst = this["tekst"].asText(),
    link = this["link"].asText(),
    aktiv = this["aktiv"].asBoolean(),
    eksternVarslingSendt = this["eksternVarslingSendt"].asBoolean(),
    eksternVarslingKanaler = this["eksternVarslingKanaler"].map { it.asText() }
)

private fun TestApplicationBuilder.mockApiWithFnr(database: LocalPostgresDatabase, fnrString: String) {
    mockEventHandlerApi(
        database = database,
        innboksEventService = InnboksEventService(database),
        installAuthenticatorsFunction = {
            installMockedAuthenticators {
                installTokenXAuthMock {
                    setAsDefault = true

                    alwaysAuthenticated = true
                    staticUserPid = fnrString
                    staticSecurityLevel = no.nav.tms.token.support.tokenx.validation.mock.SecurityLevel.LEVEL_4
                }
                installAzureAuthMock { }
            }
        }
    )
}