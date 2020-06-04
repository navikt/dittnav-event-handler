package no.nav.personbruker.dittnav.eventhandler.oppgave

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.respondWithError
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
        } catch(exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/fetch/oppgave/all") {
        try {
            val oppgaveEvents = oppgaveEventService.getAllCachedEventsForUser(innloggetBruker)
            call.respond(HttpStatusCode.OK, oppgaveEvents)
        } catch(exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/produce/oppgave/all") {
        try {
            val numberOfProcessedEvents = backupOppgaveService.produceOppgaveEventsForAllOppgaveEventsInCache()
            call.respond(HttpStatusCode.OK, "Antall prosesserte oppgave-eventer (sendt til Kafka): $numberOfProcessedEvents")
        } catch(exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/produce/done/from/inactive/oppgave") {
        try {
            var numberOfProcessedEvents = backupOppgaveService.produceDoneEventsFromAllInactiveOppgaveEvents()
            call.respond(HttpStatusCode.OK, "Antall inaktive oppgave-eventer (sendt til Kafka): $numberOfProcessedEvents")

        } catch(exception: Exception) {
            respondWithError(call, log, exception)
        }
    }
}
