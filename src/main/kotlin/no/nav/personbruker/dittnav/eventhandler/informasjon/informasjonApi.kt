package no.nav.personbruker.dittnav.eventhandler.informasjon

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import no.nav.personbruker.dittnav.eventhandler.config.userIdent

fun Route.informasjonApi(informasjonEventService: InformasjonEventService) {

    get("/fetch/informasjon") {
        informasjonEventService.getEventsFromCacheForUser(userIdent).let { events ->
            call.respond(HttpStatusCode.OK, events)
        }
    }

    get("/fetch/informasjon/all") {
        informasjonEventService.getEventsFromCacheForUser(userIdent).let { events ->
            call.respond(HttpStatusCode.OK, events)
        }
    }

}
