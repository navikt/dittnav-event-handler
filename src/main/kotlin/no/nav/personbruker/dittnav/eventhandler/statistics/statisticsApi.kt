package no.nav.personbruker.dittnav.eventhandler.statistics

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import mu.KotlinLogging
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.respondWithError
import no.nav.personbruker.dittnav.eventhandler.common.VarselType

fun Route.statisticsSystemClientApi(statisticsService: EventStatisticsService) {

    val log = KotlinLogging.logger {}

    get("/stats/grouped/bruker/{type}") {
        try {
            val type = VarselType.fromOriginalType(call.parameters["type"]!!)
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
            val type = VarselType.fromOriginalType(call.parameters["type"]!!)
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
            val type = VarselType.fromOriginalType(call.parameters["type"]!!)
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
            val type = VarselType.fromOriginalType(call.parameters["type"]!!)
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

    //TODO: Slett unødvendige endepunkt

    get("/stats/grouped/bruker/grupperings/{type}") {
        try {
            val type = VarselType.fromOriginalType(call.parameters["type"]!!)
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
            val type = VarselType.fromOriginalType(call.parameters["type"]!!)
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
            val type = VarselType.fromOriginalType(call.parameters["type"]!!)
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
            val type = VarselType.fromOriginalType(call.parameters["type"]!!)
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
            val type = VarselType.fromOriginalType(call.parameters["type"]!!)
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

    get("/stats/frequency-distribution/active/{type}") {
        try {
            val measurement = if(call.parameters["type"]!! == "all") {
                statisticsService.getTotalActiveEventsFrequencyDistribution()
            } else {
                val type = VarselType.fromOriginalType(call.parameters["type"]!!)
                statisticsService.getActiveEventsFrequencyDistribution(type)
            }

            call.respond(HttpStatusCode.OK, measurement)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }
}
