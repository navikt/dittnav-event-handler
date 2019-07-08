package no.nav.personbruker.dittnav.eventhandler.config

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.jackson.jackson
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.prometheus.client.hotspot.DefaultExports
import no.nav.personbruker.dittnav.eventhandler.api.fetchEventsApi
import no.nav.personbruker.dittnav.eventhandler.api.healthApi
import no.nav.personbruker.dittnav.eventhandler.api.produceEventsApi
import no.nav.personbruker.dittnav.eventhandler.api.regelApi
import java.util.concurrent.TimeUnit

object Server {

    const val portNumber = 8080

    fun configure(): NettyApplicationEngine {
        DefaultExports.initialize()
        val app = embeddedServer(Netty, port = portNumber) {

            install(DefaultHeaders)

            install(ContentNegotiation) {
                jackson {
                    enable(SerializationFeature.INDENT_OUTPUT)
                    registerModule(JavaTimeModule())
                }
            }

            routing {
                healthApi()
                regelApi()
                fetchEventsApi()
                produceEventsApi()
            }
        }
        Runtime.getRuntime().addShutdownHook(Thread {
            app.stop(5, 60, TimeUnit.SECONDS)
        })
        return app
    }
}
