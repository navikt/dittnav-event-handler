package no.nav.personbruker.dittnav.eventhandler.beskjed

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import no.nav.personbruker.dittnav.eventhandler.config.userIdent

fun Route.beskjedApi(beskjedEventService: BeskjedEventService) {

    get("/fetch/beskjed") {
        beskjedEventService.getEventsFromCacheForUser(userIdent).let { events ->
            call.respond(HttpStatusCode.OK, events)
        }
    }

    get("/fetch/beskjed/all") {
        beskjedEventService.getEventsFromCacheForUser(userIdent).let { events ->
            call.respond(HttpStatusCode.OK, events)
        }
    }

    get("/fetch/informasjon") {
        beskjedEventService.getEventsFromCacheForUser(userIdent).let { events ->
            call.respond(HttpStatusCode.OK, events)
        }
    }

    get("/fetch/informasjon/all") {
        beskjedEventService.getEventsFromCacheForUser(userIdent).let { events ->
            call.respond(HttpStatusCode.OK, events)
        }
    }

}
