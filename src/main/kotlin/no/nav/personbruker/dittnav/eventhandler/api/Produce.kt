package no.nav.personbruker.dittnav.eventhandler.api

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.request.receive
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.post
import no.nav.personbruker.dittnav.eventhandler.config.extractIdentFromLoginContext
import no.nav.personbruker.dittnav.eventhandler.kafka.Producer

fun Route.produceEventsApi() {

    val producer = Producer

    post("/produce/informasjon") {
        val postParametersDto = call.receive<ProduceDto>()
        val ident = extractIdentFromLoginContext()
        producer.produceInformasjonEventForIdent(ident, postParametersDto)
        val msg = "Et informasjons-event for identen: $ident har blitt lagt på kafka."
        call.respondText(text = msg, contentType = ContentType.Text.Plain)
    }

    post("/produce/oppgave") {
        val postParametersDto = call.receive<ProduceDto>()
        val ident = extractIdentFromLoginContext()
        producer.produceOppgaveEventForIdent(ident, postParametersDto)
        val msg = "Et oppgave-event for identen: $ident har blitt lagt på kafka."
        call.respondText(text = msg, contentType = ContentType.Text.Plain)
    }


}

class ProduceDto(val tekst: String, val link: String) {
    override fun toString(): String {
        return "ProduceDto{tekst='$tekst', lenke='$link'}"
    }
}
