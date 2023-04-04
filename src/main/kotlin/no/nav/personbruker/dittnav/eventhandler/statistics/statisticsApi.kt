package no.nav.personbruker.dittnav.eventhandler.statistics

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging
import no.nav.personbruker.dittnav.eventhandler.common.VarselType

fun Route.statisticsSystemClientApi(statisticsService: EventStatisticsService) {

    get("/stats/grouped/bruker/{type}") {

        val type = VarselType.fromOriginalType(call.parameters["type"]!!)
        val measurement = statisticsService.getEventsStatisticsPerUser(type)

        call.respond(HttpStatusCode.OK, measurement)
    }

    get("/stats/grouped/bruker") {

        val measurement = statisticsService.getTotalEventsStatisticsPerUser()

        call.respond(HttpStatusCode.OK, measurement)
    }

    get("/stats/grouped/bruker/active/{type}") {

        val type = VarselType.fromOriginalType(call.parameters["type"]!!)
        val measurement = statisticsService.getActiveEventsStatisticsPerUser(type)

        call.respond(HttpStatusCode.OK, measurement)
    }

    get("/stats/grouped/bruker/active") {

        val measurement = statisticsService.getTotalActiveEventsStatisticsPerUser()

        call.respond(HttpStatusCode.OK, measurement)
    }

    get("/stats/grouped/bruker/active-rate/{type}") {

        val type = VarselType.fromOriginalType(call.parameters["type"]!!)
        val measurement = statisticsService.getActiveRateEventsStatisticsPerUser(type)

        call.respond(HttpStatusCode.OK, measurement)
    }

    get("/stats/grouped/bruker/active-rate") {

        val measurement = statisticsService.getTotalActiveRateEventsStatisticsPerUser()

        call.respond(HttpStatusCode.OK, measurement)
    }

    get("/stats/grouped/gruppering/{type}") {

        val type = VarselType.fromOriginalType(call.parameters["type"]!!)
        val measurement = statisticsService.getEventsStatisticsPerGroupId(type)

        call.respond(HttpStatusCode.OK, measurement)
    }

    get("/stats/grouped/gruppering") {

        val measurement = statisticsService.getTotalEventsStatisticsPerGroupId()

        call.respond(HttpStatusCode.OK, measurement)
    }

    //TODO: Slett unødvendige endepunkt

    get("/stats/grouped/bruker/grupperings/{type}") {
        val type = VarselType.fromOriginalType(call.parameters["type"]!!)
        val measurement = statisticsService.getGroupIdsPerUser(type)
        call.respond(HttpStatusCode.OK, measurement)
    }

    get("/stats/grouped/bruker/grupperings") {
        val measurement = statisticsService.getTotalGroupIdsPerUser()
        call.respond(HttpStatusCode.OK, measurement)
    }

    get("/stats/text-length/{type}") {
        val type = VarselType.fromOriginalType(call.parameters["type"]!!)
        val measurement = statisticsService.getTextLength(type)
        call.respond(HttpStatusCode.OK, measurement)
    }

    get("/stats/text-length") {
        val measurement = statisticsService.getTotalTextLength()
        call.respond(HttpStatusCode.OK, measurement)
    }

    get("/stats/bruker-count/{type}") {
        val type = VarselType.fromOriginalType(call.parameters["type"]!!)
        val measurement = statisticsService.getCountUsersWithEvents(type)
        call.respond(HttpStatusCode.OK, measurement)
    }

    get("/stats/bruker-count") {
        val measurement = statisticsService.getTotalCountUsersWithEvents()
        call.respond(HttpStatusCode.OK, measurement)
    }

    get("/stats/count/{type}") {
        val type = VarselType.fromOriginalType(call.parameters["type"]!!)
        val measurement = statisticsService.getEventCount(type)
        call.respond(HttpStatusCode.OK, measurement)
    }

    get("/stats/count") {
        val measurement = statisticsService.getTotalEventCount()
        call.respond(HttpStatusCode.OK, measurement)
    }

    get("/stats/count/active/{type}") {
        val type = VarselType.fromOriginalType(call.parameters["type"]!!)
        val measurement = statisticsService.getActiveEventCount(type)
        call.respond(HttpStatusCode.OK, measurement)
    }

    get("/stats/count/active") {
        val measurement = statisticsService.getTotalActiveEventCount()
        call.respond(HttpStatusCode.OK, measurement)
    }

    get("/stats/frequency-distribution/active/{type}") {
        val measurement = if (call.parameters["type"]!! == "all") {
            statisticsService.getTotalActiveEventsFrequencyDistribution()
        } else {
            val type = VarselType.fromOriginalType(call.parameters["type"]!!)
            statisticsService.getActiveEventsFrequencyDistribution(type)
        }

        call.respond(HttpStatusCode.OK, measurement)
    }
}
