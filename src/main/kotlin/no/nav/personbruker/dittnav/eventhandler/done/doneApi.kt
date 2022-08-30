package no.nav.personbruker.dittnav.eventhandler.done

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.kafka.DuplicateEventException
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.kafka.EventMarkedInactiveException
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.kafka.NoEventsException
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.respondWithError
import no.nav.personbruker.dittnav.eventhandler.common.serializer.HttpStatusCodeSerializer
import no.nav.personbruker.dittnav.eventhandler.config.innloggetBruker
import no.nav.personbruker.dittnav.eventhandler.statistics.EventCountForProducer
import org.slf4j.LoggerFactory

fun Route.doneApi(doneEventService: DoneEventService) {

    val log = LoggerFactory.getLogger(DoneEventService::class.java)

    post("/produce/done") {
        respondForParameterType<DoneDTO> { doneDto ->
            try {
                doneEventService.markEventAsDone(innloggetBruker, doneDto)
                val msg = "Done-event er produsert. EventID: ${doneDto.eventId}."
                DoneResponse(msg, HttpStatusCode.OK)
            } catch (e: EventMarkedInactiveException) {
                val msg =
                    "Det ble ikke produsert et done-event fordi eventet allerede er markert inaktivt. EventId: ${doneDto.eventId}."
                log.info(msg, e)
                DoneResponse(msg, HttpStatusCode.OK)
            } catch (e: NoEventsException) {
                val msg =
                    "Det ble ikke produsert et done-event fordi vi fant ikke eventet i cachen. EventId: ${doneDto.eventId}."
                log.warn(msg, e)
                DoneResponse(msg, HttpStatusCode.NotFound)
            } catch (e: DuplicateEventException) {
                val msg =
                    "Det ble ikke produsert done-event fordi det finnes duplikat av event. EventId: ${doneDto.eventId}."
                log.error(msg, e)
                DoneResponse(msg, HttpStatusCode.InternalServerError)
            } catch (e: Exception) {
                val msg = "Done-event ble ikke produsert. EventID: ${doneDto.eventId}."
                log.error(msg, e)
                DoneResponse(msg, HttpStatusCode.InternalServerError)
            }
        }
    }
}

fun Route.doneSystemClientApi(doneEventService: DoneEventService) {

    val log = LoggerFactory.getLogger(DoneEventService::class.java)

    get("/fetch/grouped/producer/done") {
        try {
            val doneEvents = doneEventService.getAllGroupedEventsByProducerFromCache()
            val inactiveBrukernotifikasjoner = doneEventService.getNumberOfInactiveBrukernotifikasjonerByProducer()

            val result = doneEvents.mergeAndSumWith(inactiveBrukernotifikasjoner)

            call.respond(HttpStatusCode.OK, result)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }
}

private fun <T> Map<T, Int>.mergeAndSumWith(other: Map<T, Int>): Map<T, Int> {
    val result = toMutableMap()

    other.entries.forEach { (keyOther, valueOther) ->
        result.merge(keyOther, valueOther, Int::plus)
    }
    return result
}

private fun List<EventCountForProducer>.mergeAndSumWith(other: List<EventCountForProducer>): List<EventCountForProducer> {
    val thisAsMap = this.transformToMap()
    val otherAsMap = other.transformToMap()

    val mergedMap = thisAsMap.mergeAndSumWith(otherAsMap)

    return mergedMap.transformToList()
}

private fun Map<Pair<String, String>, Int>.transformToList(): List<EventCountForProducer> {
    return entries.map { (namespaceAppName, count) ->

        val (namespace, appName) = namespaceAppName
        EventCountForProducer(namespace, appName, count)
    }
}

private fun List<EventCountForProducer>.transformToMap(): Map<Pair<String, String>, Int> {
    return map {
        (it.namespace to it.appName) to it.count
    }.toMap()
}

suspend inline fun <reified T : Any> PipelineContext<Unit, ApplicationCall>.respondForParameterType(handler: (T) -> DoneResponse) {
    val postParametersDto: T = call.receive()
    val message = handler.invoke(postParametersDto)
    call.respond(message.httpStatus, message)
}

@Serializable(with = HttpStatusCodeSerializer::class)
data class DoneResponse(
    val message: String,
    val httpStatus: HttpStatusCode
)

