package no.nav.personbruker.dittnav.eventhandler.statusoppdatering

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.respondWithError
import no.nav.personbruker.dittnav.eventhandler.config.innloggetBruker
import org.slf4j.LoggerFactory

fun Route.statusoppdateringApi(statusoppdateringEventService: StatusoppdateringEventService) {

    val log = LoggerFactory.getLogger(StatusoppdateringEventService::class.java)

    get("/fetch/statusoppdatering/grouped") {
        try {
            val statusoppdateringEvents =
                    statusoppdateringEventService.getAllGroupedEventsFromCacheForUser(innloggetBruker,
                            call.request.queryParameters["grupperingsid"],
                            call.request.queryParameters["produsent"])
            call.respond(HttpStatusCode.OK, statusoppdateringEvents)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/fetch/grouped/systemuser/statusoppdatering") {
        try {
            val statusoppdateringEvents =
                    statusoppdateringEventService.getAllGroupedEventsBySystemuserFromCache()
            call.respond(HttpStatusCode.OK, statusoppdateringEvents)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }
}

