package no.nav.personbruker.dittnav.eventhandler.done

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.receiveOrNull
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.json.JsonObject
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.respondWithError
import no.nav.personbruker.dittnav.eventhandler.config.innloggetBruker
import no.nav.personbruker.dittnav.eventhandler.statistics.EventCountForProducer
import org.slf4j.LoggerFactory

fun Route.doneApi(doneEventService: DoneEventService) {

    val log = LoggerFactory.getLogger(DoneEventService::class.java)

    post("/produce/done") {
        call.receiveEventIdOrNull()?.let { eventId ->
            log.info("produce/done for beskjed med eventId $eventId")
            doneEventService.markEventAsInaktiv(innloggetBruker, eventId)
            call.respond(HttpStatusCode.OK)
        } ?: call.respond(HttpStatusCode.BadRequest, "eventid parameter mangler")
    }
}

private suspend fun ApplicationCall.receiveEventIdOrNull(): String? = receiveOrNull<JsonObject>()?.get("eventId")?.toString()

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
