package no.nav.personbruker.dittnav.eventhandler.done

import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.header
import io.ktor.server.testing.setBody
import io.ktor.server.testing.testApplication
import io.ktor.server.testing.withTestApplication
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.apiTestfnr
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedObjectMother
import no.nav.personbruker.dittnav.eventhandler.beskjed.createBeskjed
import no.nav.personbruker.dittnav.eventhandler.common.database.LocalPostgresDatabase
import no.nav.personbruker.dittnav.eventhandler.mockEventHandlerApi
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.ZonedDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DoneApiTest {
    private val doneEndpoint = "/dittnav-event-handler/produce/done"
    private val database = LocalPostgresDatabase.cleanDb()
    private val doneEventService = DoneEventService(database = database)
    private val systembruker = "x-dittnav"
    private val namespace = "localhost"
    private val appnavn = "dittnav"
    private val inaktivBeskjed = BeskjedObjectMother.createBeskjed(
        id = 1,
        eventId = "12387696478230",
        fodselsnummer = apiTestfnr,
        synligFremTil = ZonedDateTime.now().plusHours(1),
        aktiv = false,
        systembruker = systembruker,
        namespace = namespace,
        appnavn = appnavn
    )
    private val aktivBeskjed = BeskjedObjectMother.createBeskjed(
        id = 2,
        eventId = "123465abnhkfg",
        fodselsnummer = apiTestfnr,
        synligFremTil = ZonedDateTime.now().plusHours(1),
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

    @Test
    fun `inaktiverer varsel og returnerer 200`() {

        testApplication {
            mockEventHandlerApi(
                doneEventService = doneEventService,
                database = database
            )
            val response = client.request {
                url(doneEndpoint)
                method = HttpMethod.Post
                header("Content-Type", "application/json")
                setBody("""{"eventId": "${aktivBeskjed.eventId}"}""")
            }
            response.status shouldBe HttpStatusCode.OK
        }
    }

    @Test
    fun `200 for allerede inaktiverte varsel`() {
        testApplication {
            mockEventHandlerApi(
                doneEventService = doneEventService
            )
            val response = client.request{
                method = HttpMethod.Post
                url(doneEndpoint)
                header("Content-Type", "application/json")
                setBody("""{"eventId": "${inaktivBeskjed.eventId}"}""")
            }
            response.status shouldBe HttpStatusCode.OK
        }
    }

    @Test
    fun `400 for varsel som ikke finnes`() {
        testApplication {
            mockEventHandlerApi(
                doneEventService = doneEventService
            )
            val response = client.request {
                method = HttpMethod.Post
                url(doneEndpoint)
                header("Content-Type", "application/json")
                setBody("""{"eventId": "12311111111"}""")
            }
            response.status shouldBe HttpStatusCode.BadRequest
            response.bodyAsText() shouldBe "beskjed med eventId 12311111111 ikke funnet"

        }
    }

    @Test
    fun `400 når eventId mangler`() {
        testApplication{
            mockEventHandlerApi(
                doneEventService = doneEventService
            )
            val result = client.request {
                method = HttpMethod.Post
                url(doneEndpoint)
                header("Content-Type", "application/json")
                setBody("""{"event": "12398634581111"}""")
            }
            result.status shouldBe HttpStatusCode.BadRequest
            result.bodyAsText() shouldBe "eventid parameter mangler"
        }
    }
}
