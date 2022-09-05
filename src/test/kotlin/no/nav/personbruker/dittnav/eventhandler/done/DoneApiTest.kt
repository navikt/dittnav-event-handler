package no.nav.personbruker.dittnav.eventhandler.done

import io.kotest.matchers.shouldBe
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import no.nav.personbruker.dittnav.eventhandler.common.database.LocalPostgresDatabase
import no.nav.personbruker.dittnav.eventhandler.mockEventHandlerApi
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DoneApiTest {
    private val doneEndpoint = "/dittnav-event-handler/produce/done"
    private val database = LocalPostgresDatabase.cleanDb()
    private val doneEventService = DoneEventService(database = database)


    @Test
    fun `inaktiverer varsel og returnerer 200`() {

        withTestApplication(
            mockEventHandlerApi(
                doneEventService = doneEventService,
                database = database
            )
        ) {
            val response = handleRequest {
                method = HttpMethod.Post
                uri = doneEndpoint
                addHeader("Content-Type", "application/json")
                setBody("""{"eventId": "123"}""")
            }.response
            response.status() shouldBe HttpStatusCode.OK
        }
    }

    @Test
    fun `returnerer 200 for allerede inaktiverte varsel og varsel som ikke finnes`() {

        withTestApplication(
            mockEventHandlerApi(
                doneEventService = doneEventService
            )
        ) {
            val response = handleRequest {
                method = HttpMethod.Post
                uri = doneEndpoint
                addHeader("Content-Type", "application/json")
                setBody("""{"eventId": "123"}""")
            }.response
            response.status() shouldBe HttpStatusCode.OK
        }
    }

    @Test
    fun `400 når eventId mangler`() {
        withTestApplication(
            mockEventHandlerApi(
                doneEventService = doneEventService
            )
        ) {
            handleRequest {
                method = HttpMethod.Post
                uri = doneEndpoint
                addHeader("Content-Type", "application/json")
                setBody("""{"event": "123"}""")
            }.response.status() shouldBe HttpStatusCode.BadRequest
        }
    }
}
