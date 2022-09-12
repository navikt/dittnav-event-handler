package no.nav.personbruker.dittnav.eventhandler.beskjed

import Beskjed
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.DoknotifikasjonTestStatus
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
    private const val defaultSystembruker = "x-dittnav"

    fun createBeskjed(
        id: Int = ++idIncrementor,
        eventId: String = (++eventIdIncrementor).toString(),
        fodselsnummer: String = defaultFodselsnummer,
        synligFremTil: ZonedDateTime? =  ZonedDateTime.now(ZoneId.of("Europe/Oslo")).plusDays(7),
        aktiv: Boolean = true,
        systembruker: String = defaultSystembruker,
        namespace: String = "min-side",
        appnavn: String = "test-app",
        produsent: String = "$defaultSystembruker-produsent",
        eventTidspunkt: ZonedDateTime = ZonedDateTime.now(ZoneId.of("Europe/Oslo")),
        forstBehandlet: ZonedDateTime = ZonedDateTime.now(ZoneId.of("Europe/Oslo")),
        grupperingsId: String = "100$defaultFodselsnummer",
        tekst: String = "Dette er beskjed til brukeren",
        link: String = "https://nav.no/systemX/$defaultFodselsnummer",
        sistOppdatert: ZonedDateTime = ZonedDateTime.now(ZoneId.of("Europe/Oslo")),
        sikkerhetsnivaa: Int = 4,
        eksternVarslingInfo: EksternVarslingInfo = createEskternVarslingInfo()
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

    val doknotStatusForBeskjed1 = DoknotifikasjonTestStatus(
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

    val doknotStatusForBeskjed2 = DoknotifikasjonTestStatus(
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