package no.nav.personbruker.dittnav.eventhandler.varsel

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.respondWithError
import no.nav.personbruker.dittnav.eventhandler.common.modia.doIfValidRequest
import no.nav.personbruker.dittnav.eventhandler.config.innloggetBruker

fun Route.oboVarselApi(varselRepository: VarselRepository) {

    get("/fetch/varsel/on-behalf-of/inaktive") {
        doIfValidRequest { user ->
            val inactiveVarsler = varselRepository.getInactiveVarsel(user.fodselsnummer)
                .map { varsel -> varsel.toVarselDTO(user.authlevel) }
            call.respond(HttpStatusCode.OK, inactiveVarsler)
        }
    }

    get("/fetch/varsel/on-behalf-of/aktive") {
        doIfValidRequest { user ->
            val activeVarsler = varselRepository.getActiveVarsel(user.fodselsnummer)
                .map { varsel -> varsel.toVarselDTO(user.authlevel) }
            call.respond(HttpStatusCode.OK, activeVarsler)
        }
    }
}

fun Route.varselApi(eventRepository: VarselRepository) {

    get("/fetch/event/inaktive") {
            val inactiveVarsler = eventRepository.getInactiveVarsel(innloggetBruker.ident)
                .map { event -> event.toVarselDTO(innloggetBruker.loginLevel) }
            call.respond(HttpStatusCode.OK, inactiveVarsler)
    }

    get("/fetch/event/aktive") {
            val activeVarler = eventRepository.getActiveVarsel(innloggetBruker.ident)
                .map { event -> event.toVarselDTO(innloggetBruker.loginLevel) }
            call.respond(HttpStatusCode.OK, activeVarler)
    }
}