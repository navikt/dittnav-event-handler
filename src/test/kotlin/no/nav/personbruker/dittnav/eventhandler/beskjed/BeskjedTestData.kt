package no.nav.personbruker.dittnav.eventhandler.beskjed

import Beskjed
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.DoknotifikasjonStatusDto
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.EksternVarslingInfo
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.EksternVarslingInfoObjectMother
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.EksternVarslingInfoObjectMother.createEskternVarslingInfo
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.EksternVarslingStatus
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

object BeskjedObjectMother {
    private var idIncrementor = 0
    private var eventIdIncrementor = 0

    private const val defaultFodselsnummer = "123456789"
    private const val defaultSikkerhetsnivaa = 4
    private const val defaultAktiv = true
    private const val defaultSystembruker = "x-dittnav"
    private const val defaultNamespace = "min-side"
    private const val defaultAppnavn = "test-app"
    private const val defaultGrupperingsId = "100$defaultFodselsnummer"
    private const val defaultTekst = "Dette er beskjed til brukeren"
    private const val defaultLink = "https://nav.no/systemX/$defaultFodselsnummer"
    private const val defaultProdusent = "$defaultSystembruker-produsent"
    private val defaultSynligFremTil = ZonedDateTime.now(ZoneId.of("Europe/Oslo")).plusDays(7)
    private val defaultEventTidspunkt = ZonedDateTime.now(ZoneId.of("Europe/Oslo"))
    private val defaultForstBehandlet = ZonedDateTime.now(ZoneId.of("Europe/Oslo"))
    private val defaultSistOppdatert = ZonedDateTime.now(ZoneId.of("Europe/Oslo"))
    private val defaultEksternVarslinginfo = createEskternVarslingInfo()

    fun createBeskjed(
        id: Int = ++idIncrementor,
        eventId: String = (++eventIdIncrementor).toString(),
        fodselsnummer: String = defaultFodselsnummer,
        synligFremTil: ZonedDateTime? = defaultSynligFremTil,
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
    ): Beskjed {
        return Beskjed(
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
            synligFremTil = synligFremTil,
            sikkerhetsnivaa = sikkerhetsnivaa,
            aktiv = aktiv,
            eksternVarslingInfo = eksternVarslingInfo
        )
    }
}

object BeskjedTestData {

    internal const val beskjedTestFnr = "12345678910"
    internal const val eventId = "124"
    internal const val grupperingsid = "100$beskjedTestFnr"
    internal const val systembruker = "x-dittnav"
    internal const val namespace = "localhost"
    internal const val appnavn = "dittnav"

    internal val beskjed1Aktiv = BeskjedObjectMother.createBeskjed(
        id = 1,
        eventId = "123",
        fodselsnummer = beskjedTestFnr,
        grupperingsId = grupperingsid,
        synligFremTil = ZonedDateTime.now().plusHours(1).truncatedTo(ChronoUnit.MINUTES),
        forstBehandlet = ZonedDateTime.now().truncatedTo(ChronoUnit.MINUTES),
        aktiv = true,
        systembruker = systembruker,
        namespace = namespace,
        appnavn = appnavn,
        eksternVarslingInfo = createEskternVarslingInfo(
            bestilt = true,
            prefererteKanaler = listOf("SMS", "EPOST")
        )
    )

    val doknotStatusForBeskjed1 = DoknotifikasjonStatusDto(
        eventId = beskjed1Aktiv.eventId,
        status = EksternVarslingStatus.OVERSENDT.name,
        melding = "melding",
        distribusjonsId = 123L,
        kanaler = "SMS"
    )

    internal val beskjed2Aktiv = BeskjedObjectMother.createBeskjed(
        id = 2,
        eventId = eventId,
        fodselsnummer = beskjedTestFnr,
        grupperingsId = grupperingsid,
        synligFremTil = ZonedDateTime.now().plusHours(1).truncatedTo(ChronoUnit.MINUTES),
        forstBehandlet = ZonedDateTime.now().minusDays(5).truncatedTo(ChronoUnit.MINUTES),
        aktiv = true,
        systembruker = systembruker,
        namespace = namespace,
        appnavn = appnavn,
        eksternVarslingInfo = EksternVarslingInfoObjectMother.createEskternVarslingInfo(
            bestilt = true,
            prefererteKanaler = listOf("SMS", "EPOST")
        )
    )

    val doknotStatusForBeskjed2 = DoknotifikasjonStatusDto(
        eventId = beskjed2Aktiv.eventId,
        status = EksternVarslingStatus.FEILET.name,
        melding = "feilet",
        distribusjonsId = null,
        kanaler = ""
    )

    internal val beskjed3Inaktiv = BeskjedObjectMother.createBeskjed(
        id = 3,
        eventId = "567",
        fodselsnummer = beskjedTestFnr,
        grupperingsId = grupperingsid,
        synligFremTil = ZonedDateTime.now().plusHours(1).truncatedTo(ChronoUnit.MINUTES),
        forstBehandlet = ZonedDateTime.now().minusDays(15).truncatedTo(ChronoUnit.MINUTES),
        aktiv = false,
        systembruker = systembruker,
        namespace = namespace,
        appnavn = appnavn
    )
    internal val beskjed4Aktiv = BeskjedObjectMother.createBeskjed(
        id = 4,
        eventId = "789",
        fodselsnummer = "54321",
        synligFremTil = ZonedDateTime.now().plusHours(1).truncatedTo(ChronoUnit.MINUTES),
        forstBehandlet = ZonedDateTime.now().minusDays(25).truncatedTo(ChronoUnit.MINUTES),
        aktiv = true,
        systembruker = "x-dittnav-2",
        namespace = namespace,
        appnavn = "dittnav-2"
    )
}