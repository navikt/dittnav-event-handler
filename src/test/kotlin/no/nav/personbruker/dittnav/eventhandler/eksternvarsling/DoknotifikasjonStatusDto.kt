package no.nav.personbruker.dittnav.eventhandler.eksternvarsling

data class DoknotifikasjonStatusDto(
    val eventId: String,
    val status: String,
    val melding: String,
    val distribusjonsId: Long?,
    val kanaler: String
)
