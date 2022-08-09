package no.nav.personbruker.dittnav.eventhandler.eksternvarsling

data class EksternVarslingInfo(
    val bestilt: Boolean,
    val sendt: Boolean,
    val prefererteKanaler: List<String>,
    val sendteKanaler: List<String>
)
