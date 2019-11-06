package no.nav.personbruker.dittnav.eventhandler.informasjon

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import no.nav.personbruker.dittnav.eventhandler.config.extractIdentFromToken

fun Route.informasjonApi(informasjonEventService: InformasjonEventService) {

    get("/fetch/informasjon") {
        val ident = extractIdentFromToken()
        val events = informasjonEventService.getEventsFromCacheForUser(ident)
        call.respond(HttpStatusCode.OK, events)
    }

    get("/fetch/informasjon/all") {
        val ident = extractIdentFromToken()
        val events = informasjonEventService.getAllEventsFromCacheForUser(ident)
        call.respond(HttpStatusCode.OK, events)
    }

}
