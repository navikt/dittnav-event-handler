package no.nav.personbruker.dittnav.eventhandler.done

import io.kotest.matchers.shouldBe
import io.ktor.application.feature
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.routing.Route
import io.ktor.routing.Routing
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import io.mockk.mockk
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.event.EventRepository

import no.nav.personbruker.dittnav.eventhandler.mockEventHandlerApi
import org.junit.jupiter.api.Test

class DoneApiTest {
    private val database = mockk<Database>(relaxed = true)
    private val doneEventService = DoneEventService(
        database = database,
        doneProducer = mockk(relaxed = true)
    )

    @Test
    fun `inaktiverer varsel og returnerer 200`() {
        withTestApplication(mockEventHandlerApi(
            doneEventService = doneEventService
        )) {
            val response =  handleRequest {
                handleRequest(HttpMethod.Post, "/dittnav-event-handler/produce/done?eventId=123")
            }.response.status() shouldBe HttpStatusCode.OK

            println(response)
        }
    }

    @Test
    fun `returnerer 200 for inaktiverte varsel`() {

    }

    @Test
    fun `400 når eventId mangler`() {
        withTestApplication(mockEventHandlerApi(
            doneEventService = doneEventService
        )) {
            handleRequest {
                handleRequest(HttpMethod.Post, "/dittnav-event-handler/produce/done")
            }.response.status() shouldBe HttpStatusCode.BadRequest

        }

    }


}

private fun allRoutes(root: Route): List<Route> {
    return listOf(root) + root.children.flatMap { allRoutes(it) }
        .filter { it.toString().contains("method") && it.toString() != "/" }
}