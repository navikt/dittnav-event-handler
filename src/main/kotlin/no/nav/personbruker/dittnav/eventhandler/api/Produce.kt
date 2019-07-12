package no.nav.personbruker.dittnav.eventhandler.api

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.post
import no.nav.personbruker.dittnav.eventhandler.config.extractIdentFromLoginContext
import no.nav.personbruker.dittnav.eventhandler.kafka.Producer

fun Route.produceEventsApi() {

    val producer = Producer

    post("/produce/informasjon") {
        val ident = extractIdentFromLoginContext()
        producer.produceInformasjonEventForIdent(ident)
        val msg = "Et informasjons-event for identen: $ident har blitt lagt på kafka."
        call.respondText(text = msg, contentType = ContentType.Text.Plain)
    }

}
