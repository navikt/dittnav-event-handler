package no.nav.personbruker.dittnav.eventhandler

import no.nav.personbruker.dittnav.eventhandler.config.*

fun main() {
    val environment = Environment()

    Server.configure().start()

    if (!ConfigUtil.isCurrentlyRunningOnNais()) {
        Flyway.runFlywayMigrations(environment)
    }

    DatabaseConnectionFactory.initDatabase(environment)
}
