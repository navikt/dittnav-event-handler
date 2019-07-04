package no.nav.personbruker.dittnav.eventhandler

import no.nav.personbruker.dittnav.eventhandler.config.*

suspend fun main(args: Array<String>) {
    val environment = Environment()

    Server.start()

    if(!ConfigUtil.isCurrentlyRunningOnNais()) {
        Flyway.runFlywayMigrations(environment)
    }

    DatabaseConnectionFactory.initDatabase(environment)
}