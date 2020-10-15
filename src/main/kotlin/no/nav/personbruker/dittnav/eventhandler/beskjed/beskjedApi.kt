package no.nav.personbruker.dittnav.eventhandler.beskjed

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.respondWithError
import no.nav.personbruker.dittnav.eventhandler.common.validation.validateNonNullFieldMaxLength
import no.nav.personbruker.dittnav.eventhandler.config.innloggetBruker
import org.slf4j.LoggerFactory

fun Route.beskjedApi(beskjedEventService: BeskjedEventService, backupBeskjedService: BackupBeskjedService) {

    val log = LoggerFactory.getLogger(BeskjedEventService::class.java)

    get("/fetch/beskjed/aktive") {
        try {
            val aktiveBeskjedEvents = beskjedEventService.getActiveCachedEventsForUser(innloggetBruker)
            call.respond(HttpStatusCode.OK, aktiveBeskjedEvents)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/fetch/beskjed/inaktive") {
        try {
            val inaktiveBeskjedEvents = beskjedEventService.getInactiveCachedEventsForUser(innloggetBruker)
            call.respond(HttpStatusCode.OK, inaktiveBeskjedEvents)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/fetch/beskjed/all") {
        try {
            val beskjedEvents = beskjedEventService.getAllEventsFromCacheForUser(innloggetBruker)
            call.respond(HttpStatusCode.OK, beskjedEvents)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/fetch/beskjed/grouped") {
        try {
            val grupperingsId = validateNonNullFieldMaxLength(call.request.queryParameters["grupperingsid"], "grupperingsid", 100)
            val produsent = validateNonNullFieldMaxLength(call.request.queryParameters["produsent"], "produsent", 100)
            val beskjedEvents = beskjedEventService.getAllGroupedEventsFromCacheForUser(innloggetBruker, grupperingsId, produsent)
            call.respond(HttpStatusCode.OK, beskjedEvents)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }
}


