package no.nav.personbruker.dittnav.eventhandler.innboks

import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.DoknotifikasjonTestStatus
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.EksternVarslingInfo
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.EksternVarslingInfoObjectMother
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.EksternVarslingStatus
import java.time.ZoneId
import java.time.ZonedDateTime

object InnboksTestData {

    private var idIncrementor = 0
    private var eventIdIncrementor = 0

    private const val defaultFodselsnummer = "123456789"
    private const val defaultAktiv = true
    private const val defaultSystembruker = "x-dittnav"
    private const val defaultNamespace = "min-side"
    private const val defaultAppnavn = "test-app"
    private const val defaultProdusent = "$defaultSystembruker-produsent"
    private const val defaultSikkerhetsnivaa = 4
    private const val defaultGrupperingsId = "100$defaultFodselsnummer"
    private const val defaultTekst = "Dette er innboks melding til brukeren"
    private const val defaultLink = "https://nav.no/systemX/$defaultFodselsnummer"
    private val defaultEventTidspunkt = ZonedDateTime.now(ZoneId.of("Europe/Oslo"))
    private val defaultForstBehandlet = ZonedDateTime.now(ZoneId.of("Europe/Oslo"))
    private val defaultSistOppdatert = ZonedDateTime.now(ZoneId.of("Europe/Oslo"))
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
        aktiv: Boolean = defaultAktiv,
        systembruker: String = defaultSystembruker,
        namespace: String = defaultNamespace,
        appnavn: String = defaultAppnavn,
        produsent: String = defaultProdusent,
        eventTidspunkt: ZonedDateTime = defaultEventTidspunkt,
        forstBehandlet: ZonedDateTime = defaultForstBehandlet,
        grupperingsId: String = defaultGrupperingsId,
        tekst: String = defaultTekst,
        link: String = defaultLink,
        sistOppdatert: ZonedDateTime = defaultSistOppdatert,
        sikkerhetsnivaa: Int = defaultSikkerhetsnivaa,
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

internal const val innboksTestFnr1 = "12345"
internal const val innboksTestFnr2 = "67890"
internal const val innboksTestSystembruker = "x-dittnav"
internal const val innboksTestnamespace = "localhost"
internal const val innboksTestAppnavn = "dittnav"
internal const val innboksTestgrupperingsid = "100$innboksTestFnr1"

internal val innboks1Aktiv = InnboksTestData.createInnboks(
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

internal val innboks2Aktiv = InnboksTestData.createInnboks(
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

internal val innboks3Aktiv = InnboksTestData.createInnboks(
    id = 3,
    eventId = "567",
    fodselsnummer = innboksTestFnr2,
    aktiv = true,
    systembruker = "x-dittnav-2",
    namespace = innboksTestnamespace,
    appnavn = "dittnav-2",
    forstBehandlet = ZonedDateTime.now().minusDays(5),
)
internal val innboks4Inaktiv = InnboksTestData.createInnboks(
    id = 4,
    eventId = "789",
    fodselsnummer = innboksTestFnr2,
    aktiv = false,
    systembruker = innboksTestSystembruker,
    namespace = innboksTestnamespace,
    appnavn = innboksTestAppnavn,
    forstBehandlet = ZonedDateTime.now().minusDays(15),
)