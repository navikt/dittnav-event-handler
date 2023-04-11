package no.nav.personbruker.dittnav.eventhandler.innboks


import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging
import no.nav.personbruker.dittnav.eventhandler.common.modia.doIfValidRequest
import no.nav.personbruker.dittnav.eventhandler.config.innloggetBruker

fun Route.innboksApi(innboksEventService: InnboksEventService) {

    get("/fetch/innboks/aktive") {
        val aktiveInnboksEvents = innboksEventService.getActiveEventsForFodselsnummer(innloggetBruker.ident)
        call.respond(HttpStatusCode.OK, aktiveInnboksEvents)
    }

    get("/fetch/innboks/inaktive") {
        val inaktiveInnboksEvents = innboksEventService.getInactiveEventsForFodselsnummer(innloggetBruker.ident)
        call.respond(HttpStatusCode.OK, inaktiveInnboksEvents)
    }

    get("/fetch/innboks/all") {
        val innboksEvents = innboksEventService.getAllEventsForFodselsnummer(innloggetBruker.ident)
        call.respond(HttpStatusCode.OK, innboksEvents)
    }
}

fun Route.innboksSystemClientApi(innboksEventService: InnboksEventService) {

    get("/fetch/grouped/producer/innboks") {
        val beskjedEvents = innboksEventService.getAllGroupedEventsByProducerFromCache()
        call.respond(HttpStatusCode.OK, beskjedEvents)
    }

    get("/fetch/modia/innboks/aktive") {
        doIfValidRequest { userToFetchEventsFor ->
            val aktiveInnboksEvents =
                innboksEventService.getActiveEventsForFodselsnummer(userToFetchEventsFor.fodselsnummer)
            call.respond(HttpStatusCode.OK, aktiveInnboksEvents)
        }
    }

    get("/fetch/modia/innboks/inaktive") {
        doIfValidRequest { userToFetchEventsFor ->
            val inaktiveInnboksEvents =
                innboksEventService.getInactiveEventsForFodselsnummer(userToFetchEventsFor.fodselsnummer)
            call.respond(HttpStatusCode.OK, inaktiveInnboksEvents)
        }
    }

    get("/fetch/modia/innboks/all") {
        doIfValidRequest { userToFetchEventsFor ->
            val innboksEvents = innboksEventService.getAllEventsForFodselsnummer(userToFetchEventsFor.fodselsnummer)
            call.respond(HttpStatusCode.OK, innboksEvents)
        }
    }
}
