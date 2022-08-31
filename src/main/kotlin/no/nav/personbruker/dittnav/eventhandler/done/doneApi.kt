package no.nav.personbruker.dittnav.eventhandler.done

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.Serializable
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
        val eventId = call.parameters["eventId"]
            if (eventId == null) {
                call.respond( HttpStatusCode.BadRequest,"eventid parameter mangler",)
            } else {
                try {
                    doneEventService.markEventAsDone(innloggetBruker, eventId)
                    val msg = "Done-event er produsert. EventID: ${eventId}."
                    call.respond(HttpStatusCode.OK, msg)
                } catch (e: EventMarkedInactiveException) {
                    val msg =
                        "Det ble ikke produsert et done-event fordi eventet allerede er markert inaktivt. EventId: ${eventId}."
                    log.info(msg, e)
                    call.respond(HttpStatusCode.OK, msg)
                } catch (e: NoEventsException) {
                    val msg =
                        "Det ble ikke produsert et done-event fordi vi fant ikke eventet i cachen. EventId: ${eventId}."
                    log.warn(msg, e)
                    call.respond(HttpStatusCode.NotFound,msg)
                } catch (e: DuplicateEventException) {
                    val msg =
                        "Det ble ikke produsert done-event fordi det finnes duplikat av event. EventId: ${eventId}."
                    log.error(msg, e)
                    call.respond(HttpStatusCode.InternalServerError, msg)
                } catch (e: Exception) {
                    val msg = "Done-event ble ikke produsert. EventID: ${eventId}."
                    log.error(msg, e)
                    call.respond(HttpStatusCode.InternalServerError, msg)
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
