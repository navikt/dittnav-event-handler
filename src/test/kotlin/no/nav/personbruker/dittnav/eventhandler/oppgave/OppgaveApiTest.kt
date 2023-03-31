package no.nav.personbruker.dittnav.eventhandler.oppgave

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
import no.nav.personbruker.dittnav.eventhandler.mockEventHandlerApi
import no.nav.personbruker.dittnav.eventhandler.oppgave.OppgaveTestData.oppgave1Aktiv
import no.nav.personbruker.dittnav.eventhandler.oppgave.OppgaveTestData.oppgave2Aktiv
import no.nav.personbruker.dittnav.eventhandler.oppgave.OppgaveTestData.oppgave3Inaktiv
import no.nav.personbruker.dittnav.eventhandler.oppgave.OppgaveTestData.oppgave4
import no.nav.personbruker.dittnav.eventhandler.oppgave.OppgaveTestData.oppgaveTestFnr
import no.nav.tms.token.support.authentication.installer.mock.installMockedAuthenticators
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OppgaveApiTest {
    private val database = LocalPostgresDatabase.cleanDb()
    private val objectMapper = ObjectMapper()
    private val oppgaveEndpoint = "/dittnav-event-handler/fetch/oppgave"


    @BeforeAll
    fun `populer test-data`() {
        database.createOppgave(
            listOf(
                oppgave1Aktiv,
                oppgave2Aktiv,
                oppgave3Inaktiv,
                oppgave4
            )
        )
    }

    @AfterAll
    fun `slett Oppgave-eventer fra tabellen`() {
        database.deleteOppgave()
    }

    @Test
    fun `henter aktive oppgaver`() {
        testApplication {
            mockApi(database)
            val expected = listOf(oppgave1Aktiv, oppgave2Aktiv)
            val aktiveVarsler = client.get("$oppgaveEndpoint/aktive")
            aktiveVarsler.status shouldBe HttpStatusCode.OK
            objectMapper.readTree(aktiveVarsler.bodyAsText()) shouldContainExactly expected
        }
    }

    @Test
    fun `henter inaktive oppgaver`() {
        testApplication {
            mockApi(database)
            val expected = listOf(
                oppgave3Inaktiv
            )
            val inaktiveVarsler = client.get("$oppgaveEndpoint/inaktive")
            inaktiveVarsler.status shouldBe HttpStatusCode.OK
            objectMapper.readTree(inaktiveVarsler.bodyAsText()) shouldContainExactly expected
        }
    }

    @Test
    fun `henter alle oppgaver`() {
        testApplication {
            mockApi(database)
            val expected = listOf(
                oppgave3Inaktiv,
                oppgave2Aktiv,
                oppgave1Aktiv
            )
            val alleVarsler = client.get("$oppgaveEndpoint/all")
            alleVarsler.status shouldBe HttpStatusCode.OK
            objectMapper.readTree(alleVarsler.bodyAsText()) shouldContainExactly expected
        }
    }

}

private infix fun JsonNode.shouldContainExactly(expected: List<Oppgave>) {
    val comparableResultList = this.map { it.toComparableOppgave() }
    comparableResultList.size shouldBe expected.size
    comparableResultList.forEach { comparableResult ->
        val comparableExpected = expected.find { it.eventId == comparableResult.eventId }
        require(comparableExpected != null)
        comparableResult shouldEqual comparableExpected
    }
}


private fun JsonNode.toComparableOppgave(): ComparableVarsel = ComparableVarsel(
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

private fun TestApplicationBuilder.mockApi(database: LocalPostgresDatabase) {
    mockEventHandlerApi(
        database = database,
        oppgaveEventService = OppgaveEventService(database),
        installAuthenticatorsFunction = {
            installMockedAuthenticators {
                installTokenXAuthMock {
                    setAsDefault = true

                    alwaysAuthenticated = true
                    staticUserPid = oppgaveTestFnr
                    staticSecurityLevel = no.nav.tms.token.support.tokenx.validation.mock.SecurityLevel.LEVEL_4
                }
                installAzureAuthMock { }
            }
        }
    )
}
