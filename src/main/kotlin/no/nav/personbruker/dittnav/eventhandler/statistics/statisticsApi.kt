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

    get("/stats/grouped/bruker/{type}") {
        try {
            val type = EventType.fromOriginalType(call.parameters["type"]!!)
            val measurement = statisticsService.getEventsStatisticsPerUser(type)

            call.respond(HttpStatusCode.OK, measurement)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/stats/grouped/bruker") {
        try {
            val measurement = statisticsService.getTotalEventsStatisticsPerUser()

            call.respond(HttpStatusCode.OK, measurement)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/stats/grouped/bruker/active/{type}") {
        try {
            val type = EventType.fromOriginalType(call.parameters["type"]!!)
            val measurement = statisticsService.getActiveEventsStatisticsPerUser(type)

            call.respond(HttpStatusCode.OK, measurement)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/stats/grouped/bruker/active") {
        try {
            val measurement = statisticsService.getTotalActiveEventsStatisticsPerUser()

            call.respond(HttpStatusCode.OK, measurement)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/stats/grouped/bruker/active-rate/{type}") {
        try {
            val type = EventType.fromOriginalType(call.parameters["type"]!!)
            val measurement = statisticsService.getActiveRateEventsStatisticsPerUser(type)

            call.respond(HttpStatusCode.OK, measurement)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/stats/grouped/bruker/active-rate") {
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

    get("/stats/grouped/gruppering") {
        try {
            val measurement = statisticsService.getTotalEventsStatisticsPerGroupId()

            call.respond(HttpStatusCode.OK, measurement)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/stats/grouped/bruker/grupperings/{type}") {
        try {
            val type = EventType.fromOriginalType(call.parameters["type"]!!)
            val measurement = statisticsService.getGroupIdsPerUser(type)

            call.respond(HttpStatusCode.OK, measurement)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/stats/grouped/bruker/grupperings") {
        try {
            val measurement = statisticsService.getTotalGroupIdsPerUser()

            call.respond(HttpStatusCode.OK, measurement)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/stats/text-length/{type}") {
        try {
            val type = EventType.fromOriginalType(call.parameters["type"]!!)
            val measurement = statisticsService.getTextLength(type)

            call.respond(HttpStatusCode.OK, measurement)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/stats/text-length") {
        try {
            val measurement = statisticsService.getTotalTextLength()

            call.respond(HttpStatusCode.OK, measurement)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/stats/bruker-count/{type}") {
        try {
            val type = EventType.fromOriginalType(call.parameters["type"]!!)
            val measurement = statisticsService.getCountUsersWithEvents(type)

            call.respond(HttpStatusCode.OK, measurement)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/stats/bruker-count") {
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

    get("/stats/count") {
        try {
            val measurement = statisticsService.getTotalEventCount()

            call.respond(HttpStatusCode.OK, measurement)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/stats/count/active/{type}") {
        try {
            val type = EventType.fromOriginalType(call.parameters["type"]!!)
            val measurement = statisticsService.getActiveEventCount(type)

            call.respond(HttpStatusCode.OK, measurement)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/stats/count/active") {
        try {
            val measurement = statisticsService.getTotalActiveEventCount()

            call.respond(HttpStatusCode.OK, measurement)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }
}