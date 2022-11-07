package no.nav.personbruker.dittnav.eventhandler.beskjed

import Beskjed
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.server.application.Application
import io.ktor.server.testing.testApplication
import no.nav.personbruker.dittnav.eventhandler.ComparableVarsel
import no.nav.personbruker.dittnav.eventhandler.asZonedDateTime
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedTestData.beskjed1Aktiv
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedTestData.beskjed2Aktiv
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedTestData.beskjed3Inaktiv
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedTestData.beskjed4Aktiv
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedTestData.beskjedTestFnr
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedTestData.doknotStatusForBeskjed1
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedTestData.doknotStatusForBeskjed2
import no.nav.personbruker.dittnav.eventhandler.common.database.LocalPostgresDatabase
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.DoknotifikasjonTestStatus
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.EksternVarslingStatus
import no.nav.personbruker.dittnav.eventhandler.mockEventHandlerApi
import no.nav.tms.token.support.authentication.installer.mock.installMockedAuthenticators
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

private val objectMapper = ObjectMapper()

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BeskjedApiTest {
    private val fetchBeskjedEndpoint = "/dittnav-event-handler/fetch/beskjed"
    private val database = LocalPostgresDatabase.cleanDb()

    @BeforeAll
    fun `populer testdata`() {
        database.createBeskjed(
            listOf(
                beskjed1Aktiv,
                beskjed2Aktiv,
                beskjed3Inaktiv,
                beskjed4Aktiv
            )
        )
        database.createDoknotStatuses(
            listOf(
                doknotStatusForBeskjed1,
                doknotStatusForBeskjed2
            )
        )
    }

    @AfterAll
    fun `slett testdata`() {
        database.deleteAllDoknotStatusBeskjed()
        database.deleteBeskjed(listOf(beskjed1Aktiv, beskjed2Aktiv, beskjed3Inaktiv, beskjed4Aktiv))
    }

    @Test
    fun `henter aktive beskjedvarsel`() {
        val expectedVarsler = listOf(
            beskjed1Aktiv.updateWith(doknotStatusForBeskjed1),
            beskjed2Aktiv.updateWith(doknotStatusForBeskjed2)
        )
        testApplication {
            mockEventHandlerApi(
                database = database,
                beskjedEventService = BeskjedEventService(database),
                installAuthenticatorsFunction = Application::beskjedAuthConfig
            )
            val aktiveVarsler = client.get("$fetchBeskjedEndpoint/aktive")

            objectMapper.readTree(aktiveVarsler.bodyAsText()) shouldContainExactly expectedVarsler
        }

    }

    @Test
    fun `henter inaktive varsel`() {
        testApplication {
            mockEventHandlerApi(
                database = database,
                beskjedEventService = BeskjedEventService(database),
                installAuthenticatorsFunction = Application::beskjedAuthConfig
            )
            val aktiveVarsler = client.get("$fetchBeskjedEndpoint/inaktive")

            objectMapper.readTree(aktiveVarsler.bodyAsText()) shouldContainExactly listOf(
                beskjed3Inaktiv.updateWith(null)
            )
        }

    }


    @Test
    fun `henter alle varsel`() {
        val expectedVarsel = listOf(
            beskjed1Aktiv.updateWith(doknotStatusForBeskjed1),
            beskjed2Aktiv.updateWith(doknotStatusForBeskjed2),
            beskjed3Inaktiv.updateWith(null)
        )
        testApplication {
            mockEventHandlerApi(
                database = database,
                beskjedEventService = BeskjedEventService(database),
                installAuthenticatorsFunction = Application::beskjedAuthConfig
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

private fun Application.beskjedAuthConfig() {
    installMockedAuthenticators {
        installTokenXAuthMock {
            setAsDefault = true

            alwaysAuthenticated = true
            staticUserPid = beskjedTestFnr
            staticSecurityLevel = no.nav.tms.token.support.tokenx.validation.mock.SecurityLevel.LEVEL_4
        }
        installAzureAuthMock { }
    }
}

private fun Beskjed.updateWith(doknotStatus: DoknotifikasjonTestStatus?): Beskjed =
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

