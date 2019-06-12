package no.nav.personbruker.dittnav.eventhandler

fun main(args: Array<String>){
	Server.startServer(System.getenv("PORT")?.toInt() ?: 8080).start(wait = true)
}
