package no.nav.personbruker.dittnav.eventhandler.statusOppdatering

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.respondWithError
import no.nav.personbruker.dittnav.eventhandler.config.innloggetBruker
import org.slf4j.LoggerFactory

fun Route.statusOppdateringApi(statusOppdateringEventService: StatusOppdateringEventService) {

    val log = LoggerFactory.getLogger(StatusOppdateringEventService::class.java)

    get("/fetch/statusOppdatering/all") {
        try {
            val statusOppdateringEvents = statusOppdateringEventService.getAllEventsFromCacheForUser(innloggetBruker)
            call.respond(HttpStatusCode.OK, statusOppdateringEvents)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

}