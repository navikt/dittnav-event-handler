package no.nav.personbruker.dittnav.eventhandler.config


import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.ApplicationStopPreparing
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.authenticate
import io.ktor.server.metrics.micrometer.MicrometerMetrics
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.util.pipeline.PipelineContext
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import no.nav.personbruker.dittnav.common.util.config.StringEnvVar
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedEventService
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedNotFoundException
import no.nav.personbruker.dittnav.eventhandler.beskjed.beskjedApi
import no.nav.personbruker.dittnav.eventhandler.beskjed.beskjedSystemClientApi
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.common.health.HealthService
import no.nav.personbruker.dittnav.eventhandler.common.health.healthApi
import no.nav.personbruker.dittnav.eventhandler.done.DoneEventService
import no.nav.personbruker.dittnav.eventhandler.done.doneSystemClientApi
import no.nav.personbruker.dittnav.eventhandler.varsel.VarselRepository
import no.nav.personbruker.dittnav.eventhandler.varsel.varselApi
import no.nav.personbruker.dittnav.eventhandler.innboks.InnboksEventService
import no.nav.personbruker.dittnav.eventhandler.innboks.innboksApi
import no.nav.personbruker.dittnav.eventhandler.innboks.innboksSystemClientApi
import no.nav.personbruker.dittnav.eventhandler.oppgave.OppgaveEventService
import no.nav.personbruker.dittnav.eventhandler.oppgave.oppgaveApi
import no.nav.personbruker.dittnav.eventhandler.oppgave.oppgaveSystemClientApi
import no.nav.personbruker.dittnav.eventhandler.statistics.EventStatisticsService
import no.nav.personbruker.dittnav.eventhandler.statistics.statisticsSystemClientApi
import no.nav.personbruker.dittnav.eventhandler.varsel.eventApi
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
    doneEventService: DoneEventService,
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
        exception<BeskjedNotFoundException> { call, cause ->
            log.warn { cause.message }
            call.respond(status = HttpStatusCode.BadRequest, message = cause.message.toString())
        }
    }

    routing {
        route("/dittnav-event-handler") {
            healthApi(healthService, prometheusMeterRegistry)
            authenticate {
                beskjedApi(beskjedEventService)
                innboksApi(innboksEventService)
                oppgaveApi(oppgaveEventService)
                eventApi(varselRepository)
            }
            authenticate(AzureAuthenticator.name) {
                doneSystemClientApi(doneEventService)
                beskjedSystemClientApi(beskjedEventService)
                innboksSystemClientApi(innboksEventService)
                oppgaveSystemClientApi(oppgaveEventService)
                varselApi(varselRepository)

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
        closeTheDatabaseConectionPool(database)
    }
}

private fun closeTheDatabaseConectionPool(database: Database) {
    database.close()
}

val PipelineContext<Unit, ApplicationCall>.innloggetBruker: TokenXUser
    get():  TokenXUser {
        val claimName = StringEnvVar.getOptionalEnvVar("OIDC_CLAIM_CONTAINING_THE_IDENTITY")
        return if (claimName.isNullOrEmpty()) {
            TokenXUserFactory.createTokenXUser(call)
        } else {
            TokenXUserFactory.createTokenXUser(call, claimName)
        }
    }
