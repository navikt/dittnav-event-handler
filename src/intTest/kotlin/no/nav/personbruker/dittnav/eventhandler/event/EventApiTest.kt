package no.nav.personbruker.dittnav.eventhandler.event

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.extensions.system.withEnvironment
import io.kotest.matchers.shouldBe
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import io.mockk.coEvery
import io.mockk.mockk
import mockEventHandlerApi
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.time.ZoneId
import java.time.ZonedDateTime

class EventApiTest {

    @Test
    @Disabled
    fun `eventer-apiet skal returnere event på riktig format`() {
        val fodselsnummer = "12354"
        val grupperingsId = "123"
        val eventId = "456"
        val eventTidspunkt = ZonedDateTime.of(2020, 1, 1, 1, 1, 1, 1, ZoneId.of("Europe/Oslo"))
        val produsent = "produsent"
        val sikkerhetsnivaa = 4
        val sistOppdatert = ZonedDateTime.of(2020, 1, 1, 1, 1, 1, 1, ZoneId.of("Europe/Oslo"))
        val tekst = "tekst"
        val link = "link"
        val aktiv = true
        val type = EventType.BESKJED

        val eventRepository: EventRepository = mockk()
        coEvery {
            eventRepository.getInactiveEvents(any())
        }.returns(
            listOf(
                Event(
                    fodselsnummer = fodselsnummer,
                    grupperingsId = grupperingsId,
                    eventId = eventId,
                    eventTidspunkt = eventTidspunkt,
                    produsent = produsent,
                    sikkerhetsnivaa = sikkerhetsnivaa,
                    sistOppdatert = sistOppdatert,
                    tekst = tekst,
                    link = link,
                    aktiv = aktiv,
                    type = type
                )
            )
        )

        withEnvironment(mapOf("TOKEN_X_CLIENT_ID" to "test", "TOKEN_X_WELL_KNOWN_URL" to "test")) {
            withTestApplication(
                mockEventHandlerApi(eventRepository = eventRepository)
            ) {
                handleRequest(HttpMethod.Get, "") {
                }.apply {
                    response.status() shouldBe HttpStatusCode.OK

                    val eventJson = ObjectMapper().readTree(response.content)[0]
                    eventJson["fodselsnummer"] shouldBe fodselsnummer
                    eventJson["fodselsnummer"] shouldBe fodselsnummer
                    eventJson["grupperingsId"] shouldBe grupperingsId
                    eventJson["eventId"] shouldBe eventId
                    eventJson["eventTidspunkt"] shouldBe eventTidspunkt
                    eventJson["produsent"] shouldBe produsent
                    eventJson["sikkerhetsnivaa"] shouldBe sikkerhetsnivaa
                    eventJson["sistOppdatert"] shouldBe sistOppdatert
                    eventJson["tekst"] shouldBe tekst
                    eventJson["link"] shouldBe link
                    eventJson["aktiv"] shouldBe aktiv
                }
            }
        }
    }
}