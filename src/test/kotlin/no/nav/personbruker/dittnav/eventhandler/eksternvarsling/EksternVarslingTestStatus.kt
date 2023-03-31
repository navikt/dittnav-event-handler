package no.nav.personbruker.dittnav.eventhandler.eksternvarsling

data class EksternVarslingTestStatus(
    val eventId: String,
    val kanaler: String,
    val sendt: Boolean,
    val renotifikasjonSendt: Boolean,
    val historikkJson: String
)
