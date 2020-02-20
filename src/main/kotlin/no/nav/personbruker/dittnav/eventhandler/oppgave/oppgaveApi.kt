package no.nav.personbruker.dittnav.eventhandler.oppgave

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import no.nav.personbruker.dittnav.eventhandler.common.innloggetBruker

fun Route.oppgaveApi(oppgaveEventService: OppgaveEventService) {

    get("/fetch/oppgave") {
        oppgaveEventService.getEventsFromCacheForUser(innloggetBruker).let { events ->
            call.respond(HttpStatusCode.OK, events)
        }
    }

    get("/fetch/oppgave/all") {
        oppgaveEventService.getAllEventsFromCacheForUser(innloggetBruker).let { events ->
            call.respond(HttpStatusCode.OK, events)
        }
    }

}
