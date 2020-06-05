package no.nav.personbruker.dittnav.eventhandler.oppgave

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

fun Route.oppgaveApi(oppgaveEventService: OppgaveEventService, backupOppgaveService: BackupOppgaveService) {

    val log = LoggerFactory.getLogger(OppgaveEventService::class.java)

    get("/fetch/oppgave/aktive") {
        try {
            val aktiveOppgaveEvents = oppgaveEventService.getActiveCachedEventsForUser(innloggetBruker)
            call.respond(HttpStatusCode.OK, aktiveOppgaveEvents)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/fetch/oppgave/inaktive") {
        try {
            val inaktiveOppgaveEvents = oppgaveEventService.getInactiveCachedEventsForUser(innloggetBruker)
            call.respond(HttpStatusCode.OK, inaktiveOppgaveEvents)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/fetch/oppgave/all") {
        try {
            val oppgaveEvents = oppgaveEventService.getAllCachedEventsForUser(innloggetBruker)
            call.respond(HttpStatusCode.OK, oppgaveEvents)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/produce/oppgave/all") {
        try {
            val numberOfProcessedEvents = backupOppgaveService.produceOppgaveEventsForAllOppgaveEventsInCache(true)
            call.respond(HttpStatusCode.OK, "Dryrun = true. Antall prosesserte oppgave-eventer (IKKE sendt til Kafka): $numberOfProcessedEvents")
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    post("/produce/oppgave/all") {
        try {
            val externalResponse = call.receive<ExternalResponse>()
            if (isDryrun(externalResponse.dryRun)) {
                val numberOfProcessedEvents = backupOppgaveService.produceOppgaveEventsForAllOppgaveEventsInCache(true)
                call.respond(HttpStatusCode.OK, "Dryrun = true. Antall prosesserte oppgave-eventer (IKKE sendt til Kafka): $numberOfProcessedEvents")
            } else {
                val numberOfProcessedEvents = backupOppgaveService.produceOppgaveEventsForAllOppgaveEventsInCache(true)
                call.respond(HttpStatusCode.OK, "Dryrun = false. Antall prosesserte oppgave-eventer (sendt til Kafka): $numberOfProcessedEvents")
            }
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/produce/done/from/inactive/oppgave") {
        try {
            var numberOfProcessedEvents = backupOppgaveService.produceDoneEventsFromAllInactiveOppgaveEvents(true)
            call.respond(HttpStatusCode.OK, "Dryrun = true. Antall inaktive oppgave-eventer (IKKE sendt til Kafka): $numberOfProcessedEvents")
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    post("/produce/done/from/inactive/oppgave") {
        try {
            val externalResponse = call.receive<ExternalResponse>()
            if (isDryrun(externalResponse.dryRun)) {
                var numberOfProcessedEvents = backupOppgaveService.produceDoneEventsFromAllInactiveOppgaveEvents(true)
                call.respond(HttpStatusCode.OK, "Dryrun = true. Antall inaktive oppgave-eventer (IKKE sendt til Kafka): $numberOfProcessedEvents")
            } else {
                var numberOfProcessedEvents = backupOppgaveService.produceDoneEventsFromAllInactiveOppgaveEvents(true)
                call.respond(HttpStatusCode.OK, "Dryrun = false. Antall inaktive oppgave-eventer (sendt til Kafka): $numberOfProcessedEvents")
            }
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }
}
