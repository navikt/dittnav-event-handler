package no.nav.personbruker.dittnav.eventhandler.innboks

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import no.nav.personbruker.dittnav.eventhandler.config.userIdent

fun Route.innboksApi(innboksEventService: InnboksEventService) {

    get("/fetch/innboks") {
        innboksEventService.getCachedActiveEventsForUser(userIdent).let { events->
            call.respond(HttpStatusCode.OK, events)
        }
    }

    get("/fetch/innboks/all") {
        innboksEventService.getAllCachedEventsForUser(userIdent).let { events ->
            call.respond(HttpStatusCode.OK, events)
        }
    }
}