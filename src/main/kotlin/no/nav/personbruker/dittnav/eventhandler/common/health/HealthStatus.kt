package no.nav.personbruker.dittnav.eventhandler.common.health

data class HealthStatus(val serviceName: String,
                        val status: Status,
                        val statusMessage: String,
                        val includeInReadiness: Boolean = false)

enum class Status {
    OK, ERROR
}
