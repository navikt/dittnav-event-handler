package no.nav.personbruker.dittnav.eventhandler.done

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.post
import no.nav.personbruker.dittnav.eventhandler.config.extractIdentFromToken

fun Route.doneApi(doneEventService: DoneEventService) {

    post("/produce/done/all") {
        val ident = extractIdentFromToken()
        doneEventService.markAllBrukernotifikasjonerAsDone(ident)
        val msg = "Done-eventer er produsert for alle identen: $ident sine brukernotifikasjoner."
        call.respondText(text = msg, contentType = ContentType.Text.Plain)
    }

}
