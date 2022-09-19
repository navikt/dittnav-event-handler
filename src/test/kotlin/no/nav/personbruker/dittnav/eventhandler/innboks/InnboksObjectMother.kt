package no.nav.personbruker.dittnav.eventhandler.innboks

import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.DoknotifikasjonTestStatus
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.EksternVarslingInfo
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.EksternVarslingInfoObjectMother
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.EksternVarslingStatus
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

object InnboksObjectMother {

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

    fun createInnboks(
        id: Int = ++idIncrementor,
        eventId: String = (++eventIdIncrementor).toString(),
        fodselsnummer: String = defaultFodselsnummer,
        aktiv: Boolean = true,
        systembruker: String = defaultSystembruker,
        namespace: String = "min-side",
        appnavn: String = "test-app",
        produsent: String = "$defaultSystembruker-produsent",
        eventTidspunkt: ZonedDateTime = ZonedDateTime.now(ZoneId.of("Europe/Oslo")),
        forstBehandlet: ZonedDateTime = ZonedDateTime.now(ZoneId.of("Europe/Oslo")),
        grupperingsId: String = "100$defaultFodselsnummer",
        tekst: String = "Dette er innboks melding til brukeren",
        link: String = "https://nav.no/systemX/$defaultFodselsnummer",
        sistOppdatert: ZonedDateTime = ZonedDateTime.now(ZoneId.of("Europe/Oslo")),
        sikkerhetsnivaa: Int = 4,
        eksternVarslingInfo: EksternVarslingInfo = defaultEksternVarslinginfo
    ): Innboks {
        return Innboks(
            id = id,
            produsent = produsent,
            systembruker = systembruker,
            namespace = namespace,
            appnavn = appnavn,
            eventTidspunkt = eventTidspunkt,
            forstBehandlet = forstBehandlet,
            fodselsnummer = fodselsnummer,
            eventId = eventId,
            grupperingsId = grupperingsId,
            tekst = tekst,
            link = link,
            sistOppdatert = sistOppdatert,
            sikkerhetsnivaa = sikkerhetsnivaa,
            aktiv = aktiv,
            eksternVarslingInfo = eksternVarslingInfo
        )
    }
}

internal const val innboksTestFnr1 = "12345987659"
internal const val innboksTestFnr2 = "67890987659"
internal const val innboksTestSystembruker = "x-dittnav"
internal const val innboksTestnamespace = "localhost"
internal const val innboksTestAppnavn = "dittnav"
internal const val innboksTestgrupperingsid = "100$innboksTestFnr1"


internal val innboks1Aktiv = InnboksObjectMother.createInnboks(
    id = 1,
    eventId = "123",
    fodselsnummer = innboksTestFnr1,
    grupperingsId = innboksTestgrupperingsid,
    aktiv = true,
    systembruker = innboksTestSystembruker,
    namespace = innboksTestnamespace,
    appnavn = innboksTestAppnavn,
    eksternVarslingInfo = EksternVarslingInfoObjectMother.createEskternVarslingInfo(
        bestilt = true,
        prefererteKanaler = listOf("SMS", "EPOST")
    )
)

internal val doknotStatusForInnboks1 = DoknotifikasjonTestStatus(
    eventId = innboks1Aktiv.eventId,
    status = EksternVarslingStatus.OVERSENDT.name,
    melding = "melding",
    distribusjonsId = 123L,
    kanaler = "SMS"
)

internal val innboks2Aktiv = InnboksObjectMother.createInnboks(
    id = 2,
    eventId = "345",
    fodselsnummer = innboksTestFnr1,
    grupperingsId = innboksTestgrupperingsid,
    aktiv = true,
    systembruker = innboksTestSystembruker,
    namespace = innboksTestnamespace,
    appnavn = innboksTestAppnavn,
    eksternVarslingInfo = EksternVarslingInfoObjectMother.createEskternVarslingInfo(
        bestilt = true,
        prefererteKanaler = listOf("SMS", "EPOST")
    )
)

internal val doknotStatusForInnboks2 = DoknotifikasjonTestStatus(
    eventId = innboks2Aktiv.eventId,
    status = EksternVarslingStatus.FEILET.name,
    melding = "feilet",
    distribusjonsId = null,
    kanaler = ""
)

internal val innboks3Aktiv = InnboksObjectMother.createInnboks(
    id = 3,
    eventId = "567",
    fodselsnummer = innboksTestFnr2,
    aktiv = true,
    systembruker = "x-dittnav-2",
    namespace = innboksTestnamespace,
    appnavn = "dittnav-2",
    forstBehandlet = ZonedDateTime.now().minusDays(5),
)
internal val innboks4Inaktiv = InnboksObjectMother.createInnboks(
    id = 4,
    eventId = "789",
    fodselsnummer = innboksTestFnr2,
    aktiv = false,
    systembruker = innboksTestSystembruker,
    namespace = innboksTestnamespace,
    appnavn = innboksTestAppnavn,
    forstBehandlet = ZonedDateTime.now().minusDays(15),
)

