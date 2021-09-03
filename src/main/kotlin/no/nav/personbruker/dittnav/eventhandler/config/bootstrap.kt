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
import no.nav.personbruker.dittnav.eventhandler.beskjed.beskjedSystemuserApi
import no.nav.personbruker.dittnav.eventhandler.brukernotifikasjon.brukernotifikasjoner
import no.nav.personbruker.dittnav.eventhandler.common.health.healthApi
import no.nav.personbruker.dittnav.eventhandler.common.produsent.producerNameAliasApi
import no.nav.personbruker.dittnav.eventhandler.done.doneApi
import no.nav.personbruker.dittnav.eventhandler.done.doneSystemuserApi
import no.nav.personbruker.dittnav.eventhandler.innboks.innboksApi
import no.nav.personbruker.dittnav.eventhandler.innboks.innboksSystemuserApi
import no.nav.personbruker.dittnav.eventhandler.oppgave.oppgaveApi
import no.nav.personbruker.dittnav.eventhandler.oppgave.oppgaveSystemuserApi
import no.nav.personbruker.dittnav.eventhandler.statusoppdatering.statusoppdateringApi
import no.nav.personbruker.dittnav.eventhandler.statusoppdatering.statusoppdateringSystemuserApi
import no.nav.tms.token.support.authentication.installer.installAuthenticators
import no.nav.tms.token.support.azure.validation.AzureAuthenticator
import no.nav.tms.token.support.tokenx.validation.user.TokenXUser
import no.nav.tms.token.support.tokenx.validation.user.TokenXUserFactory

@KtorExperimentalAPI
fun Application.mainModule(appContext: ApplicationContext = ApplicationContext()) {
    DefaultExports.initialize()
    install(DefaultHeaders)

    install(ContentNegotiation) {
        json()
    }

    installAuthenticators {
        installTokenXAuth {
            setAsDefault = true
        }
        installAzureAuth{
            setAsDefault = false
        }
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
        authenticate(AzureAuthenticator.name) {
            doneSystemuserApi(appContext.doneEventService)
            beskjedSystemuserApi(appContext.beskjedEventService)
            innboksSystemuserApi(appContext.innboksEventService)
            oppgaveSystemuserApi(appContext.oppgaveEventService)
            statusoppdateringSystemuserApi(appContext.statusoppdateringEventService)
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

val PipelineContext<Unit, ApplicationCall>.innloggetBruker: TokenXUser
    get() = TokenXUserFactory.createTokenXUser(call)
