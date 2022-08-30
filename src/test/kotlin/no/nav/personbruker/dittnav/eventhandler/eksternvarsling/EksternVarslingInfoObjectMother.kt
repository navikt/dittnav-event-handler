package no.nav.personbruker.dittnav.eventhandler.eksternvarsling

object EksternVarslingInfoObjectMother {

    private val defaultBestilt = false
    private val defaultPrefererteKanaler = listOf<String>()
    private val defaultSendt = true
    private val defaultSendteKanaler = listOf<String>()

    fun createEskternVarslingInfo(
        bestilt: Boolean = defaultBestilt,
        prefererteKanaler: List<String> = defaultPrefererteKanaler,
        sendt: Boolean = defaultSendt,
        sendteKanaler: List<String> = defaultSendteKanaler
    ) = EksternVarslingInfo(
        bestilt = bestilt,
        prefererteKanaler = prefererteKanaler,
        sendt = sendt,
        sendteKanaler = sendteKanaler
    )
}
