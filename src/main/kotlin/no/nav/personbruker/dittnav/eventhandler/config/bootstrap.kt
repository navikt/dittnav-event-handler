package no.nav.personbruker.dittnav.eventhandler.config


import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.metrics.micrometer.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedEventService
import no.nav.personbruker.dittnav.eventhandler.beskjed.beskjedApi
import no.nav.personbruker.dittnav.eventhandler.beskjed.beskjedSystemClientApi
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.common.health.HealthService
import no.nav.personbruker.dittnav.eventhandler.common.health.healthApi
import no.nav.personbruker.dittnav.eventhandler.innboks.InnboksEventService
import no.nav.personbruker.dittnav.eventhandler.innboks.innboksApi
import no.nav.personbruker.dittnav.eventhandler.innboks.innboksSystemClientApi
import no.nav.personbruker.dittnav.eventhandler.oppgave.OppgaveEventService
import no.nav.personbruker.dittnav.eventhandler.oppgave.oppgaveApi
import no.nav.personbruker.dittnav.eventhandler.oppgave.oppgaveSystemClientApi
import no.nav.personbruker.dittnav.eventhandler.statistics.EventStatisticsService
import no.nav.personbruker.dittnav.eventhandler.statistics.statisticsSystemClientApi
import no.nav.personbruker.dittnav.eventhandler.varsel.VarselRepository
import no.nav.personbruker.dittnav.eventhandler.varsel.oboVarselApi
import no.nav.personbruker.dittnav.eventhandler.varsel.varselApi
import no.nav.tms.token.support.authentication.installer.installAuthenticators
import no.nav.tms.token.support.azure.validation.AzureAuthenticator
import no.nav.tms.token.support.tokenx.validation.user.TokenXUser
import no.nav.tms.token.support.tokenx.validation.user.TokenXUserFactory

val log = KotlinLogging.logger {}

fun Application.eventHandlerApi(
    healthService: HealthService,
    beskjedEventService: BeskjedEventService,
    oppgaveEventService: OppgaveEventService,
    innboksEventService: InnboksEventService,
    varselRepository: VarselRepository,
    eventStatisticsService: EventStatisticsService,
    database: Database,
    installAuthenticatorsFunction: Application.() -> Unit = installAuth(),
    installShutdownHook: (database: Database) -> Unit = { configureShutdownHook(database) }
) {
    val prometheusMeterRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

    install(DefaultHeaders)
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }

    install(MicrometerMetrics) {
        registry = prometheusMeterRegistry
    }

    installAuthenticatorsFunction()
    install(StatusPages) {
        configureErrorResponses()
    }

    routing {
        route("/dittnav-event-handler") {
            healthApi(healthService, prometheusMeterRegistry)
            authenticate {
                beskjedApi(beskjedEventService)
                innboksApi(innboksEventService)
                oppgaveApi(oppgaveEventService)
                varselApi(varselRepository)
            }
            authenticate(AzureAuthenticator.name) {
                beskjedSystemClientApi(beskjedEventService)
                innboksSystemClientApi(innboksEventService)
                oppgaveSystemClientApi(oppgaveEventService)
                oboVarselApi(varselRepository)
                statisticsSystemClientApi(eventStatisticsService)
            }
        }
    }
    installShutdownHook(database)
}


private fun installAuth(): Application.() -> Unit = {
    installAuthenticators {
        installTokenXAuth {
            setAsDefault = true
        }
        installAzureAuth {
            setAsDefault = false
        }
    }
}

private fun Application.configureShutdownHook(database: Database) {
    environment.monitor.subscribe(ApplicationStopPreparing) {
        database.close()
    }
}

val PipelineContext<Unit, ApplicationCall>.innloggetBruker: TokenXUser
    get():  TokenXUser =
        TokenXUserFactory.createTokenXUser(call)


