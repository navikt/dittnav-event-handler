package no.nav.personbruker.dittnav.eventhandler.api

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.database.entity.Informasjon
import no.nav.personbruker.dittnav.eventhandler.service.InformasjonEventService
import no.nav.personbruker.dittnav.eventhandler.util.AuthenticationUtil.authenticateRequest

fun Routing.fetchEventsApi() {

    val informasjonEventService = InformasjonEventService()

    get("/fetch/informasjon") {
        authenticateRequest { ident ->
            runBlocking {
                val events = informasjonEventService.getEventsFromCacheForUser(ident)
                printOneEventPerLine(events)
                call.respondText(text = events.toString(), contentType = ContentType.Application.Json)
            }
        }

    }

}

private fun printOneEventPerLine(events: List<Informasjon>) {
    var counter = 0
    for (event in events) {
        counter++
        println("Event $counter: $event")
    }
    println("Total number of events: $counter")

}
