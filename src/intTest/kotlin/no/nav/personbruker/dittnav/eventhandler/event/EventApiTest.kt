package no.nav.personbruker.dittnav.eventhandler.event

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.shouldBe
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import io.mockk.coEvery
import io.mockk.mockk
import mockEventHandlerApi
import org.junit.jupiter.api.Test
import java.time.ZoneId
import java.time.ZonedDateTime

class EventApiTest {

    @Test
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

        val eventRepositoryMock: EventRepository = mockk()
        coEvery {
            eventRepositoryMock.getInactiveEvents(any())
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

        val response = withTestApplication(
            mockEventHandlerApi(eventRepository = eventRepositoryMock)
        ) {
            handleRequest(HttpMethod.Get, "/fetch/event/inaktive") {}
        }.response

        response.status() shouldBe HttpStatusCode.OK

        val eventJson = ObjectMapper().readTree(response.content)[0]
        eventJson["grupperingsId"].asText() shouldBe grupperingsId
        eventJson["eventId"].asText() shouldBe eventId
        ZonedDateTime.parse(eventJson["eventTidspunkt"].asText()) shouldBe eventTidspunkt
        eventJson["produsent"].asText() shouldBe produsent
        eventJson["sikkerhetsnivaa"].asInt() shouldBe sikkerhetsnivaa
        ZonedDateTime.parse(eventJson["sistOppdatert"].asText()) shouldBe sistOppdatert
        eventJson["tekst"].asText() shouldBe tekst
        eventJson["link"].asText() shouldBe link
        eventJson["aktiv"].asBoolean() shouldBe aktiv
    }
}