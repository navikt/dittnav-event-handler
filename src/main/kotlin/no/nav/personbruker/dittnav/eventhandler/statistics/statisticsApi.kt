package no.nav.personbruker.dittnav.eventhandler.statistics

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedEventService
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.respondWithError
import org.slf4j.LoggerFactory

fun Route.statisticsSystemClientApi(statisticsService: EventStatisticsService) {

    val log = LoggerFactory.getLogger(BeskjedEventService::class.java)

    get("/stats/all") {
        try {
            val intMeasurements = mutableListOf<IntegerMeasurement>()
            val decimalMeasurements = mutableListOf<DecimalMeasurement>()
            val countMeasurements = mutableListOf<CountMeasurement>()
            intMeasurements += statisticsService.getEventsStatisticsPerUser(EventType.BESKJED)
            intMeasurements += statisticsService.getEventsStatisticsPerUser(EventType.OPPGAVE)
            intMeasurements += statisticsService.getEventsStatisticsPerUser(EventType.INNBOKS)

            intMeasurements += statisticsService.getActiveEventsStatisticsPerUser(EventType.BESKJED)
            intMeasurements += statisticsService.getActiveEventsStatisticsPerUser(EventType.OPPGAVE)
            intMeasurements += statisticsService.getActiveEventsStatisticsPerUser(EventType.INNBOKS)

            decimalMeasurements += statisticsService.getActiveRateEventsStatisticsPerUser(EventType.BESKJED)
            decimalMeasurements += statisticsService.getActiveRateEventsStatisticsPerUser(EventType.OPPGAVE)
            decimalMeasurements += statisticsService.getActiveRateEventsStatisticsPerUser(EventType.INNBOKS)

            intMeasurements += statisticsService.getEventsStatisticsPerGroupId(EventType.BESKJED)
            intMeasurements += statisticsService.getEventsStatisticsPerGroupId(EventType.OPPGAVE)
            intMeasurements += statisticsService.getEventsStatisticsPerGroupId(EventType.INNBOKS)

            intMeasurements += statisticsService.getGroupIdsPerUser(EventType.BESKJED)
            intMeasurements += statisticsService.getGroupIdsPerUser(EventType.OPPGAVE)
            intMeasurements += statisticsService.getGroupIdsPerUser(EventType.INNBOKS)

            intMeasurements += statisticsService.getTextLength(EventType.BESKJED)
            intMeasurements += statisticsService.getTextLength(EventType.OPPGAVE)
            intMeasurements += statisticsService.getTextLength(EventType.INNBOKS)

            countMeasurements += statisticsService.getCountUsersWithEvents(EventType.BESKJED)
            countMeasurements += statisticsService.getCountUsersWithEvents(EventType.OPPGAVE)
            countMeasurements += statisticsService.getCountUsersWithEvents(EventType.INNBOKS)

            countMeasurements += statisticsService.getEventCount(EventType.BESKJED)
            countMeasurements += statisticsService.getEventCount(EventType.OPPGAVE)
            countMeasurements += statisticsService.getEventCount(EventType.INNBOKS)

            countMeasurements += statisticsService.getActiveEventCount(EventType.BESKJED)
            countMeasurements += statisticsService.getActiveEventCount(EventType.OPPGAVE)
            countMeasurements += statisticsService.getActiveEventCount(EventType.INNBOKS)

            val measurements = mapOf(
                "intMeasurements" to intMeasurements,
                "decimalMeasurements" to decimalMeasurements,
                "countMeasurements" to countMeasurements
            )

            call.respond(HttpStatusCode.OK, measurements)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/stats/grouped/bruker/{type}") {
        try {
            val type = EventType.fromOriginalType(call.parameters["type"]!!)
            val measurement = statisticsService.getEventsStatisticsPerUser(type)

            call.respond(HttpStatusCode.OK, measurement)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/stats/total/grouped/bruker") {
        try {
            val measurement = statisticsService.getTotalEventsStatisticsPerUser()

            call.respond(HttpStatusCode.OK, measurement)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/stats/grouped/bruker/{type}/active") {
        try {
            val type = EventType.fromOriginalType(call.parameters["type"]!!)
            val measurement = statisticsService.getActiveEventsStatisticsPerUser(type)

            call.respond(HttpStatusCode.OK, measurement)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/stats/total/grouped/bruker/active") {
        try {
            val measurement = statisticsService.getTotalActiveEventsStatisticsPerUser()

            call.respond(HttpStatusCode.OK, measurement)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/stats/grouped/bruker/{type}/active-rate") {
        try {
            val type = EventType.fromOriginalType(call.parameters["type"]!!)
            val measurement = statisticsService.getActiveRateEventsStatisticsPerUser(type)

            call.respond(HttpStatusCode.OK, measurement)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/stats/total/grouped/bruker/active-rate") {
        try {
            val measurement = statisticsService.getTotalActiveRateEventsStatisticsPerUser()

            call.respond(HttpStatusCode.OK, measurement)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/stats/grouped/gruppering/{type}") {
        try {
            val type = EventType.fromOriginalType(call.parameters["type"]!!)
            val measurement = statisticsService.getEventsStatisticsPerGroupId(type)

            call.respond(HttpStatusCode.OK, measurement)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/stats/total/grouped/gruppering") {
        try {
            val measurement = statisticsService.getTotalEventsStatisticsPerGroupId()

            call.respond(HttpStatusCode.OK, measurement)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/stats/grouped/bruker/{type}/grupperings") {
        try {
            val type = EventType.fromOriginalType(call.parameters["type"]!!)
            val measurement = statisticsService.getGroupIdsPerUser(type)

            call.respond(HttpStatusCode.OK, measurement)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/stats/total/grouped/bruker/grupperings") {
        try {
            val measurement = statisticsService.getTotalGroupIdsPerUser()

            call.respond(HttpStatusCode.OK, measurement)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/stats/{type}/text-length") {
        try {
            val type = EventType.fromOriginalType(call.parameters["type"]!!)
            val measurement = statisticsService.getTextLength(type)

            call.respond(HttpStatusCode.OK, measurement)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/stats/total/text-length") {
        try {
            val measurement = statisticsService.getTotalTextLength()

            call.respond(HttpStatusCode.OK, measurement)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/stats/{type}/bruker-count") {
        try {
            val type = EventType.fromOriginalType(call.parameters["type"]!!)
            val measurement = statisticsService.getCountUsersWithEvents(type)

            call.respond(HttpStatusCode.OK, measurement)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/stats/total/bruker-count") {
        try {
            val measurement = statisticsService.getTotalCountUsersWithEvents()

            call.respond(HttpStatusCode.OK, measurement)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/stats/count/{type}") {
        try {
            val type = EventType.fromOriginalType(call.parameters["type"]!!)
            val measurement = statisticsService.getEventCount(type)

            call.respond(HttpStatusCode.OK, measurement)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/stats/total/count") {
        try {
            val measurement = statisticsService.getTotalEventCount()

            call.respond(HttpStatusCode.OK, measurement)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/stats/count/{type}/active") {
        try {
            val type = EventType.fromOriginalType(call.parameters["type"]!!)
            val measurement = statisticsService.getActiveEventCount(type)

            call.respond(HttpStatusCode.OK, measurement)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/stats/total/count/active") {
        try {
            val measurement = statisticsService.getTotalActiveEventCount()

            call.respond(HttpStatusCode.OK, measurement)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }
}
