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
    private val beskjedTestFnr = "234567"
    private val fetchBeskjedEndpoint = "/dittnav-event-handler/fetch/beskjed"
    private val database = LocalPostgresDatabase.cleanDb()
    private val aktivBeskjedMedEksternVarsling = createBeskjed()
    private val aktivBeskjedMedFeiletEksternVarsling = createBeskjed()
    private val inaktivBeskjedUtenEksternVarsling = createBeskjed()
    private val aktivBeskjedMedAnnetpersonNummer = createBeskjed()
    private val sendtDoknotStatus = DoknotifikasjonTestStatus(
        eventId = aktivBeskjedMedEksternVarsling.eventId,
        status = EksternVarslingStatus.OVERSENDT.name,
        melding = "melding",
        distribusjonsId = 8877L,
        kanaler = "SMS,EPOST"
    )
    private val feiletDoknostStatus = DoknotifikasjonTestStatus(
        eventId = aktivBeskjedMedFeiletEksternVarsling.eventId,
        status = EksternVarslingStatus.FEILET.name,
        melding = "feilet",
        distribusjonsId = 8877L,
        kanaler = "SMS"

    )


    @BeforeAll
    fun `populer testdata`() {
        database.createBeskjed(
            listOf(
                aktivBeskjedMedEksternVarsling,
                aktivBeskjedMedFeiletEksternVarsling,
                inaktivBeskjedUtenEksternVarsling,
                aktivBeskjedMedAnnetpersonNummer
            )
        )
        database.createDoknotStatuses(
            listOf(
                sendtDoknotStatus,
                feiletDoknostStatus
            )
        )
    }

    @AfterAll
    fun `slett testdata`() {
        database.deleteAllDoknotStatusBeskjed()
        database.deleteBeskjed(
            listOf(
                aktivBeskjedMedEksternVarsling,
                aktivBeskjedMedFeiletEksternVarsling,
                inaktivBeskjedUtenEksternVarsling,
                aktivBeskjedMedAnnetpersonNummer
            )
        )
    }

    @Test
    fun `henter aktive beskjedvarsel`() {
        val expectedVarsler = listOf(
            aktivBeskjedMedEksternVarsling.updateWith(sendtDoknotStatus),
            aktivBeskjedMedFeiletEksternVarsling.updateWith(feiletDoknostStatus)
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
            val aktiveVarsler = client.get("$fetchBeskjedEndpoint/inaktive")

            objectMapper.readTree(aktiveVarsler.bodyAsText()) shouldContainExactly listOf(
                inaktivBeskjedUtenEksternVarsling.updateWith(null)
            )
        }

    }


    @Test
    fun `henter alle varsel`() {
        val expectedVarsel = listOf(
            aktivBeskjedMedEksternVarsling.updateWith(sendtDoknotStatus),
            aktivBeskjedMedFeiletEksternVarsling.updateWith(feiletDoknostStatus),
            inaktivBeskjedUtenEksternVarsling.updateWith(null)
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

private fun Beskjed.updateWith(doknotStatus: DoknotifikasjonTestStatus?): Beskjed =
    if (doknotStatus != null) {
        this.copy(
            eksternVarslingKanaler = this.eksternVarslingKanaler,
            eksternVarslingSendt = this.eksternVarslingSendt
        )
    } else {
        this.copy(
            eksternVarslingKanaler = this.eksternVarslingKanaler,
            eksternVarslingSendt = false

        )
    }

