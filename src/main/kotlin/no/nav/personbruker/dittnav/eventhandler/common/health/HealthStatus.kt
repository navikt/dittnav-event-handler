package no.nav.personbruker.dittnav.eventhandler.common.health

data class HealthStatus(val serviceName: String, val status: Status, val statusMessage: String)

enum class Status {
    OK, ERROR
}

