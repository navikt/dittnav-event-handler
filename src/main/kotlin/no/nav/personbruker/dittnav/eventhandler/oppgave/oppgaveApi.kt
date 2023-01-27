package no.nav.personbruker.dittnav.eventhandler.oppgave


import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import mu.KotlinLogging
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.respondWithError
import no.nav.personbruker.dittnav.eventhandler.common.modia.doIfValidRequest
import no.nav.personbruker.dittnav.eventhandler.config.innloggetBruker

fun Route.oppgaveApi(oppgaveEventService: OppgaveEventService) {

    val log = KotlinLogging.logger {}

    get("/fetch/oppgave/aktive") {
        try {
            val aktiveOppgaveEvents = oppgaveEventService.getActiveEventsForFodselsnummer(innloggetBruker.ident)
            call.respond(HttpStatusCode.OK, aktiveOppgaveEvents)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/fetch/oppgave/inaktive") {
        try {
            val inaktiveOppgaveEvents = oppgaveEventService.getInactiveEventsForFodselsnummer(innloggetBruker.ident)
            call.respond(HttpStatusCode.OK, inaktiveOppgaveEvents)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/fetch/oppgave/all") {
        try {
            val oppgaveEvents = oppgaveEventService.getAllEventsForFodselsnummer(innloggetBruker.ident)
            call.respond(HttpStatusCode.OK, oppgaveEvents)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }
}

fun Route.oppgaveSystemClientApi(oppgaveEventService: OppgaveEventService) {

    val log = KotlinLogging.logger {}

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
                val aktiveOppgaveEvents = oppgaveEventService.getActiveEventsForFodselsnummer(userToFetchEventsFor.fodselsnummer)
                call.respond(HttpStatusCode.OK, aktiveOppgaveEvents)
            } catch (exception: Exception) {
                respondWithError(call, log, exception)
            }
        }
    }

    get("/fetch/modia/oppgave/inaktive") {
        doIfValidRequest { userToFetchEventsFor ->
            try {
                val inaktiveOppgaveEvents = oppgaveEventService.getInactiveEventsForFodselsnummer(userToFetchEventsFor.fodselsnummer)
                call.respond(HttpStatusCode.OK, inaktiveOppgaveEvents)
            } catch (exception: Exception) {
                respondWithError(call, log, exception)
            }
        }
    }

    get("/fetch/modia/oppgave/all") {
        doIfValidRequest { userToFetchEventsFor ->
            try {
                val oppgaveEvents = oppgaveEventService.getAllEventsForFodselsnummer(userToFetchEventsFor.fodselsnummer)
                call.respond(HttpStatusCode.OK, oppgaveEvents)
            } catch (exception: Exception) {
                respondWithError(call, log, exception)
            }
        }
    }
}
