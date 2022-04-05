package no.nav.personbruker.dittnav.eventhandler.common.health

import no.nav.personbruker.dittnav.eventhandler.common.database.Database

class HealthService (private val database: Database) {

    suspend fun getHealthChecks(): List<HealthStatus> {
        return listOf(
                database.status()
        )
    }
}