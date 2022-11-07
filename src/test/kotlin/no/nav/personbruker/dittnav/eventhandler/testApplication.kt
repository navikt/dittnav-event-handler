package no.nav.personbruker.dittnav.eventhandler

import com.fasterxml.jackson.databind.JsonNode
import io.ktor.server.application.Application
import io.ktor.server.testing.TestApplicationBuilder
import io.mockk.mockk
import no.nav.personbruker.dittnav.eventhandler.beskjed.Beskjed
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedEventService
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.common.health.HealthService
import no.nav.personbruker.dittnav.eventhandler.config.eventHandlerApi
import no.nav.personbruker.dittnav.eventhandler.done.DoneEventService
import no.nav.personbruker.dittnav.eventhandler.varsel.VarselRepository
import no.nav.personbruker.dittnav.eventhandler.innboks.Innboks
import no.nav.personbruker.dittnav.eventhandler.innboks.InnboksEventService
import no.nav.personbruker.dittnav.eventhandler.oppgave.Oppgave
import no.nav.personbruker.dittnav.eventhandler.oppgave.OppgaveEventService
import no.nav.personbruker.dittnav.eventhandler.statistics.EventStatisticsService
import no.nav.tms.token.support.authentication.installer.mock.installMockedAuthenticators
import no.nav.tms.token.support.tokenx.validation.mock.SecurityLevel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

const val apiTestfnr = "12345678910"
fun TestApplicationBuilder.mockEventHandlerApi(
    healthService: HealthService = mockk(relaxed = true),
    beskjedEventService: BeskjedEventService = mockk(relaxed = true),
    oppgaveEventService: OppgaveEventService = mockk(relaxed = true),
    innboksEventService: InnboksEventService = mockk(relaxed = true),
    doneEventService: DoneEventService = mockk(relaxed = true),
    eventRepository: VarselRepository = mockk(relaxed = true),
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
            installAzureAuthMock {}
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
            varselRepository = eventRepository,
            eventStatisticsService = eventStatisticsService,
            database = database,
            installAuthenticatorsFunction = installAuthenticatorsFunction,
            installShutdownHook = {}
        )
    }
}

internal class ComparableVarsel(
    sistOppdatert: ZonedDateTime,
    forstBehandlet: ZonedDateTime,
    val eventId: String,
    private val fodselsnummer: String,
    private val grupperingsId: String,
    private val produsent: String,
    private val sikkerhetsnivaa: Int,
    private val tekst: String,
    private val link: String,
    private val aktiv: Boolean,
    private val eksternVarslingSendt: Boolean,
    private val eksternVarslingKanaler: List<String>
) {
    private val sistOppdatert =
        sistOppdatert.withZoneSameInstant(ZoneId.of("Europe/Oslo")).truncatedTo(ChronoUnit.SECONDS)
    private val forstBehandlet =
        forstBehandlet.withZoneSameInstant(ZoneId.of("Europe/Oslo")).truncatedTo(ChronoUnit.SECONDS)

    private fun assertThisEquals(expected: ComparableVarsel) {
        val eventId = expected.eventId
        assertEquals(expected.fodselsnummer, this.fodselsnummer, "fodselsnummer")
        assertEquals(expected.grupperingsId, this.grupperingsId, "grupperingsid")
        assertEquals(expected.eventId, this.eventId, "eventId")
        assertEquals(expected.forstBehandlet, this.forstBehandlet, "forstBehandlet")
        assertEquals(expected.produsent, this.produsent, "produsent")
        assertEquals(expected.sikkerhetsnivaa, this.sikkerhetsnivaa, "sikkerhetsnivaa")
        assertEquals(expected.sistOppdatert, this.sistOppdatert, "sistOppdatert")
        assertEquals(expected.tekst, this.tekst, "tekst")
        assertEquals(expected.link, this.link, "tekst")
        assertEquals(expected.aktiv, this.aktiv)
        assertEquals(expected.eksternVarslingSendt, this.eksternVarslingSendt, "eksternVarslingSendt")
        assertEquals(
            eksternVarslingKanaler.size,
            this.eksternVarslingKanaler.size,
            "eksternVarslingKanaler: ${this.eksternVarslingKanaler} har ikke samme lengde som $eksternVarslingKanaler}"
        )
        this.eksternVarslingKanaler.forEach { res ->
            assertTrue(
                eksternVarslingKanaler.any { res == it },
                "eksternVarslingKanaler: fant ikke $res i $eksternVarslingKanaler"
            )
        }
    }

    infix fun shouldEqual(expectedBeskjed: Beskjed) {
        this.assertThisEquals(expectedBeskjed.toCompparableVarsel())
    }

    infix fun shouldEqual(expectedInnboks: Innboks) {
        this.assertThisEquals(expectedInnboks.toCompparableVarsel())
    }

    infix fun shouldEqual(expectedOppgave: Oppgave) {
        this.assertThisEquals(expectedOppgave.toCompparableVarsel())
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
    eksternVarslingSendt = eksternVarslingSendt,
    eksternVarslingKanaler = eksternVarslingKanaler
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

internal fun JsonNode.asZonedDateTime(): ZonedDateTime =
    ZonedDateTime.parse(this.asText())

internal object OsloDateTime {
    private val zoneID = ZoneId.of("Europe/Oslo")
    internal fun now(): ZonedDateTime {
        return ZonedDateTime.ofInstant(Instant.now(), zoneID)
    }
}

internal inline fun <T> T.assert(block: T.() -> Unit): T =
    apply {
        block()
    }
