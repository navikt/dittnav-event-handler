package no.nav.personbruker.dittnav.eventhandler.config

import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.authentication
import io.ktor.auth.jwt.JWTAuthenticationProvider
import io.ktor.auth.jwt.JWTCredential
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.util.pipeline.PipelineContext
import java.net.URL
import java.util.concurrent.TimeUnit

fun JWTAuthenticationProvider.Configuration.setupOidcAuthentication(environment: Environment) {
    val jwkProvider = initJwkProvider(environment.securityJwksUri)
    verifier(jwkProvider, environment.securityJwksIssuer)
    realm = "dittnav-event-handler"
    validate { credentials ->
        return@validate validationLogicPerRequest(credentials, environment)
    }
}

fun initJwkProvider(securityJwksUri: URL): JwkProvider {
    val jwkProvider = JwkProviderBuilder(securityJwksUri)
            .cached(10, 24, TimeUnit.HOURS)
            .rateLimited(10, 1, TimeUnit.MINUTES)
            .build()
    return jwkProvider
}

fun validationLogicPerRequest(credentials: JWTCredential, environment: Environment): JWTPrincipal? {
    Server.log.info("#### Running authenticaiton!")
    if (credentials.payload.audience.contains(environment.securityAudience)
            && credentials.payload.issuer == environment.securityJwksIssuer) {
        Server.log.info("User authenticated!")
        return JWTPrincipal(credentials.payload)

    } else {
        Server.log.warn("User NOT authenticated!")
        return null
    }
}

fun PipelineContext<Unit, ApplicationCall>.extractIdentFromLoginContext() =
        (call.authentication.principal as JWTPrincipal).payload.subject