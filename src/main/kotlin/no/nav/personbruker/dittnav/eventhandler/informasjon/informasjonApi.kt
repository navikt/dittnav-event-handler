package no.nav.personbruker.dittnav.eventhandler.informasjon

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import no.nav.personbruker.dittnav.eventhandler.config.extractIdentFromLoginContext

fun Route.informasjonApi(informasjonEventService: InformasjonEventService) {

    get("/fetch/informasjon") {
        val ident = extractIdentFromLoginContext()
        val events = informasjonEventService.getEventsFromCacheForUser(ident)
        call.respond(HttpStatusCode.OK, events)
    }

    post("/produce/informasjon") {
        val postParametersDto = call.receive<ProduceInformasjonDto>()
        val ident = extractIdentFromLoginContext()
        InformasjonProducer.produceInformasjonEventForIdent(ident, postParametersDto)
        val msg = "Et informasjons-event for identen: $ident har blitt lagt på kafka."
        call.respondText(text = msg, contentType = ContentType.Text.Plain)
    }

}
