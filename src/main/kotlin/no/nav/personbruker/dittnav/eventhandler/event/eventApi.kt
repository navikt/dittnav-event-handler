package no.nav.personbruker.dittnav.eventhandler.event

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.respondWithError
import no.nav.personbruker.dittnav.eventhandler.config.innloggetBruker
import org.slf4j.LoggerFactory

fun Route.eventApi(eventRepository: EventRepository) {

    val log = LoggerFactory.getLogger(EventRepository::class.java)

    get("/fetch/event/inaktive") {
        try {
            val inactiveEventDTOs = eventRepository.getInactiveEvents(innloggetBruker.ident)
                    .map { event -> event.toEventDTO() }
            call.respond(HttpStatusCode.OK, inactiveEventDTOs)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }
}