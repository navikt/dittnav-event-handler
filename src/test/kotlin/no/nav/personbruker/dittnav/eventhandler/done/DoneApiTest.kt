package no.nav.personbruker.dittnav.eventhandler.done

import com.zaxxer.hikari.HikariDataSource
import io.kotest.matchers.shouldBe
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import io.mockk.coEvery
import io.mockk.mockk
import no.nav.personbruker.dittnav.eventhandler.beskjed.setBeskjedInaktiv
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.mockEventHandlerApi
import org.junit.jupiter.api.Test
import java.sql.Connection

class DoneApiTest {
    private val connection: Connection = mockk(relaxed = true)
    val hikariDataSource = mockk<HikariDataSource>(relaxed = true)
    private val doneEndpoint = "/dittnav-event-handler/produce/done"
    private val database = object : Database {
        override val dataSource: HikariDataSource
            get() = hikariDataSource
    }
    private val doneEventService = DoneEventService(database = database)

    @Test
    fun `inaktiverer varsel og returnerer 200`() {
        coEvery {
            hikariDataSource.connection
        } returns connection

        coEvery {
            connection.setBeskjedInaktiv(any(), "123")
        } returns 1

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
    fun `returnerer 200 for allerede inaktiverte varsel og varsel som ikke finnes`() {
        coEvery {
            hikariDataSource.connection
        } returns connection

        coEvery {
            connection.setBeskjedInaktiv(any(), "123")
        } returns 0

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
                setBody("""{"ikkeEventIdIhvertfall": "123"}""")
            }.response.status() shouldBe HttpStatusCode.BadRequest
            handleRequest(HttpMethod.Post, doneEndpoint).response.status() shouldBe HttpStatusCode.BadRequest
        }
    }
}
