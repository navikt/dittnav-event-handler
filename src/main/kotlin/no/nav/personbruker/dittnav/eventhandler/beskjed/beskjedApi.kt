package no.nav.personbruker.dittnav.eventhandler.beskjed

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import no.nav.personbruker.dittnav.eventhandler.common.innloggetBruker

fun Route.beskjedApi(beskjedEventService: BeskjedEventService) {

    get("/fetch/beskjed/aktive") {
        beskjedEventService.getActiveCachedEventsForUser(innloggetBruker).let { events ->
            call.respond(HttpStatusCode.OK, events)
        }
    }

    get("/fetch/beskjed/inaktive") {
        beskjedEventService.getInactiveCachedEventsForUser(innloggetBruker).let { events ->
            call.respond(HttpStatusCode.OK, events)
        }
    }

    get("/fetch/beskjed/all") {
        beskjedEventService.getAllEventsFromCacheForUser(innloggetBruker).let { events ->
            call.respond(HttpStatusCode.OK, events)
        }
    }
}
