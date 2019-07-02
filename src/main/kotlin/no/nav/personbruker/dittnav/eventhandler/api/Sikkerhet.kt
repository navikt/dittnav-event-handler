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
import java.net.URL
import java.security.interfaces.RSAPublicKey

fun Routing.regelApi() {
    get("/sikkerhet") {
        val authToken : String? = getAuthToken(call.request)

        if(validateToken(authToken)){
            call.respondText(text = "OK" , contentType = ContentType.Text.Plain)
        }
        else{
            call.response.status(HttpStatusCode.Unauthorized)
        }
    }
}

fun getAuthToken(request: ApplicationRequest): String?{
    return request.headers.get(HttpHeaders.Authorization)
}

fun validateToken(authToken: String?): Boolean {
    var result: Boolean
    var jwks_uri: String = System.getenv("jwks_uri") ?: "default_value"
    val jwkProvider = UrlJwkProvider( URL(jwks_uri) )

    val jwt =  decodeToken(authToken)
    val jwk = jwkProvider.get(jwt.keyId)

    val publicKey = jwk.publicKey as? RSAPublicKey ?: throw Exception("Invalid key type")
    val algorithm = Algorithm.RSA256(publicKey, null)

    val verifier = JWT.require(algorithm)
            .withIssuer(getIssuerFromToken(jwt))
            .withAudience(getAudFromToken(jwt))
            .build()

    result =
            try {
                verifier.verify(jwt.token)
                true
            } catch (e: Exception) {
                false
            }
    return result
}

fun decodeToken(authToken: String?): DecodedJWT{
    return JWT.decode(authToken.toString().substring(7))
}

fun getAudFromToken(authToken: DecodedJWT): String {
    return authToken.getClaim("aud").asString()
}

fun getIssuerFromToken(authToken: DecodedJWT): String {
    return authToken.getClaim("iss").asString()
}

fun getIdentFromToken(authToken: DecodedJWT): String{
    return authToken.getClaim("sub").asString()
}


