package no.nav.personbruker.dittnav.eventhandler

import com.auth0.jwk.JwkProviderBuilder
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import no.nav.personbruker.dittnav.eventhandler.api.healthApi
import no.nav.personbruker.dittnav.eventhandler.api.regelApi
import java.net.URL
import java.util.concurrent.TimeUnit

object Server {

    val disableJwt = false
    var jwkUrl = "https://security-token-service.nais.adeo.no/rest/v1/sts/jwks"
    val jwkIssuer = "https://security-token-service.nais.adeo.no"
    val jwkProvider = JwkProviderBuilder(URL(jwkUrl))
            .cached(10, 24, TimeUnit.HOURS)
            .rateLimited(10, 1, TimeUnit.MINUTES)
            .build()

    fun start() {
        val app = embeddedServer(Netty, port = 8080) {
            regelApi (jwkIssuer,
                    jwkProvider,
                    disableJwt)
            routing { healthApi() }
        }
        app.start(wait = false)
        Runtime.getRuntime().addShutdownHook(Thread {
            app.stop(5, 60, TimeUnit.SECONDS)
        })
    }
}
