package no.nav.personbruker.dittnav.eventhandler.oppgave

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import no.nav.personbruker.dittnav.eventhandler.config.extractIdentFromToken

fun Route.oppgaveApi(oppgaveEventService: OppgaveEventService) {

    get("/fetch/oppgave") {
        val ident = extractIdentFromToken()
        val events = oppgaveEventService.getEventsFromCacheForUser(ident)
        call.respond(HttpStatusCode.OK, events)
    }

    get("/fetch/oppgave/all") {
        val ident = extractIdentFromToken()
        val events = oppgaveEventService.getAllEventsFromCacheForUser(ident)
        call.respond(HttpStatusCode.OK, events)
    }

    post("/produce/oppgave") {
        val postParametersDto = call.receive<ProduceOppgaveDto>()
        val ident = extractIdentFromToken()
        OppgaveProducer.produceOppgaveEventForIdent(ident, postParametersDto)
        val msg = "Et oppgave-event for identen: $ident har blitt lagt på kafka."
        call.respondText(text = msg, contentType = ContentType.Text.Plain)
    }

}
