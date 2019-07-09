package no.nav.personbruker.dittnav.eventhandler.api

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.post
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.kafka.Producer
import no.nav.personbruker.dittnav.eventhandler.util.AuthenticationUtil.authenticateRequest

fun Routing.produceEventsApi() {

    val producer = Producer

    post("/produce/informasjon") {
        authenticateRequest { ident ->
            runBlocking {
                producer.produceInformasjonEventForIdent(ident)
                val msg = "Et informasjons-event for identen: $ident har blitt lagt på kafka."
                call.respondText(text = msg, contentType = ContentType.Text.Plain)
            }
        }
    }

}
