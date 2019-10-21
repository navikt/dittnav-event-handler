package no.nav.personbruker.dittnav.eventhandler.config

import com.auth0.jwt.JWT
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpHeaders
import io.ktor.util.pipeline.PipelineContext

// Disse metodene er midlertidige, fram til vi finner en mer standard måte å hente ut subject-et på.
fun PipelineContext<Unit, ApplicationCall>.extractIdentFromToken(): String {
    var authToken = getTokenFromHeader()
    if (authToken == null) {
        authToken = getTokenFromCookie()
    }
    verifyThatATokenWasFound(authToken)
    return extractSubject(authToken)
}

private fun verifyThatATokenWasFound(authToken: String?) {
    if (authToken == null) {
        throw Exception("Token ble ikke funnet. Dette skal ikke kunne skje.")
    }
}

private fun PipelineContext<Unit, ApplicationCall>.getTokenFromHeader() =
        call.request.headers[HttpHeaders.Authorization]?.replace("Bearer ", "")

private fun PipelineContext<Unit, ApplicationCall>.getTokenFromCookie() =
        call.request.cookies["selvbetjening-idtoken"]

private fun extractSubject(authToken: String?): String {
    val jwt = JWT.decode(authToken)
    return jwt.getClaim("sub").asString() ?: "subject (ident) ikke funnet"
}
