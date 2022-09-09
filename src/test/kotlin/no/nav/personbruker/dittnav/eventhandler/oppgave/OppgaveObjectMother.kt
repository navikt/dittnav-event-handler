package no.nav.personbruker.dittnav.eventhandler.oppgave

import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.EksternVarslingInfo
import java.time.ZoneId
import java.time.ZonedDateTime

object OppgaveObjectMother {

    private var idIncrementor = 0
    private var eventIdIncrementor = 0

    private const val defaultFodselsnummer = "123456789"
    private const val defaultSystembruker = "x-dittnav"

    private val defaultEksternVarslinginfo = EksternVarslingInfo(
        bestilt = false,
        prefererteKanaler = emptyList(),
        sendt = false,
        sendteKanaler = emptyList()
    )

    fun createOppgave(
        id: Int = ++idIncrementor,
        eventId: String = (++eventIdIncrementor).toString(),
        fodselsnummer: String = defaultFodselsnummer,
        aktiv: Boolean = true,
        systembruker: String = defaultSystembruker,
        namespace: String = "min-side",
        appnavn: String = "test-app",
        grupperingsId: String = "100$defaultFodselsnummer",
        eventTidspunkt: ZonedDateTime = ZonedDateTime.now(ZoneId.of("Europe/Oslo")),
        forstBehandlet: ZonedDateTime = ZonedDateTime.now(ZoneId.of("Europe/Oslo")),
        produsent: String = "$defaultSystembruker-produsent",
        sistOppdatert: ZonedDateTime = ZonedDateTime.now(ZoneId.of("Europe/Oslo")),
        tekst: String = "Dette er oppgave til brukeren",
        link: String = "https://nav.no/systemX/$defaultFodselsnummer",
        sikkerhetsnivaa: Int = 4,
        eksternVarslingInfo: EksternVarslingInfo = defaultEksternVarslinginfo
    ): Oppgave {
        return Oppgave(
            id = id,
            fodselsnummer = fodselsnummer,
            grupperingsId = grupperingsId,
            eventId = eventId,
            eventTidspunkt = eventTidspunkt,
            forstBehandlet = forstBehandlet,
            produsent = produsent,
            systembruker = systembruker,
            namespace = namespace,
            appnavn = appnavn,
            sikkerhetsnivaa = sikkerhetsnivaa,
            sistOppdatert = sistOppdatert,
            tekst = tekst,
            link = link,
            aktiv = aktiv,
            eksternVarslingInfo = eksternVarslingInfo
        )
    }
}
