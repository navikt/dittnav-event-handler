package no.nav.personbruker.dittnav.eventhandler.done

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.request.receive
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.post
import io.ktor.util.pipeline.PipelineContext
import no.nav.personbruker.dittnav.eventhandler.config.userIdent

fun Route.doneApi(doneEventService: DoneEventService) {

    post("/handler/produce/done") {
        respondForParameterType<Done> { doneDto ->
            val eventId = doneDto.eventId
            val ident = userIdent
            doneEventService.markEventAsDone(ident, eventId)
            "Done-event er produsert for identen: xxx sitt event med eventID: ${doneDto.eventId}."
        }
    }
}

suspend inline fun <reified T : Any> PipelineContext<Unit, ApplicationCall>.respondForParameterType(handler: (T) -> String) {
    val postParametersDto: T = call.receive()
    val message = handler.invoke(postParametersDto)
    call.respondText(text = message, contentType = ContentType.Text.Plain)
}

