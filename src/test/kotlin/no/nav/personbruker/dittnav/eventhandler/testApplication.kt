package no.nav.personbruker.dittnav.eventhandler


import Beskjed
import com.fasterxml.jackson.databind.JsonNode
import io.ktor.server.application.Application
import io.ktor.server.testing.TestApplicationBuilder
import io.mockk.mockk
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedEventService
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.common.health.HealthService
import no.nav.personbruker.dittnav.eventhandler.config.eventHandlerApi
import no.nav.personbruker.dittnav.eventhandler.done.DoneEventService
import no.nav.personbruker.dittnav.eventhandler.event.EventRepository
import no.nav.personbruker.dittnav.eventhandler.innboks.Innboks
import no.nav.personbruker.dittnav.eventhandler.innboks.InnboksEventService
import no.nav.personbruker.dittnav.eventhandler.oppgave.Oppgave
import no.nav.personbruker.dittnav.eventhandler.oppgave.OppgaveEventService
import no.nav.personbruker.dittnav.eventhandler.statistics.EventStatisticsService
import no.nav.tms.token.support.authentication.installer.mock.installMockedAuthenticators
import no.nav.tms.token.support.tokenx.validation.mock.SecurityLevel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import java.time.ZonedDateTime

const val apiTestfnr = "12345678910"
fun TestApplicationBuilder.mockEventHandlerApi(
    healthService: HealthService = mockk(relaxed = true),
    beskjedEventService: BeskjedEventService = mockk(relaxed = true),
    oppgaveEventService: OppgaveEventService = mockk(relaxed = true),
    innboksEventService: InnboksEventService = mockk(relaxed = true),
    doneEventService: DoneEventService = mockk(relaxed = true),
    eventRepository: EventRepository = mockk(relaxed = true),
    eventStatisticsService: EventStatisticsService = mockk(relaxed = true),
    database: Database = mockk(relaxed = true),
    installAuthenticatorsFunction: Application.() -> Unit = {
        installMockedAuthenticators {
            installTokenXAuthMock {
                setAsDefault = true

                alwaysAuthenticated = true
                staticUserPid = apiTestfnr
                staticSecurityLevel = SecurityLevel.LEVEL_4
            }
            installAzureAuthMock { }
        }
    }
) {
    application {
        eventHandlerApi(
            healthService = healthService,
            beskjedEventService = beskjedEventService,
            oppgaveEventService = oppgaveEventService,
            innboksEventService = innboksEventService,
            doneEventService = doneEventService,
            eventRepository = eventRepository,
            eventStatisticsService = eventStatisticsService,
            database = database,
            installAuthenticatorsFunction = installAuthenticatorsFunction,
            installShutdownHook = {}
        )
    }
}

internal class ComparableVarsel(
    val eventId: String,
    private val fodselsnummer: String,
    private val grupperingsId: String,
    private val forstBehandlet: ZonedDateTime,
    private val produsent: String,
    private val sikkerhetsnivaa: Int,
    private val sistOppdatert: ZonedDateTime,
    private val tekst: String,
    private val link: String,
    private val aktiv: Boolean,
    private val eksternVarslingSendt: Boolean,
    private val eksternVarslingKanaler: List<String>
) {

    private fun assertResultEquals(result: ComparableVarsel) {
        assertEquals(fodselsnummer, result.fodselsnummer, "fodselsnummer")
        assertEquals(grupperingsId, result.grupperingsId, "grupperingsid")
        assertEquals(eventId, result.eventId, "eventId")
        assertEquals(forstBehandlet, result.forstBehandlet, "forstBehandlet")
        assertEquals(produsent, result.produsent, "produsent")
        assertEquals(sikkerhetsnivaa, result.sikkerhetsnivaa, "sikkerhetsnivaa")
        assertEquals(sistOppdatert, result.sistOppdatert, "sistOppdatert")
        assertEquals(tekst, result.tekst, "tekst")
        assertEquals(link, result.link, "tekst")
        assertEquals(aktiv, result.aktiv)
        assertEquals(eksternVarslingSendt, result.eksternVarslingSendt, "eksternVarslingSendt")
        assertEquals(
            eksternVarslingKanaler.size,
            result.eksternVarslingKanaler.size,
            "eksternVarslingKanaler: ${result.eksternVarslingKanaler} har ikke samme lengde som $eksternVarslingKanaler}"
        )
        result.eksternVarslingKanaler.forEach { res ->
            assertTrue(
                eksternVarslingKanaler.any { res == it },
                "eksternVarslingKanaler: fant ikke $res i $eksternVarslingKanaler"
            )
        }
    }

    infix fun shouldEqual(expectedBeskjed: Beskjed) {
        this.assertResultEquals(expectedBeskjed.toCompparableVarsel())
    }

    infix fun shouldEqual(expectedInnboks: Innboks) {
        this.assertResultEquals(expectedInnboks.toCompparableVarsel())
    }

    infix fun shouldEqual(expectedOppgave: Oppgave) {
        this.assertResultEquals(expectedOppgave.toCompparableVarsel())
    }
}


private fun Beskjed.toCompparableVarsel() = ComparableVarsel(
    fodselsnummer = this.fodselsnummer,
    grupperingsId = this.grupperingsId,
    eventId = this.eventId,
    forstBehandlet = this.forstBehandlet,
    produsent = this.appnavn,
    sikkerhetsnivaa = this.sikkerhetsnivaa,
    sistOppdatert = this.sistOppdatert,
    tekst = this.tekst,
    link = this.link,
    aktiv = this.aktiv,
    eksternVarslingSendt = eksternVarslingInfo.sendt,
    eksternVarslingKanaler = eksternVarslingInfo.sendteKanaler
)

private fun Innboks.toCompparableVarsel() = ComparableVarsel(
    fodselsnummer = this.fodselsnummer,
    grupperingsId = this.grupperingsId,
    eventId = this.eventId,
    forstBehandlet = this.forstBehandlet,
    produsent = this.appnavn,
    sikkerhetsnivaa = this.sikkerhetsnivaa,
    sistOppdatert = this.sistOppdatert,
    tekst = this.tekst,
    link = this.link,
    aktiv = this.aktiv,
    eksternVarslingSendt = eksternVarslingInfo.sendt,
    eksternVarslingKanaler = eksternVarslingInfo.sendteKanaler
)


private fun Oppgave.toCompparableVarsel(): ComparableVarsel = ComparableVarsel(
    fodselsnummer = this.fodselsnummer,
    grupperingsId = this.grupperingsId,
    eventId = this.eventId,
    forstBehandlet = this.forstBehandlet,
    produsent = this.appnavn,
    sikkerhetsnivaa = this.sikkerhetsnivaa,
    sistOppdatert = this.sistOppdatert,
    tekst = this.tekst,
    link = this.link,
    aktiv = this.aktiv,
    eksternVarslingSendt = eksternVarslingInfo.sendt,
    eksternVarslingKanaler = eksternVarslingInfo.sendteKanaler
)

internal fun JsonNode.asZonedDateTime(): ZonedDateTime = ZonedDateTime.parse(this.asText()).minusHours(2)


