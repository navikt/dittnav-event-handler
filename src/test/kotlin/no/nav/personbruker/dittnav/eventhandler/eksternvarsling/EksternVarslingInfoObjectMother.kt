package no.nav.personbruker.dittnav.eventhandler.eksternvarsling

object EksternVarslingInfoObjectMother {

    fun createEskternVarslingInfo(
        prefererteKanaler: List<String> = emptyList(),
        sendt: Boolean = true,
        renotifikasjon: Boolean = false,
        sendteKanaler: List<String> = emptyList(),
        historikk: List<EksternVarslingHistorikkEntry> = emptyList()
    ) = EksternVarslingInfo(
        prefererteKanaler = prefererteKanaler,
        sendt = sendt,
        sendteKanaler = sendteKanaler,
        renotifikasjonSendt = renotifikasjon,
        historikk = historikk
    )
}
