package no.nav.personbruker.dittnav.eventhandler.common.health

import no.nav.personbruker.dittnav.eventhandler.config.ApplicationContext

class HealthService (private val applicationContext: ApplicationContext) {

    suspend fun getHealthChecks(): List<HealthStatus> {
        return listOf(
                applicationContext.database.status()
        )
    }
}