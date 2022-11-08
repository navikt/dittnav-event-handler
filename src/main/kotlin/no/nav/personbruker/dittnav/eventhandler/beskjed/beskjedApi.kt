package no.nav.personbruker.dittnav.eventhandler.beskjed


import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import mu.KotlinLogging
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.respondWithError
import no.nav.personbruker.dittnav.eventhandler.common.modia.doIfValidRequest
import no.nav.personbruker.dittnav.eventhandler.config.innloggetBruker
import java.time.ZonedDateTime

fun Route.beskjedApi(beskjedEventService: BeskjedEventService) {

    val log = KotlinLogging.logger {}

    get("/fetch/beskjed/aktive") {
        try {
            ZonedDateTime.now().offset
            val aktiveBeskjedEventsDTO = beskjedEventService.getActiveEventsForFodselsnummer(innloggetBruker.ident)
            call.respond(HttpStatusCode.OK, aktiveBeskjedEventsDTO)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/fetch/beskjed/inaktive") {
        try {
            val inaktiveBeskjedEvents = beskjedEventService.getInactiveEventsForFodselsnummer(innloggetBruker.ident)
            call.respond(HttpStatusCode.OK, inaktiveBeskjedEvents)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/fetch/beskjed/all") {
        try {
            val beskjedEvents = beskjedEventService.getAllEventsForFodselsnummer(innloggetBruker.ident)
            call.respond(HttpStatusCode.OK, beskjedEvents)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/fetch/beskjed/grouped") {
        try {
            val beskjedEvents =
                beskjedEventService.getAllGroupedEventsFromCacheForUser(
                    innloggetBruker,
                    call.request.queryParameters["grupperingsid"],
                    call.request.queryParameters["produsent"]
                )
            call.respond(HttpStatusCode.OK, beskjedEvents)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }
}

fun Route.beskjedSystemClientApi(beskjedEventService: BeskjedEventService) {

    val log = KotlinLogging.logger {}

    get("/fetch/grouped/producer/beskjed") {
        try {
            val beskjedEvents =
                beskjedEventService.getAllGroupedEventsByProducerFromCache()
            call.respond(HttpStatusCode.OK, beskjedEvents)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/fetch/modia/beskjed/aktive") {
        doIfValidRequest { userToFetchEventsFor ->
            try {
                val aktiveBeskjedEvents = beskjedEventService.getActiveEventsForFodselsnummer(userToFetchEventsFor.fodselsnummer)
                call.respond(HttpStatusCode.OK, aktiveBeskjedEvents)
            } catch (exception: Exception) {
                respondWithError(call, log, exception)
            }
        }
    }

    get("/fetch/modia/beskjed/inaktive") {
        doIfValidRequest { userToFetchEventsFor ->
            try {
                val inaktiveBeskjedEvents = beskjedEventService.getInactiveEventsForFodselsnummer(userToFetchEventsFor.fodselsnummer)
                call.respond(HttpStatusCode.OK, inaktiveBeskjedEvents)
            } catch (exception: Exception) {
                respondWithError(call, log, exception)
            }
        }
    }

    get("/fetch/modia/beskjed/all") {
        doIfValidRequest { userToFetchEventsFor ->
            try {
                val beskjedEvents = beskjedEventService.getAllEventsForFodselsnummer(userToFetchEventsFor.fodselsnummer)
                call.respond(HttpStatusCode.OK, beskjedEvents)
            } catch (exception: Exception) {
                respondWithError(call, log, exception)
            }
        }
    }
}
