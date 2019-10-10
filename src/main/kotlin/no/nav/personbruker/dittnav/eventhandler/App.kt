package no.nav.personbruker.dittnav.eventhandler

import no.nav.personbruker.dittnav.eventhandler.config.ApplicationContext
import no.nav.personbruker.dittnav.eventhandler.config.ConfigUtil
import no.nav.personbruker.dittnav.eventhandler.config.Flyway
import no.nav.personbruker.dittnav.eventhandler.config.Server

fun main() {
    val applicationContext = ApplicationContext()

    Server.configure(applicationContext).start()

    if (!ConfigUtil.isCurrentlyRunningOnNais()) {
        Flyway.runFlywayMigrations(applicationContext.environment)
    }
}
