package no.nav.personbruker.dittnav.eventhandler.beskjed

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.testing.testApplication
import no.nav.personbruker.dittnav.eventhandler.ComparableVarsel
import no.nav.personbruker.dittnav.eventhandler.asZonedDateTime
import no.nav.personbruker.dittnav.eventhandler.common.database.LocalPostgresDatabase
import no.nav.personbruker.dittnav.eventhandler.createBeskjed
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.DoknotifikasjonTestStatus
import no.nav.personbruker.dittnav.eventhandler.mockEventHandlerApi
import no.nav.tms.token.support.authentication.installer.mock.installMockedAuthenticators
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

private val objectMapper = ObjectMapper()

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BeskjedApiTest {
    private val baseEventId = "7777"
    private val beskjedTestFnr = "234567"
    private val fetchBeskjedEndpoint = "/dittnav-event-handler/fetch/beskjed"
    private val database = LocalPostgresDatabase.cleanDb()
    private val aktivMedEksternVarsling = createBeskjed(
        fodselsnummer = beskjedTestFnr,
        eventId = "${baseEventId}9",
        eksternVarslingKanaler = listOf("SMS")
    ).medEksternVarsling(true)
    private val aktivBeskjedMedFeiletEksternVarsling = createBeskjed(
        fodselsnummer = beskjedTestFnr,
        eventId = "${baseEventId}8",
        eksternVarslingKanaler = listOf("SMS", "EPOST")
    ).medEksternVarsling(false)
    private val inaktivBeskjedUtenEksternVarsling = createBeskjed(
        fodselsnummer = beskjedTestFnr,
        eventId = "${baseEventId}7",
        aktiv = false
    )
    private val aktivBeskjedMedAnnetpersonNummer = createBeskjed(
        eventId = "${baseEventId}3",
        fodselsnummer = "887766"
    )


    @BeforeAll
    fun `populer testdata`() {
        database.createBeskjed(
            listOf(
                aktivMedEksternVarsling.beskjed,
                aktivBeskjedMedFeiletEksternVarsling.beskjed,
                inaktivBeskjedUtenEksternVarsling,
                aktivBeskjedMedAnnetpersonNummer
            )
        )
        database.createDoknotStatuses(
            listOf(
                aktivMedEksternVarsling.doknotStatus,
                aktivBeskjedMedFeiletEksternVarsling.doknotStatus
            )
        )
    }

    @AfterAll
    fun `slett testdata`() {
        database.deleteAllDoknotStatusBeskjed()
        database.deleteBeskjed(
            listOf(
                aktivMedEksternVarsling.beskjed,
                aktivBeskjedMedFeiletEksternVarsling.beskjed,
                inaktivBeskjedUtenEksternVarsling,
                aktivBeskjedMedAnnetpersonNummer
            )
        )
    }

    @Test
    fun `henter aktive beskjedvarsel`() {
        val expectedVarsler = listOf(
            aktivMedEksternVarsling.beskjed,
            aktivBeskjedMedFeiletEksternVarsling.beskjed
        )
        testApplication {
            mockEventHandlerApi(
                database = database,
                beskjedEventService = BeskjedEventService(database),
                installAuthenticatorsFunction = { beskjedAuthConfig(beskjedTestFnr) }
            )
            val aktiveVarsler = client.get("$fetchBeskjedEndpoint/aktive")
            aktiveVarsler.status shouldBe HttpStatusCode.OK
            objectMapper.readTree(aktiveVarsler.bodyAsText()) shouldContainExactly expectedVarsler
        }

    }

    @Test
    fun `henter inaktive varsel`() {
        testApplication {
            mockEventHandlerApi(
                database = database,
                beskjedEventService = BeskjedEventService(database),
                installAuthenticatorsFunction = { beskjedAuthConfig(beskjedTestFnr) }
            )
            val inaktiveVarsler = client.get("$fetchBeskjedEndpoint/inaktive")

            objectMapper.readTree(inaktiveVarsler.bodyAsText()) shouldContainExactly listOf(
                inaktivBeskjedUtenEksternVarsling
            )
        }

    }


    @Test
    fun `henter alle varsel`() {
        val expectedVarsel = listOf(
            aktivMedEksternVarsling.beskjed,
            aktivBeskjedMedFeiletEksternVarsling.beskjed,
            inaktivBeskjedUtenEksternVarsling
        )
        testApplication {
            mockEventHandlerApi(
                database = database,
                beskjedEventService = BeskjedEventService(database),
                installAuthenticatorsFunction = { beskjedAuthConfig(beskjedTestFnr) }
            )
            val aktiveVarsler = client.get("$fetchBeskjedEndpoint/all")

            objectMapper.readTree(aktiveVarsler.bodyAsText()) shouldContainExactly expectedVarsel
        }

    }

    /*
    TODO
        "/fetch/beskjed/grouped"
        "/fetch/grouped/producer/beskjed"
        "/fetch/modia/beskjed/aktive"
        /fetch/modia/beskjed/inaktive
        "/fetch/modia/beskjed/all" */

}

private infix fun JsonNode.shouldContainExactly(expected: List<Beskjed>) {
    val comparableResultList = this.map { it.toComparableBeskjed() }
    comparableResultList.size shouldBe expected.size
    comparableResultList.forEach { comparableResult ->
        val comparableExpected = expected.find { it.eventId == comparableResult.eventId }
        require(comparableExpected != null)
        comparableResult shouldEqual comparableExpected
    }
}

private fun JsonNode.toComparableBeskjed(): ComparableVarsel = ComparableVarsel(
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

private fun Application.beskjedAuthConfig(testFnr: String) {
    installMockedAuthenticators {
        installTokenXAuthMock {
            setAsDefault = true

            alwaysAuthenticated = true
            staticUserPid = testFnr
            staticSecurityLevel = no.nav.tms.token.support.tokenx.validation.mock.SecurityLevel.LEVEL_4
        }
        installAzureAuthMock { }
    }
}
