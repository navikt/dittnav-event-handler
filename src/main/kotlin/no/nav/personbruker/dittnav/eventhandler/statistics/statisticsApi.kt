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
            val measurements = mutableListOf<Any>()
            measurements += statisticsService.getEventsStatisticsPerUser(EventType.BESKJED)
            measurements += statisticsService.getEventsStatisticsPerUser(EventType.OPPGAVE)
            measurements += statisticsService.getEventsStatisticsPerUser(EventType.INNBOKS)

            measurements += statisticsService.getActiveEventsStatisticsPerUser(EventType.BESKJED)
            measurements += statisticsService.getActiveEventsStatisticsPerUser(EventType.OPPGAVE)
            measurements += statisticsService.getActiveEventsStatisticsPerUser(EventType.INNBOKS)

            measurements += statisticsService.getActiveRateEventsStatisticsPerUser(EventType.BESKJED)
            measurements += statisticsService.getActiveRateEventsStatisticsPerUser(EventType.OPPGAVE)
            measurements += statisticsService.getActiveRateEventsStatisticsPerUser(EventType.INNBOKS)

            measurements += statisticsService.getEventsStatisticsPerGroupId(EventType.BESKJED)
            measurements += statisticsService.getEventsStatisticsPerGroupId(EventType.OPPGAVE)
            measurements += statisticsService.getEventsStatisticsPerGroupId(EventType.INNBOKS)

            measurements += statisticsService.getGroupIdsPerUser(EventType.BESKJED)
            measurements += statisticsService.getGroupIdsPerUser(EventType.OPPGAVE)
            measurements += statisticsService.getGroupIdsPerUser(EventType.INNBOKS)

            measurements += statisticsService.getTextLength(EventType.BESKJED)
            measurements += statisticsService.getTextLength(EventType.OPPGAVE)
            measurements += statisticsService.getTextLength(EventType.INNBOKS)

            measurements += statisticsService.getCountUsersWithEvents(EventType.BESKJED)
            measurements += statisticsService.getCountUsersWithEvents(EventType.OPPGAVE)
            measurements += statisticsService.getCountUsersWithEvents(EventType.INNBOKS)

            measurements += statisticsService.getEventCount(EventType.BESKJED)
            measurements += statisticsService.getEventCount(EventType.OPPGAVE)
            measurements += statisticsService.getEventCount(EventType.INNBOKS)

            measurements += statisticsService.getActiveEventCount(EventType.BESKJED)
            measurements += statisticsService.getActiveEventCount(EventType.OPPGAVE)
            measurements += statisticsService.getActiveEventCount(EventType.INNBOKS)

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

    get("/stats/grouped/bruker/{type}/active") {
        try {
            val type = EventType.fromOriginalType(call.parameters["type"]!!)
            val measurement = statisticsService.getActiveEventsStatisticsPerUser(type)

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

    get("/stats/grouped/gruppering/{type}") {
        try {
            val type = EventType.fromOriginalType(call.parameters["type"]!!)
            val measurement = statisticsService.getEventsStatisticsPerGroupId(type)

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

    get("/stats/{type}/text-length") {
        try {
            val type = EventType.fromOriginalType(call.parameters["type"]!!)
            val measurement = statisticsService.getTextLength(type)

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


    get("/stats/{type}") {
        try {
            val type = EventType.fromOriginalType(call.parameters["type"]!!)
            val measurement = statisticsService.getEventCount(type)

            call.respond(HttpStatusCode.OK, measurement)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/stats/{type}/active") {
        try {
            val type = EventType.fromOriginalType(call.parameters["type"]!!)
            val measurement = statisticsService.getActiveEventCount(type)

            call.respond(HttpStatusCode.OK, measurement)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }
}
