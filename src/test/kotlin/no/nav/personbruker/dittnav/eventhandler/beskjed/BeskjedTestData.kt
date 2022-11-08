package no.nav.personbruker.dittnav.eventhandler.beskjed

import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.DoknotifikasjonTestStatus
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.EksternVarslingStatus

internal fun Beskjed.medEksternVarsling(
    sendt: Boolean
): Pair<Beskjed, DoknotifikasjonTestStatus> = Pair(
    copy(eksternVarslingSendt = sendt),
    DoknotifikasjonTestStatus(
        eventId = eventId,
        status = sendt.resolveVarslingStatus(),
        melding = sendt.resolveMelding(),
        distribusjonsId = if (sendt) 123L else null,
        kanaler = eksternVarslingKanaler.resolveVarslingKanaler(sendt)
    )
)

internal val Pair<Beskjed, DoknotifikasjonTestStatus>.beskjed: Beskjed
    get() = first

internal val Pair<Beskjed, DoknotifikasjonTestStatus>.doknotStatus: DoknotifikasjonTestStatus
    get() = second


private fun Boolean.resolveMelding(): String = if (this) {
    EksternVarslingStatus.OVERSENDT.name
} else {
    "feilet"
}

private fun Boolean.resolveVarslingStatus(): String =
    if (this) {
        EksternVarslingStatus.OVERSENDT.name
    } else {
        EksternVarslingStatus.FEILET.name
    }

private fun List<String>.resolveVarslingKanaler(sendt: Boolean) = if (sendt) {
    this.joinToString(",")
} else {
    ""
}