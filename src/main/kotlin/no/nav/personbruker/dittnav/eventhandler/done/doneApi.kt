package no.nav.personbruker.dittnav.eventhandler.done

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.request.receive
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.post
import io.ktor.util.pipeline.PipelineContext
import no.nav.personbruker.dittnav.eventhandler.common.innloggetBruker

fun Route.doneApi(doneEventService: DoneEventService) {

    post("/handler/produce/done") {
        respondForParameterType<Done> { doneDto ->
            val beskjed = doneEventService.getBeskjedFromCacheForUser(innloggetBruker.getIdent(), doneDto.uid.toInt(), doneDto.eventId)

            if (beskjed.isNotEmpty()) {
                doneEventService.markEventAsDone(innloggetBruker.getIdent(), doneDto.eventId, beskjed.get(0).produsent, beskjed.get(0).grupperingsId)
                "Done-event er produsert for identen: ${innloggetBruker.getIdent()} sitt event med eventID: ${doneDto.eventId}. Uid: ${doneDto.uid}"
            } else {
                "Done-event ble -ikke- produsert fordi vi fant ikke eventet. Identen: ${innloggetBruker.getIdent()} . EventID: ${doneDto.eventId}. Uid: ${doneDto.uid}"
            }
        }
    }
}

suspend inline fun <reified T : Any> PipelineContext<Unit, ApplicationCall>.respondForParameterType(handler: (T) -> String) {
    val postParametersDto: T = call.receive()
    val message = handler.invoke(postParametersDto)
    call.respondText(text = message, contentType = ContentType.Text.Plain)
}

