package no.nav.personbruker.dittnav.eventhandler

import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import no.nav.personbruker.dittnav.eventhandler.api.healthApi

object Server {

    fun startServer(port: Int): NettyApplicationEngine {
        return embeddedServer(Netty, port) {
            routing { healthApi() }
        }
    }
}
