package no.nav.personbruker.dittnav.eventhandler.done

import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.OsloDateTime
import no.nav.personbruker.dittnav.eventhandler.apiTestfnr
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedObjectMother
import no.nav.personbruker.dittnav.eventhandler.beskjed.createBeskjed
import no.nav.personbruker.dittnav.eventhandler.beskjed.deleteBeskjed
import no.nav.personbruker.dittnav.eventhandler.common.database.LocalPostgresDatabase
import no.nav.personbruker.dittnav.eventhandler.mockEventHandlerApi
import no.nav.tms.token.support.authentication.installer.mock.installMockedAuthenticators
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DoneApiSystemClientTest {
    private val doneEndpoint = "/dittnav-event-handler/beskjed/done"
    private val database = LocalPostgresDatabase.cleanDb()
    private val doneEventService = DoneEventService(database = database)
    private val systembruker = "x-dittnav"
    private val namespace = "localhost"
    private val appnavn = "dittnav"
    private val inaktivBeskjed = BeskjedObjectMother.createBeskjed(
        id = 1,
        eventId = "12387696478230",
        fodselsnummer = apiTestfnr,
        synligFremTil = OsloDateTime.now().plusHours(1),
        aktiv = false,
        systembruker = systembruker,
        namespace = namespace,
        appnavn = appnavn
    )
    private val aktivBeskjed = BeskjedObjectMother.createBeskjed(
        id = 2,
        eventId = "123465abnhkfg",
        fodselsnummer = apiTestfnr,
        synligFremTil = OsloDateTime.now().plusHours(1),
        aktiv = true,
        systembruker = systembruker,
        namespace = namespace,
        appnavn = appnavn
    )


    @BeforeAll
    fun populate() {
        runBlocking {
            database.dbQuery {
                createBeskjed(listOf(inaktivBeskjed, aktivBeskjed))
            }
        }
    }

    @AfterAll
    fun cleanup() {
        runBlocking {
            database.dbQuery {
                deleteBeskjed(listOf(inaktivBeskjed, aktivBeskjed))
            }
        }
    }

    @Test
    fun `inaktiverer varsel og returnerer 200`() {

        testApplication {
            mockSystemClientApi()
            val response = client.doneRequest(eventId = aktivBeskjed.eventId, fnr = apiTestfnr)
            response.status shouldBe HttpStatusCode.OK
        }
    }

    @Test
    fun `200 for allerede inaktiverte varsel`() {
        testApplication {
            mockSystemClientApi()
            val response = client.doneRequest(eventId = inaktivBeskjed.eventId, fnr = apiTestfnr)
            response.status shouldBe HttpStatusCode.OK
        }
    }

    @Test
    fun `400 for varsel som ikke finnes`() {
        testApplication {
            mockSystemClientApi()
            val response = client.doneRequest(eventId = "12311111111", fnr = apiTestfnr)
            response.status shouldBe HttpStatusCode.BadRequest
            response.bodyAsText() shouldBe "beskjed med eventId 12311111111 ikke funnet"

        }
    }

    @Test
    fun `400 når eventId mangler`() {
        testApplication {
            mockSystemClientApi()
            val result = client.request {
                method = HttpMethod.Post
                url(doneEndpoint)
                header("Content-Type", "application/json")
                header("fodselsnummer", apiTestfnr)
                setBody("""{"event": "12398634581111"}""")
            }
            result.status shouldBe HttpStatusCode.BadRequest
            result.bodyAsText() shouldBe "eventid parameter mangler"
        }
    }


    @Test
    fun `400 når fødselsnummer mangler`() {
        testApplication {
            mockSystemClientApi()
            val result = client.request {
                method = HttpMethod.Post
                url(doneEndpoint)
                header("Content-Type", "application/json")
                setBody("""{"event": "${aktivBeskjed.eventId}"}""")
            }
            result.status shouldBe HttpStatusCode.BadRequest
            result.bodyAsText() shouldBe "Requesten mangler header-en 'fodselsnummer'"
        }
    }

    private suspend fun HttpClient.doneRequest(eventId: String, fnr: String) = request {
        url(doneEndpoint)
        method = HttpMethod.Post
        header("Content-Type", "application/json")
        header("fodselsnummer",fnr)
        setBody("""{"eventId": "$eventId"}""")
    }

    private fun ApplicationTestBuilder.mockSystemClientApi(){
        mockEventHandlerApi(
            doneEventService = doneEventService,
            database = database,
            installAuthenticatorsFunction = { systemclientAuth(true) }
        )
    }
}

private fun Application.systemclientAuth(authenticated: Boolean) {
    installMockedAuthenticators {
        installTokenXAuthMock {
            setAsDefault = true
        }
        installAzureAuthMock {
            setAsDefault = false
            alwaysAuthenticated = authenticated

        }
    }
}



