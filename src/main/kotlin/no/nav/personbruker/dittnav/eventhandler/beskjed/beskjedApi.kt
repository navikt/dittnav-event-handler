package no.nav.personbruker.dittnav.eventhandler.beskjed

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import no.nav.personbruker.dittnav.eventhandler.common.ExternalResponse
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.respondWithError
import no.nav.personbruker.dittnav.eventhandler.common.isDryrun
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

    get("/produce/beskjed/all") {
        try {
            val numberOfProcessedEvents = backupBeskjedService.produceBeskjedEventsForAllBeskjedEventsInCache(true)
            call.respond(HttpStatusCode.OK, "Dryrun = true. Antall prosesserte beskjed-eventer (IKKE sendt til Kafka): $numberOfProcessedEvents")
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    post("/produce/beskjed/all") {
        try {
            val externalResponse = call.receive<ExternalResponse>()
            if (isDryrun(externalResponse.dryRun)) {
                val numberOfProcessedEvents = backupBeskjedService.produceBeskjedEventsForAllBeskjedEventsInCache(true)
                call.respond(HttpStatusCode.OK, "Dryrun = true. Antall prosesserte beskjed-eventer (IKKE sendt til Kafka): $numberOfProcessedEvents")
            } else {
                val numberOfProcessedEvents = backupBeskjedService.produceBeskjedEventsForAllBeskjedEventsInCache(false)
                call.respond(HttpStatusCode.OK, "Dryrun = false. Antall prosesserte beskjed-eventer (sendt til Kafka): $numberOfProcessedEvents")
            }
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/produce/done/from/inactive/beskjed/") {
        try {
            val numberOfProcessedEvents = backupBeskjedService.produceDoneEventsFromAllInactiveBeskjedEvents(true)
            call.respond(HttpStatusCode.OK, "Dryrun = true. Antall inaktive beskjed-eventer (IKKE sendt til Kafka): $numberOfProcessedEvents")
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    post("/produce/done/from/inactive/beskjed/") {
        try {
            val externalResponse = call.receive<ExternalResponse>()
            if (isDryrun(externalResponse.dryRun)) {
                val numberOfProcessedEvents = backupBeskjedService.produceDoneEventsFromAllInactiveBeskjedEvents(true)
                call.respond(HttpStatusCode.OK, "Dryrun = true. Antall inaktive beskjed-eventer (IKKE sendt til Kafka): $numberOfProcessedEvents")
            } else {
                val numberOfProcessedEvents = backupBeskjedService.produceDoneEventsFromAllInactiveBeskjedEvents(false)
                call.respond(HttpStatusCode.OK, "Dryrun = false. Antall inaktive beskjed-eventer (sendt til Kafka): $numberOfProcessedEvents")
            }
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }
}

