package no.nav.personbruker.dittnav.eventhandler.done


import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import kotlinx.serialization.Serializable
import mu.KotlinLogging
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.respondWithError
import no.nav.personbruker.dittnav.eventhandler.statistics.EventCountForProducer


fun Route.doneSystemClientApi(doneEventService: DoneEventService) {

    val log = KotlinLogging.logger {}

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

@Serializable
data class EventIdBody(val eventId: String?=null)
