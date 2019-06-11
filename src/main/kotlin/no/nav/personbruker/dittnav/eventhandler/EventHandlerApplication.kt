package no.nav.personbruker.dittnav.eventhandler

import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.DefaultHeaders
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

class EventHandlerApplication

fun main(args: Array<String>){
	val jsonResponse = """{
    "ping": "pong"
}"""

	embeddedServer(Netty, 8080) {
		install(DefaultHeaders) {
			header("X-Developer", "Team Personbruker")
			header(HttpHeaders.Server, "My Server")
		}
		install(Routing) {
			get("/ping") {
				call.respondText(jsonResponse, ContentType.Application.Json)
			}
		}
	}.start(wait = true)
}
