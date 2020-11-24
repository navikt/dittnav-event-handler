package no.nav.personbruker.dittnav.eventhandler.common.produsent

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.respondWithError
import org.slf4j.LoggerFactory

fun Routing.producerNameAliasApi(producerNameAliasService: ProducerNameAliasService) {

    val log = LoggerFactory.getLogger(ProducerNameAliasService::class.java)

    get("/producer/alias") {
        try {
            val produsentnavn = producerNameAliasService.getProducerNameAlias(call.request.queryParameters["systembruker"])
            call.respond(HttpStatusCode.OK, produsentnavn)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }
}