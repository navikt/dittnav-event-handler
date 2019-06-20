package no.nav.personbruker.dittnav.eventhandler.api

import com.auth0.jwk.JwkProvider
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.authenticate
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.auth.jwt.jwt
import io.ktor.features.DefaultHeaders
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing

val jwkRealm = "dittnav-event-handler"

fun Application.regelApi(
        jwtIssuer: String,
        jwkProvider: JwkProvider,
        disableJwt: Boolean = false
) {
    install(DefaultHeaders)

    install(Authentication) {
        jwt {
            verifier(jwkProvider, jwtIssuer)
            realm = jwkRealm
            validate { credentials ->
                println(credentials.payload.subject.toString() + " -authenticated-")
                JWTPrincipal(credentials.payload)
            }
        }
    }

    routing {
        authenticate(optional = disableJwt) {
            get("/sikkerhet") {
                call.respondText(text = "OK", contentType = ContentType.Text.Plain)
            }
        }
    }
}