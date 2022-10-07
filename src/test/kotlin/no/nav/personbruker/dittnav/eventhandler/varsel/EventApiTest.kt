package no.nav.personbruker.dittnav.eventhandler.varsel

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.mockk
import no.nav.personbruker.dittnav.eventhandler.common.EventType
import no.nav.personbruker.dittnav.eventhandler.mockEventHandlerApi
import org.junit.jupiter.api.Test
import java.time.ZoneId
import java.time.ZonedDateTime

class EventApiTest {

    private val fodselsnummer = "12354"
    private val grupperingsId = "123"
    private val eventId = "456"
    private val eventTidspunkt = ZonedDateTime.of(2020, 1, 1, 1, 1, 1, 1, ZoneId.of("Europe/Oslo"))
    private val forstBehandlet = ZonedDateTime.of(2020, 1, 1, 1, 1, 1, 1, ZoneId.of("Europe/Oslo"))
    private val produsent = "produsent"
    private val sikkerhetsnivaa = 4
    private val sistOppdatert = ZonedDateTime.of(2020, 1, 1, 1, 1, 1, 1, ZoneId.of("Europe/Oslo"))
    private val tekst = "tekst"
    private val link = "link"
    private val aktiv = false
    private val type = EventType.BESKJED

    private val event = Varsel(
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
        type = type,
        forstBehandlet = forstBehandlet
    )

    @Test
    fun `eventer-apiet skal returnere inaktive eventer`() {
        val eventRepositoryMock: VarselRepository = mockk()
        coEvery {
            eventRepositoryMock.getInactiveVarsel(any())
        }.returns(
            listOf(
                event
            )
        )

        testApplication {
            mockEventHandlerApi(eventRepository = eventRepositoryMock)
            val response = client.get("dittnav-event-handler/fetch/event/inaktive")

            response.status shouldBe HttpStatusCode.OK

            val eventJson = ObjectMapper().readTree(response.bodyAsText())[0]
            eventJson["grupperingsId"].asText() shouldBe grupperingsId
            eventJson["eventId"].asText() shouldBe eventId
            ZonedDateTime.parse(eventJson["eventTidspunkt"].asText()) shouldBe eventTidspunkt
            eventJson["produsent"].asText() shouldBe produsent
            eventJson["sikkerhetsnivaa"].asInt() shouldBe sikkerhetsnivaa
            ZonedDateTime.parse(eventJson["sistOppdatert"].asText()) shouldBe sistOppdatert
            eventJson["tekst"].asText() shouldBe tekst
            eventJson["link"].asText() shouldBe link
            eventJson["aktiv"].asBoolean() shouldBe aktiv
            ZonedDateTime.parse(eventJson["forstBehandlet"].asText()) shouldBe forstBehandlet
        }
    }

    @Test
    fun `eventer-apiet skal returnere aktive eventer`() {
        val eventRepositoryMock: VarselRepository = mockk()
        coEvery {
            eventRepositoryMock.getActiveVarsel(any())
        }.returns(
            listOf(
                event
            )
        )
        testApplication {
            mockEventHandlerApi(eventRepository = eventRepositoryMock)
            val response = client.get("dittnav-event-handler/fetch/event/aktive")
            response.status shouldBe HttpStatusCode.OK
            val eventJson = ObjectMapper().readTree(response.bodyAsText())[0]
            eventJson["eventId"].asText() shouldBe eventId
        }
    }
}
