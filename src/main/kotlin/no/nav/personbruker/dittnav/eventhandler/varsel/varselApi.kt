package no.nav.personbruker.dittnav.eventhandler.varsel

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.respondWithError
import no.nav.personbruker.dittnav.eventhandler.common.modia.doIfValidRequest

//brukes av varselbjella
fun Route.varselApi(varselRepository: VarselRepository) {

    val log = KotlinLogging.logger {}

    get("/fetch/varsel/on-behalf-of/inaktive") {
        doIfValidRequest { user ->
            try {
                val inactiveVarsler = varselRepository.getInactiveVarsel(user.fodselsnummer)
                    .map { varsel -> varsel.toVarselDTO() }
                call.respond(HttpStatusCode.OK, inactiveVarsler)
            } catch (exception: Exception) {
                respondWithError(call, log, exception)
            }
        }
    }

    get("/fetch/varsel/on-behalf-of/aktive") {
        doIfValidRequest { user ->
            try {
                val activeEventDTOs = varselRepository.getActiveVarsel(user.fodselsnummer)
                    .map { varsel -> varsel.toVarselDTO() }
                call.respond(HttpStatusCode.OK, activeEventDTOs)
            } catch (exception: Exception) {
                respondWithError(call, log, exception)
            }
        }
    }
}
