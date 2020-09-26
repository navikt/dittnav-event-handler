package no.nav.personbruker.dittnav.eventhandler.brukernotifikasjon

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.respondWithError
import no.nav.personbruker.dittnav.eventhandler.config.innloggetBruker
import org.slf4j.LoggerFactory

fun Route.brukernotifikasjoner(brukernotifikasjonService: BrukernotifikasjonService) {

    val log = LoggerFactory.getLogger(BrukernotifikasjonService::class.java)

    get("/count/brukernotifikasjoner") {
        try {
            val numberOfEvents = brukernotifikasjonService.totalNumberOfEvents(innloggetBruker)
            call.respond(HttpStatusCode.OK, numberOfEvents)

        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

    get("/count/brukernotifikasjoner/active") {
        try {
            val numberOfEvents = brukernotifikasjonService.numberOfActiveEvents(innloggetBruker)
            call.respond(HttpStatusCode.OK, numberOfEvents)

        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }

}
