package no.nav.personbruker.dittnav.eventhandler.api

import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.request.ApplicationRequest
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get

fun Routing.regelApi() {
    get("/sikkerhet") {
        val authToken : String? = getAuthToken(call.request)

        if(validateToken(authToken)){
            val ident :String  = getIdFromToken(authToken)
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
    //TODO timestamp, skjekke mot azure?
    return authToken != null
}

fun getIdFromToken(authToken: String?): String{
    val decodedJWT : DecodedJWT = decodeToken(authToken)
    return decodedJWT.getClaim("sub").asString()
}

fun decodeToken(authToken: String?): DecodedJWT{
    return JWT.decode(authToken.toString().substring(7))
}

