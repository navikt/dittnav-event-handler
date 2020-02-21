package no.nav.personbruker.dittnav.eventhandler.done

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.request.receive
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.post
import io.ktor.util.pipeline.PipelineContext
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.DuplicateEventException
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.NoEventsException
import no.nav.personbruker.dittnav.eventhandler.common.innloggetBruker
import org.slf4j.LoggerFactory
import java.lang.Exception

fun Route.doneApi(doneEventService: DoneEventService) {

    val log = LoggerFactory.getLogger(DoneEventService::class.java)

    post("/handler/produce/done") {
        respondForParameterType<Done> { doneDto ->
            try {
                doneEventService.markEventAsDone(innloggetBruker, doneDto)
                log.info("Done-event er produsert. EventID: ${doneDto.eventId}. Uid: ${doneDto.uid} ")
                "Done-event er produsert. Identen: ${innloggetBruker.getIdent()}, EventID: ${doneDto.eventId}. Uid: ${doneDto.uid}"
            } catch (e: NoEventsException) {
                val msg = "Det ble ikke produsert et done-event fordi vi fant ikke eventet i cachen. EventId: ${doneDto.eventId}, Uid: ${doneDto.uid}"
                log.error(msg, e)
                "Done-event ble -ikke- produsert fordi vi fant ikke eventet. Identen: ${innloggetBruker.getIdent()} . EventID: ${doneDto.eventId}. Uid: ${doneDto.uid}"
                msg
            } catch (e: DuplicateEventException) {
                val msg ="Det ble ikke produsert done-event fordi det finnes duplikat av events. EventId: ${doneDto.eventId}, Uid: ${doneDto.uid}"
                log.error(msg, e)
                msg
            } catch (e: Exception) {
                val msg = "Done-event ble ikke produsert. EventID: ${doneDto.eventId}. Uid: ${doneDto.uid}"
                log.error(msg, e)
                msg
            }
        }
    }
}

suspend inline fun <reified T : Any> PipelineContext<Unit, ApplicationCall>.respondForParameterType(handler: (T) -> String) {
    val postParametersDto: T = call.receive()
    val message = handler.invoke(postParametersDto)
    call.respondText(text = message, contentType = ContentType.Text.Plain)
}
