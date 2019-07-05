package no.nav.personbruker.dittnav.eventhandler.util

import com.auth0.jwk.UrlJwkProvider
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.request.ApplicationRequest
import io.ktor.util.pipeline.PipelineContext
import no.nav.personbruker.dittnav.eventhandler.api.informasjonEventService
import no.nav.personbruker.dittnav.eventhandler.api.log
import no.nav.personbruker.dittnav.eventhandler.database.entity.Informasjon
import java.net.URL
import java.security.interfaces.RSAPublicKey

object AuthenticationUtil {

    fun PipelineContext<Unit, ApplicationCall>.authenticateRequest(runIfAuthenticated: (ident: String) -> Unit) {
        try {
            val authToken = getAuthToken(call.request)
            validateToken(authToken)

            val extractedIdent = getIdentFromToken(authToken)
            runIfAuthenticated(extractedIdent)

        } catch (e: Exception) {
            log.warn("User not authenticated: ${e.message}")
            call.response.status(HttpStatusCode.Unauthorized)
        }
    }

    fun getAuthToken(request: ApplicationRequest): String {
        return request.headers[HttpHeaders.Authorization] ?: throw Exception("The authentication header is missing")
    }

    fun validateToken(authToken: String?): DecodedJWT {
        val jwksUri: String = System.getenv("jwks_uri") ?: "changemeForRunningLocally"
        val jwkProvider = UrlJwkProvider(URL(jwksUri))

        val jwt = decodeToken(authToken)
        val jwk = jwkProvider.get(jwt.keyId)

        val publicKey = jwk.publicKey as? RSAPublicKey ?: throw Exception("Invalid key type")
        val algorithm = Algorithm.RSA256(publicKey, null)

        val verifier = JWT.require(algorithm)
                .withIssuer(getIssuerFromToken(jwt))
                .withAudience(getAudienceFromToken(jwt))
                .build()

        return verifier.verify(jwt.token)
    }

    fun decodeToken(authToken: String?): DecodedJWT {
        return JWT.decode(authToken.toString().substring(7))
    }

    fun getAudienceFromToken(authToken: DecodedJWT): String {
        return authToken.getClaim("aud").asString()
    }

    fun getIssuerFromToken(authToken: DecodedJWT): String {
        return authToken.getClaim("iss").asString()
    }

    fun getPersonInfoFromCache(ident: String): List<Informasjon> {
        return informasjonEventService.getEventsFromCacheForUser(ident)
    }

    fun getIdentFromToken(authToken: String?): String {
        val jwt: DecodedJWT = decodeToken(authToken)
        return jwt.getClaim("sub").asString()
    }

}