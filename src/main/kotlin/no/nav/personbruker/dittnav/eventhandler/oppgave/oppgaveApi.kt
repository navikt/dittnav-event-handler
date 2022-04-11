package no.nav.personbruker.dittnav.eventhandler.oppgave

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.respondWithError
import no.nav.personbruker.dittnav.eventhandler.common.modia.doIfValidRequest
import no.nav.personbruker.dittnav.eventhandler.config.innloggetBruker
import org.slf4j.LoggerFactory

fun Route.oppgaveApi(oppgaveEventService: OppgaveEventService) {

    val log = LoggerFactory.getLogger(OppgaveEventService::class.java)

    get("/fetch/oppgave/aktive") {
        try {
            val aktiveOppgaveEvents = oppgaveEventService.getActiveCachedEventsForUser(innloggetBruker)
            call.respond(HttpStatusCode.OK, aktiveOppgaveEvents)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/fetch/oppgave/inaktive") {
        try {
            val inaktiveOppgaveEvents = oppgaveEventService.getInactiveCachedEventsForUser(innloggetBruker)
            call.respond(HttpStatusCode.OK, inaktiveOppgaveEvents)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/fetch/oppgave/all") {
        try {
            val oppgaveEvents = oppgaveEventService.getAllCachedEventsForUser(innloggetBruker)
            call.respond(HttpStatusCode.OK, oppgaveEvents)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/fetch/oppgave/grouped") {
        try {
            val oppgaveEvents =
                    oppgaveEventService.getAllGroupedEventsFromCacheForUser(innloggetBruker,
                            call.request.queryParameters["grupperingsid"],
                            call.request.queryParameters["produsent"])
            call.respond(HttpStatusCode.OK, oppgaveEvents)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }
}

fun Route.oppgaveSystemClientApi(oppgaveEventService: OppgaveEventService) {

    val log = LoggerFactory.getLogger(OppgaveEventService::class.java)

    get("/fetch/grouped/producer/oppgave") {
        try {
            val beskjedEvents =
                oppgaveEventService.getAllGroupedEventsByProducerFromCache()
            call.respond(HttpStatusCode.OK, beskjedEvents)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/fetch/modia/oppgave/aktive") {
        doIfValidRequest { userToFetchEventsFor ->
            try {
                val aktiveOppgaveEvents = oppgaveEventService.getActiveCachedEventsForUser(userToFetchEventsFor)
                call.respond(HttpStatusCode.OK, aktiveOppgaveEvents)

            } catch (exception: Exception) {
                respondWithError(call, log, exception)
            }
        }
    }

    get("/fetch/modia/oppgave/inaktive") {
        doIfValidRequest { userToFetchEventsFor ->
            try {
                val inaktiveOppgaveEvents = oppgaveEventService.getInactiveCachedEventsForUser(userToFetchEventsFor)
                call.respond(HttpStatusCode.OK, inaktiveOppgaveEvents)
            } catch (exception: Exception) {
                respondWithError(call, log, exception)
            }
        }
    }

    get("/fetch/modia/oppgave/all") {
        doIfValidRequest { userToFetchEventsFor ->
            try {
                val oppgaveEvents = oppgaveEventService.getAllCachedEventsForUser(userToFetchEventsFor)
                call.respond(HttpStatusCode.OK, oppgaveEvents)

            } catch (exception: Exception) {
                respondWithError(call, log, exception)
            }
        }
    }
}
