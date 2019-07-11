package no.nav.personbruker.dittnav.eventhandler.config

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.authenticate
import io.ktor.auth.jwt.jwt
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
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

object Server {

    const val portNumber = 8090

    val log = LoggerFactory.getLogger(Server::class.java)

    fun configure(environment: Environment): NettyApplicationEngine {
        DefaultExports.initialize()
        val app = embeddedServer(Netty, port = portNumber) {
            install(DefaultHeaders)

            install(ContentNegotiation) {
                jackson {
                    enable(SerializationFeature.INDENT_OUTPUT)
                    registerModule(JavaTimeModule())
                }
            }

            install(Authentication) {
                jwt {
                    setupOidcAuthentication(environment)
                }
            }

            routing {
                healthApi()
                regelApi()
                authenticate {
                    fetchEventsApi()
                    produceEventsApi()
                }
            }
        }
        addGraceTimeAtShutdownToAllowRunningRequestsToComplete(app)
        return app
    }

    private fun addGraceTimeAtShutdownToAllowRunningRequestsToComplete(app: NettyApplicationEngine) {
        Runtime.getRuntime().addShutdownHook(Thread {
            app.stop(5, 60, TimeUnit.SECONDS)
        })
    }

}
