package no.nav.personbruker.dittnav.eventhandler.api

import com.auth0.jwk.UrlJwkProvider
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.request.ApplicationRequest
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import no.nav.personbruker.dittnav.eventhandler.service.InformasjonEventService
import org.slf4j.LoggerFactory
import java.net.URL
import java.security.interfaces.RSAPublicKey

val log = LoggerFactory.getLogger(InformasjonEventService::class.java)
val informasjonEventService: InformasjonEventService = InformasjonEventService()

fun Routing.regelApi() {
    get("/sikkerhet") {
        val authToken: String? = getAuthToken(call.request)

        try {
            validateToken(authToken)
            log.info("* - - - - - - - - - - - * ValidateToken = OK * - - - - - - - - - - - * ")
            call.respondText(text = getPersonInfoFromCache(authToken), contentType = ContentType.Text.Plain)
        } catch (exception: Exception){
            call.response.status(HttpStatusCode.Unauthorized)
        }
    }
}

fun getAuthToken(request: ApplicationRequest): String? {
    return request.headers.get(HttpHeaders.Authorization)
}

fun validateToken(authToken: String?): DecodedJWT {
    var jwks_uri: String = System.getenv("jwks_uri") ?: "https://login.microsoftonline.com/navtestb2c.onmicrosoft.com/discovery/v2.0/keys?p=b2c_1a_idporten_ver1" //"default_value"
    val jwkProvider = UrlJwkProvider(URL(jwks_uri))

    val jwt = decodeToken(authToken)
    val jwk = jwkProvider.get(jwt.keyId)

    val publicKey = jwk.publicKey as? RSAPublicKey ?: throw Exception("Invalid key type")
    val algorithm = Algorithm.RSA256(publicKey, null)

    val verifier = JWT.require(algorithm)
            .withIssuer(getIssuerFromToken(jwt))
            .withAudience(getAudFromToken(jwt))
            .build()

    return verifier.verify(jwt.token)
}

fun decodeToken(authToken: String?): DecodedJWT {
    return JWT.decode(authToken.toString().substring(7))
}

fun getAudFromToken(authToken: DecodedJWT): String {
    return authToken.getClaim("aud").asString()
}

fun getIssuerFromToken(authToken: DecodedJWT): String {
    return authToken.getClaim("iss").asString()
}

fun getPersonInfoFromCache(authToken: String?): String {
    return informasjonEventService.getEventFromCache(getIdentFromToken(authToken))
}

fun getIdentFromToken(authToken: String?): String {
    val jwt: DecodedJWT = decodeToken(authToken)
    return jwt.getClaim("sub").toString()
}


