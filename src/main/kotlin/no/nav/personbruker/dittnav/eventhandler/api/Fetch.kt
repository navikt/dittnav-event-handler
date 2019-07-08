package no.nav.personbruker.dittnav.eventhandler.api

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.service.InformasjonEventService
import no.nav.personbruker.dittnav.eventhandler.util.AuthenticationUtil.authenticateRequest

fun Routing.fetchEventsApi() {

    val informasjonEventService = InformasjonEventService()

    get("/fetch/informasjon") {
        authenticateRequest { ident ->
            runBlocking {
                val events = informasjonEventService.getEventsFromCacheForUser(ident)
                call.respond(HttpStatusCode.OK, events)
            }
        }

    }

}
