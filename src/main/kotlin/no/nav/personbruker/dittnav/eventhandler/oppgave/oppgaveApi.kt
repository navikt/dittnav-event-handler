package no.nav.personbruker.dittnav.eventhandler.oppgave

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import no.nav.personbruker.dittnav.eventhandler.config.innloggetBruker

fun Route.oppgaveApi(oppgaveEventService: OppgaveEventService) {

    get("/fetch/oppgave/aktive") {
        oppgaveEventService.getActiveCachedEventsForUser(innloggetBruker).let { events ->
            call.respond(HttpStatusCode.OK, events)
        }
    }

    get("/fetch/oppgave/inaktive") {
        oppgaveEventService.getInactiveCachedEventsForUser(innloggetBruker).let { events ->
            call.respond(HttpStatusCode.OK, events)
        }
    }

    get("/fetch/oppgave/all") {
        oppgaveEventService.getAllCachedEventsForUser(innloggetBruker).let { events ->
            call.respond(HttpStatusCode.OK, events)
        }
    }
}
