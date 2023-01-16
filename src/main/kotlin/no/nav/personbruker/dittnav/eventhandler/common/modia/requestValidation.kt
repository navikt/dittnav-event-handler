package no.nav.personbruker.dittnav.eventhandler.common.modia


import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.util.pipeline.PipelineContext
import mu.KotlinLogging

val log = KotlinLogging.logger {}

suspend inline fun PipelineContext<Unit, ApplicationCall>.doIfValidRequest(handler: (fnr: User) -> Unit) {
    val fnrHeaderName = "fodselsnummer"
    val authlevelHeaderName = "authlevel"
    val fnr = call.request.headers[fnrHeaderName]
    val authlevel = call.request.headers[authlevelHeaderName]?.toInt()

    if (fnr != null && authlevel != null) {
        if (isFodselsnummerOfValidLength(fnr)) {
            val user = User(fnr, authlevel)
            handler.invoke(user)
        } else {
            val msg = "Header-en '$fnrHeaderName' inneholder ikke et gyldig fødselsnummer."
            log.warn(msg)
            call.respond(HttpStatusCode.BadRequest, msg)
        }
    } else {

        val msg = "Requesten mangler påkrevde headere $fnrHeaderName og/eller $authlevelHeaderName"
        log.warn(msg)
        call.respond(HttpStatusCode.BadRequest, msg)
    }
}

fun isFodselsnummerOfValidLength(fnrHeader: String) = fnrHeader.isNotEmpty() && fnrHeader.length == 11
