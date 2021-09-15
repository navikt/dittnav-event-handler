package no.nav.personbruker.dittnav.eventhandler.config

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import io.prometheus.client.hotspot.DefaultExports
import no.nav.personbruker.dittnav.common.util.config.StringEnvVar
import no.nav.personbruker.dittnav.eventhandler.beskjed.beskjedApi
import no.nav.personbruker.dittnav.eventhandler.beskjed.beskjedSystemClientApi
import no.nav.personbruker.dittnav.eventhandler.brukernotifikasjon.brukernotifikasjoner
import no.nav.personbruker.dittnav.eventhandler.common.health.healthApi
import no.nav.personbruker.dittnav.eventhandler.common.produsent.producerNameAliasApi
import no.nav.personbruker.dittnav.eventhandler.done.doneApi
import no.nav.personbruker.dittnav.eventhandler.done.doneSystemClientApi
import no.nav.personbruker.dittnav.eventhandler.innboks.innboksApi
import no.nav.personbruker.dittnav.eventhandler.innboks.innboksSystemClientApi
import no.nav.personbruker.dittnav.eventhandler.oppgave.oppgaveApi
import no.nav.personbruker.dittnav.eventhandler.oppgave.oppgaveSystemClientApi
import no.nav.personbruker.dittnav.eventhandler.statusoppdatering.statusoppdateringApi
import no.nav.personbruker.dittnav.eventhandler.statusoppdatering.statusoppdateringSystemClientApi
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
        installAzureAuth {
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
            doneSystemClientApi(appContext.doneEventService)
            beskjedSystemClientApi(appContext.beskjedEventService)
            innboksSystemClientApi(appContext.innboksEventService)
            oppgaveSystemClientApi(appContext.oppgaveEventService)
            statusoppdateringSystemClientApi(appContext.statusoppdateringEventService)
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
    get(): TokenXUser {
        val claimName = StringEnvVar.getOptionalEnvVar("OIDC_CLAIM_CONTAINING_THE_IDENTITY")
        return if(claimName.isNullOrEmpty()) {
            TokenXUserFactory.createTokenXUser(call)
        } else {
            TokenXUserFactory.createTokenXUser(call, claimName)
        }
    }
