package no.nav.personbruker.dittnav.eventhandler.oppgave

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import no.nav.personbruker.dittnav.eventhandler.config.extractIdentFromToken

fun Route.oppgaveApi(oppgaveEventService: OppgaveEventService) {

    get("/fetch/oppgave") {
        val ident = extractIdentFromToken()
        val events = oppgaveEventService.getEventsFromCacheForUser(ident)
        call.respond(HttpStatusCode.OK, events)
    }

    get("/fetch/oppgave/all") {
        val ident = extractIdentFromToken()
        val events = oppgaveEventService.getAllEventsFromCacheForUser(ident)
        call.respond(HttpStatusCode.OK, events)
    }

}
