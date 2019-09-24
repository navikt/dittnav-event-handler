package no.nav.personbruker.dittnav.eventhandler.api

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import no.nav.personbruker.dittnav.eventhandler.config.extractIdentFromLoginContext
import no.nav.personbruker.dittnav.eventhandler.service.InformasjonEventService
import no.nav.personbruker.dittnav.eventhandler.service.OppgaveEventService

fun Route.fetchEventsApi() {

    val informasjonEventService = InformasjonEventService()
    val oppgaveEventService = OppgaveEventService()

    get("/fetch/informasjon") {
        val ident = extractIdentFromLoginContext()
        val events = informasjonEventService.getEventsFromCacheForUser(ident)
        call.respond(HttpStatusCode.OK, events)
    }

    get("/fetch/oppgave") {
        val ident = extractIdentFromLoginContext()
        val events = oppgaveEventService.getEventsFromCacheForUser(ident)
        call.respond(HttpStatusCode.OK, events)
    }

}
