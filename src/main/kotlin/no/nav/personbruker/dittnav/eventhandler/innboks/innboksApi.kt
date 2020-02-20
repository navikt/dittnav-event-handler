package no.nav.personbruker.dittnav.eventhandler.innboks

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import no.nav.personbruker.dittnav.eventhandler.common.innloggetBruker

fun Route.innboksApi(innboksEventService: InnboksEventService) {

    get("/fetch/innboks") {
        innboksEventService.getCachedActiveEventsForUser(innloggetBruker).let { events->
            call.respond(HttpStatusCode.OK, events)
        }
    }

    get("/fetch/innboks/all") {
        innboksEventService.getAllCachedEventsForUser(innloggetBruker).let { events ->
            call.respond(HttpStatusCode.OK, events)
        }
    }
}