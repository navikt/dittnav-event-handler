package no.nav.personbruker.dittnav.eventhandler.eksternvarsling

import java.lang.IllegalArgumentException

enum class EksternVarslingStatus {
    FEILET, INFO, OVERSENDT, FERDIGSTILT;

    companion object {
        private val map = EksternVarslingStatus.values().associateBy { it.name }
        operator fun get(value: String?) = map[value]
    }
}
