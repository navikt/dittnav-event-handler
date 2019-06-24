package no.nav.personbruker.dittnav.eventhandler

import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import no.nav.personbruker.dittnav.eventhandler.api.healthApi
import no.nav.personbruker.dittnav.eventhandler.api.regelApi
import java.util.concurrent.TimeUnit

object Server {
    fun start() {
        val app = embeddedServer(Netty, port = 8080) {
            routing { healthApi(); regelApi() }
        }
        app.start(wait = false)
        Runtime.getRuntime().addShutdownHook(Thread {
            app.stop(5, 60, TimeUnit.SECONDS)
        })
    }
}
