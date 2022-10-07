package no.nav.personbruker.dittnav.eventhandler.varsel

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import mu.KotlinLogging
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.respondWithError
import no.nav.personbruker.dittnav.eventhandler.config.innloggetBruker

fun Route.eventApi(eventRepository: VarselRepository) {

    val log = KotlinLogging.logger {}

    get("/fetch/event/inaktive") {
        try {
            val inactiveEventDTOs = eventRepository.getInactiveVarsel(innloggetBruker.ident)
                .map { event -> event.toEventDTO() }
            call.respond(HttpStatusCode.OK, inactiveEventDTOs)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/fetch/event/aktive") {
        try {
            val inactiveEventDTOs = eventRepository.getActiveVarsel(innloggetBruker.ident)
                .map { event -> event.toEventDTO() }
            call.respond(HttpStatusCode.OK, inactiveEventDTOs)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }
}
