package no.nav.personbruker.dittnav.eventhandler.oppgave


import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.personbruker.dittnav.eventhandler.common.modia.doIfValidRequest
import no.nav.personbruker.dittnav.eventhandler.config.innloggetBruker

fun Route.oppgaveApi(oppgaveEventService: OppgaveEventService) {

    get("/fetch/oppgave/aktive") {
        val aktiveOppgaveEvents = oppgaveEventService.getActiveEventsForFodselsnummer(innloggetBruker.ident)
        call.respond(HttpStatusCode.OK, aktiveOppgaveEvents)
    }

    get("/fetch/oppgave/inaktive") {
        val inaktiveOppgaveEvents = oppgaveEventService.getInactiveEventsForFodselsnummer(innloggetBruker.ident)
        call.respond(HttpStatusCode.OK, inaktiveOppgaveEvents)
    }

    get("/fetch/oppgave/all") {
        val oppgaveEvents = oppgaveEventService.getAllEventsForFodselsnummer(innloggetBruker.ident)
        call.respond(HttpStatusCode.OK, oppgaveEvents)
    }
}

fun Route.oppgaveSystemClientApi(oppgaveEventService: OppgaveEventService) {

    get("/fetch/grouped/producer/oppgave") {
        val beskjedEvents =
            oppgaveEventService.getAllGroupedEventsByProducerFromCache()
        call.respond(HttpStatusCode.OK, beskjedEvents)
    }

    get("/fetch/modia/oppgave/aktive") {
        doIfValidRequest { userToFetchEventsFor ->
            val aktiveOppgaveEvents =
                oppgaveEventService.getActiveEventsForFodselsnummer(userToFetchEventsFor.fodselsnummer)
            call.respond(HttpStatusCode.OK, aktiveOppgaveEvents)
        }
    }

    get("/fetch/modia/oppgave/inaktive") {
        doIfValidRequest { userToFetchEventsFor ->
            val inaktiveOppgaveEvents =
                oppgaveEventService.getInactiveEventsForFodselsnummer(userToFetchEventsFor.fodselsnummer)
            call.respond(HttpStatusCode.OK, inaktiveOppgaveEvents)
        }
    }

    get("/fetch/modia/oppgave/all") {
        doIfValidRequest { userToFetchEventsFor ->
            val oppgaveEvents = oppgaveEventService.getAllEventsForFodselsnummer(userToFetchEventsFor.fodselsnummer)
            call.respond(HttpStatusCode.OK, oppgaveEvents)
        }
    }
}
