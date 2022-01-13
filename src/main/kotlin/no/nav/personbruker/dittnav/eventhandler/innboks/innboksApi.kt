package no.nav.personbruker.dittnav.eventhandler.innboks

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.respondWithError
import no.nav.personbruker.dittnav.eventhandler.config.innloggetBruker
import org.slf4j.LoggerFactory

fun Route.innboksApi(innboksEventService: InnboksEventService) {

    val log = LoggerFactory.getLogger(InnboksEventService::class.java)

    get("/fetch/innboks/aktive") {
        try {
            val aktiveInnboksEvents = innboksEventService.getActiveCachedEventsForUser(innloggetBruker)
            call.respond(HttpStatusCode.OK, aktiveInnboksEvents)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/fetch/innboks/inaktive") {
        try {
            val inaktiveInnboksEvents = innboksEventService.getInctiveCachedEventsForUser(innloggetBruker)
            call.respond(HttpStatusCode.OK, inaktiveInnboksEvents)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/fetch/innboks/all") {
        try {
            val innboksEvents = innboksEventService.getAllCachedEventsForUser(innloggetBruker)
            call.respond(HttpStatusCode.OK, innboksEvents)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/fetch/innboks/grouped") {
        try {
            val innboksEvents =
                    innboksEventService.getAllGroupedEventsFromCacheForUser(innloggetBruker,
                            call.request.queryParameters["grupperingsid"],
                            call.request.queryParameters["produsent"])
            call.respond(HttpStatusCode.OK, innboksEvents)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }
}

fun Route.innboksSystemClientApi(innboksEventService: InnboksEventService) {

    val log = LoggerFactory.getLogger(InnboksEventService::class.java)

    get("/fetch/grouped/systemuser/innboks") {
        try {
            val innboksEvents =
                innboksEventService.getAllGroupedEventsBySystemuserFromCache()
            call.respond(HttpStatusCode.OK, innboksEvents)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/fetch/grouped/producer/innboks") {
        try {
            val beskjedEvents =
                innboksEventService.getAllGroupedEventsByProducerFromCache()
            call.respond(HttpStatusCode.OK, beskjedEvents)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }
}
