package no.nav.personbruker.dittnav.eventhandler.api

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.eventhandler.service.InformasjonEventService
import no.nav.personbruker.dittnav.eventhandler.util.AuthenticationUtil
import no.nav.personbruker.dittnav.eventhandler.util.AuthenticationUtil.authenticateRequest
import org.slf4j.LoggerFactory

val log = LoggerFactory.getLogger(InformasjonEventService::class.java)
val informasjonEventService: InformasjonEventService = InformasjonEventService()

fun Routing.regelApi() {
    get("/sikkerhet") {
        authenticateRequest { ident ->
            runBlocking {
                log.info("* - - - - - - - - - - - * ValidateToken = OK * - - - - - - - - - - - * ")
                val personInfo = AuthenticationUtil.getPersonInfoFromCache(ident)
                val msg = "### Ident: $ident, personInfo: $personInfo"
                call.respondText(text = msg, contentType = ContentType.Text.Plain)
            }
        }
    }

}
