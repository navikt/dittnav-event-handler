package no.nav.personbruker.dittnav.eventhandler.beskjed

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.respondWithError
import no.nav.personbruker.dittnav.eventhandler.common.modia.doIfValidRequest
import no.nav.personbruker.dittnav.eventhandler.config.innloggetBruker
import org.slf4j.LoggerFactory

fun Route.beskjedApi(beskjedEventService: BeskjedEventService) {

    val log = LoggerFactory.getLogger(BeskjedEventService::class.java)

    get("/fetch/beskjed/aktive") {
        try {
            val aktiveBeskjedEventsDTO = beskjedEventService.getActiveCachedEventsForUser(innloggetBruker)
            call.respond(HttpStatusCode.OK, aktiveBeskjedEventsDTO)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/fetch/beskjed/inaktive") {
        try {
            val inaktiveBeskjedEvents = beskjedEventService.getInactiveCachedEventsForUser(innloggetBruker)
            call.respond(HttpStatusCode.OK, inaktiveBeskjedEvents)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/fetch/beskjed/all") {
        try {
            val beskjedEvents = beskjedEventService.getAllCachedEventsForUser(innloggetBruker)
            call.respond(HttpStatusCode.OK, beskjedEvents)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/fetch/beskjed/grouped") {
        try {
            val beskjedEvents =
                    beskjedEventService.getAllGroupedEventsFromCacheForUser(innloggetBruker,
                            call.request.queryParameters["grupperingsid"],
                            call.request.queryParameters["produsent"])
            call.respond(HttpStatusCode.OK, beskjedEvents)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }
}

fun Route.beskjedSystemClientApi(beskjedEventService: BeskjedEventService) {

    val log = LoggerFactory.getLogger(BeskjedEventService::class.java)

    // TODO: Remove
    get("/fetch/grouped/systemuser/beskjed") {
        try {
            val beskjedEvents =
                beskjedEventService.getAllGroupedEventsBySystemuserFromCache()
            call.respond(HttpStatusCode.OK, beskjedEvents)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/fetch/grouped/producer/beskjed") {
        try {
            val beskjedEvents =
                beskjedEventService.getAllGroupedEventsByProducerFromCache()
            call.respond(HttpStatusCode.OK, beskjedEvents)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/modia/fetch/beskjed/aktive") {
        doIfValidRequest { userToFetchEventsFor ->
            try {
                val aktiveBeskjedEvents = beskjedEventService.getActiveCachedEventsForUser(userToFetchEventsFor)
                call.respond(HttpStatusCode.OK, aktiveBeskjedEvents)

            } catch (exception: Exception) {
                respondWithError(call, log, exception)
            }
        }
    }

    get("/modia/fetch/beskjed/inaktive") {
        doIfValidRequest { userToFetchEventsFor ->
            try {
                val inaktiveBeskjedEvents = beskjedEventService.getInactiveCachedEventsForUser(userToFetchEventsFor)
                call.respond(HttpStatusCode.OK, inaktiveBeskjedEvents)

            } catch (exception: Exception) {
                respondWithError(call, log, exception)
            }
        }
    }

    get("/modia/fetch/beskjed/all") {
        doIfValidRequest { userToFetchEventsFor ->
            try {
                val beskjedEvents = beskjedEventService.getAllCachedEventsForUser(userToFetchEventsFor)
                call.respond(HttpStatusCode.OK, beskjedEvents)

            } catch (exception: Exception) {
                respondWithError(call, log, exception)
            }
        }
    }
}
