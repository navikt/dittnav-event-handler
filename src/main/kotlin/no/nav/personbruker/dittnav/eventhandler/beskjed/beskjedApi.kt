package no.nav.personbruker.dittnav.eventhandler.beskjed


import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.personbruker.dittnav.eventhandler.common.modia.doIfValidRequest
import no.nav.personbruker.dittnav.eventhandler.config.innloggetBruker
import java.time.ZonedDateTime

fun Route.beskjedApi(beskjedEventService: BeskjedEventService) {

    get("/fetch/beskjed/aktive") {
        ZonedDateTime.now().offset
        val aktiveBeskjedEventsDTO = beskjedEventService.getActiveEventsForFodselsnummer(innloggetBruker.ident)
        call.respond(HttpStatusCode.OK, aktiveBeskjedEventsDTO)
    }

    get("/fetch/beskjed/inaktive") {
        val inaktiveBeskjedEvents = beskjedEventService.getInactiveEventsForFodselsnummer(innloggetBruker.ident)
        call.respond(HttpStatusCode.OK, inaktiveBeskjedEvents)
    }

    get("/fetch/beskjed/all") {

        val beskjedEvents = beskjedEventService.getAllEventsForFodselsnummer(innloggetBruker.ident)
        call.respond(HttpStatusCode.OK, beskjedEvents)

    }
}

fun Route.beskjedSystemClientApi(beskjedEventService: BeskjedEventService) {

    get("/fetch/grouped/producer/beskjed") {
        val beskjedEvents =
            beskjedEventService.getAllGroupedEventsByProducerFromCache()
        call.respond(HttpStatusCode.OK, beskjedEvents)
    }

    get("/fetch/modia/beskjed/aktive") {
        doIfValidRequest { userToFetchEventsFor ->
            val aktiveBeskjedEvents =
                beskjedEventService.getActiveEventsForFodselsnummer(userToFetchEventsFor.fodselsnummer)
            call.respond(HttpStatusCode.OK, aktiveBeskjedEvents)
        }
    }

    get("/fetch/modia/beskjed/inaktive") {
        doIfValidRequest { userToFetchEventsFor ->
            val inaktiveBeskjedEvents =
                beskjedEventService.getInactiveEventsForFodselsnummer(userToFetchEventsFor.fodselsnummer)
            call.respond(HttpStatusCode.OK, inaktiveBeskjedEvents)
        }
    }

    get("/fetch/modia/beskjed/all") {
        doIfValidRequest { userToFetchEventsFor ->

            val beskjedEvents = beskjedEventService.getAllEventsForFodselsnummer(userToFetchEventsFor.fodselsnummer)
            call.respond(HttpStatusCode.OK, beskjedEvents)
        }
    }
}
