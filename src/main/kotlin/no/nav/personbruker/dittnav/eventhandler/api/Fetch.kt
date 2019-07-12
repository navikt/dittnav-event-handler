package no.nav.personbruker.dittnav.eventhandler.api

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import no.nav.personbruker.dittnav.eventhandler.config.extractIdentFromLoginContext
import no.nav.personbruker.dittnav.eventhandler.service.InformasjonEventService

fun Route.fetchEventsApi() {

    val informasjonEventService = InformasjonEventService()

    get("/fetch/informasjon") {
        val ident = extractIdentFromLoginContext()
        val events = informasjonEventService.getEventsFromCacheForUser(ident)
        call.respond(HttpStatusCode.OK, events)
    }

}
