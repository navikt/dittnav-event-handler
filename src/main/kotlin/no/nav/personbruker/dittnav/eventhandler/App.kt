package no.nav.personbruker.dittnav.eventhandler

import no.nav.personbruker.dittnav.eventhandler.config.ConfigUtil
import no.nav.personbruker.dittnav.eventhandler.config.Environment
import no.nav.personbruker.dittnav.eventhandler.config.Flyway
import no.nav.personbruker.dittnav.eventhandler.config.Server

fun main() {
    val environment = Environment()

    Server.configure(environment).start()

    if (!ConfigUtil.isCurrentlyRunningOnNais()) {
        Flyway.runFlywayMigrations(environment)
    }
}
