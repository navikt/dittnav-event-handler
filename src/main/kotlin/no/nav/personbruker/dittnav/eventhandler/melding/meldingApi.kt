package no.nav.personbruker.dittnav.eventhandler.melding

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import no.nav.personbruker.dittnav.eventhandler.config.userIdent

fun Route.meldingApi(meldingEventService: MeldingEventService) {

    get("/fetch/melding") {
        meldingEventService.getCachedActiveEventsForUser(userIdent).let { events->
            call.respond(HttpStatusCode.OK, events)
        }
    }

    get("/fetch/melding/all") {
        meldingEventService.getAllCachedEventsForUser(userIdent).let { events ->
            call.respond(HttpStatusCode.OK, events)
        }
    }
}