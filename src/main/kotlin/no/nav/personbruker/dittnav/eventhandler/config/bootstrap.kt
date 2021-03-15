package no.nav.personbruker.dittnav.eventhandler.config

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import io.prometheus.client.hotspot.DefaultExports
import no.nav.personbruker.dittnav.eventhandler.beskjed.beskjedApi
import no.nav.personbruker.dittnav.eventhandler.brukernotifikasjon.brukernotifikasjoner
import no.nav.personbruker.dittnav.eventhandler.common.InnloggetBruker
import no.nav.personbruker.dittnav.eventhandler.common.InnloggetBrukerFactory
import no.nav.personbruker.dittnav.eventhandler.common.health.healthApi
import no.nav.personbruker.dittnav.eventhandler.common.produsent.producerNameAliasApi
import no.nav.personbruker.dittnav.eventhandler.done.doneApi
import no.nav.personbruker.dittnav.eventhandler.innboks.innboksApi
import no.nav.personbruker.dittnav.eventhandler.oppgave.oppgaveApi
import no.nav.personbruker.dittnav.eventhandler.statusoppdatering.statusoppdateringApi
import no.nav.security.token.support.ktor.tokenValidationSupport

@KtorExperimentalAPI
fun Application.mainModule(appContext: ApplicationContext = ApplicationContext()) {
    DefaultExports.initialize()
    install(DefaultHeaders)

    install(ContentNegotiation) {
        json()
    }

    val config = this.environment.config

    install(Authentication) {
        tokenValidationSupport(config = config)
    }

    routing {
        healthApi(appContext.healthService)
        producerNameAliasApi(appContext.producerNameAliasService)
        authenticate {
            doneApi(appContext.doneEventService)
            beskjedApi(appContext.beskjedEventService)
            innboksApi(appContext.innboksEventService)
            oppgaveApi(appContext.oppgaveEventService)
            brukernotifikasjoner(appContext.brukernotifikasjonService)
            statusoppdateringApi(appContext.statusoppdateringEventService)
        }
    }

    configureShutdownHook(appContext)
}

private fun Application.configureShutdownHook(appContext: ApplicationContext) {
    environment.monitor.subscribe(ApplicationStopPreparing) {
        closeTheDatabaseConectionPool(appContext)
        appContext.kafkaProducerDone.flushAndClose()
        appContext.kafkaProducerDoneBackup.flushAndClose()
        appContext.kafkaProducerBeskjedBackup.flushAndClose()
        appContext.kafkaProducerOppgaveBackup.flushAndClose()
        appContext.kafkaProducerTableDoneBackup.flushAndClose()
    }
}

private fun closeTheDatabaseConectionPool(appContext: ApplicationContext) {
    appContext.database.dataSource.close()
}

val PipelineContext<Unit, ApplicationCall>.innloggetBruker: InnloggetBruker
    get() = InnloggetBrukerFactory.createNewInnloggetBruker(call.authentication.principal())
