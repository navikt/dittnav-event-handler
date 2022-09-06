package no.nav.personbruker.dittnav.eventhandler.common.health

interface HealthCheck {

    suspend fun status(): HealthStatus
}
