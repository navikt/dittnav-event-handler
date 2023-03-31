package no.nav.personbruker.dittnav.eventhandler.eksternvarsling

object EksternVarslingObjectMother {

    fun createEksternVarsling(
        prefererteKanaler: List<String> = emptyList(),
        sendt: Boolean = true,
        renotifikasjon: Boolean = false,
        sendteKanaler: List<String> = emptyList(),
        historikk: List<EksternVarslingHistorikkEntry> = emptyList()
    ) = EksternVarsling(
        prefererteKanaler = prefererteKanaler,
        sendt = sendt,
        sendteKanaler = sendteKanaler,
        renotifikasjonSendt = renotifikasjon,
        historikk = historikk
    )
}
