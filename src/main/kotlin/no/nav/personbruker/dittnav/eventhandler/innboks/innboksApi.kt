package no.nav.personbruker.dittnav.eventhandler.innboks


import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import mu.KotlinLogging
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.respondWithError
import no.nav.personbruker.dittnav.eventhandler.common.modia.doIfValidRequest
import no.nav.personbruker.dittnav.eventhandler.config.innloggetBruker

fun Route.innboksApi(innboksEventService: InnboksEventService) {

    val log = KotlinLogging.logger {}

    get("/fetch/innboks/aktive") {
        try {
            val aktiveInnboksEvents = innboksEventService.getActiveEventsForFodselsnummer(innloggetBruker.ident)
            call.respond(HttpStatusCode.OK, aktiveInnboksEvents)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/fetch/innboks/inaktive") {
        try {
            val inaktiveInnboksEvents = innboksEventService.getInactiveEventsForFodselsnummer(innloggetBruker.ident)
            call.respond(HttpStatusCode.OK, inaktiveInnboksEvents)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/fetch/innboks/all") {
        try {
            val innboksEvents = innboksEventService.getAllEventsForFodselsnummer(innloggetBruker.ident)
            call.respond(HttpStatusCode.OK, innboksEvents)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/fetch/innboks/grouped") {
        try {
            val innboksEvents =
                innboksEventService.getAllGroupedEventsFromCacheForUser(
                    innloggetBruker,
                    call.request.queryParameters["grupperingsid"],
                    call.request.queryParameters["produsent"]
                )
            call.respond(HttpStatusCode.OK, innboksEvents)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }
}

fun Route.innboksSystemClientApi(innboksEventService: InnboksEventService) {

    val log = KotlinLogging.logger {}

    get("/fetch/grouped/producer/innboks") {
        try {
            val beskjedEvents =
                innboksEventService.getAllGroupedEventsByProducerFromCache()
            call.respond(HttpStatusCode.OK, beskjedEvents)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/fetch/modia/innboks/aktive") {
        doIfValidRequest { userToFetchEventsFor ->
            try {
                val aktiveInnboksEvents = innboksEventService.getActiveEventsForFodselsnummer(userToFetchEventsFor.fodselsnummer)
                call.respond(HttpStatusCode.OK, aktiveInnboksEvents)
            } catch (exception: Exception) {
                respondWithError(call, log, exception)
            }
        }
    }

    get("/fetch/modia/innboks/inaktive") {
        doIfValidRequest { userToFetchEventsFor ->
            try {
                val inaktiveInnboksEvents = innboksEventService.getInactiveEventsForFodselsnummer(userToFetchEventsFor.fodselsnummer)
                call.respond(HttpStatusCode.OK, inaktiveInnboksEvents)
            } catch (exception: Exception) {
                respondWithError(call, log, exception)
            }
        }
    }

    get("/fetch/modia/innboks/all") {
        doIfValidRequest { userToFetchEventsFor ->
            try {
                val innboksEvents = innboksEventService.getAllEventsForFodselsnummer(userToFetchEventsFor.fodselsnummer)
                call.respond(HttpStatusCode.OK, innboksEvents)
            } catch (exception: Exception) {
                respondWithError(call, log, exception)
            }
        }
    }
}
