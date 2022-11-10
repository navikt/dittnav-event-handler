package no.nav.personbruker.dittnav.eventhandler.beskjed

import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.DoknotifikasjonTestStatus
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.EksternVarslingStatus.*

internal fun Beskjed.medEksternVarsling(
    oversendt: Boolean
): Pair<Beskjed, DoknotifikasjonTestStatus> = Pair(
    copy(eksternVarslingSendt = oversendt),
    DoknotifikasjonTestStatus(
        eventId = eventId,
        status = oversendt.resolveVarslingStatus(),
        melding = oversendt.resolveMelding(),
        distribusjonsId = if (oversendt) 123L else null,
        kanaler = eksternVarslingKanaler.resolveVarslingKanaler(oversendt)
    )
)

internal val Pair<Beskjed, DoknotifikasjonTestStatus>.beskjed
    get() = first
internal val Pair<Beskjed, DoknotifikasjonTestStatus>.doknotStatus
    get() = second
private fun Boolean.resolveMelding(): String = if (this) OVERSENDT.name else "feilet"
private fun Boolean.resolveVarslingStatus(): String = if (this) OVERSENDT.name else FEILET.name
private fun List<String>.resolveVarslingKanaler(sendt: Boolean) = if (sendt) this.joinToString(",") else ""