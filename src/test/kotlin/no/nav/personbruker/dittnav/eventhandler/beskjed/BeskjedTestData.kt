package no.nav.personbruker.dittnav.eventhandler.beskjed

import no.nav.personbruker.dittnav.eventhandler.OsloDateTime
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.DoknotifikasjonTestStatus
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.EksternVarslingStatus
import java.time.ZonedDateTime

object BeskjedObjectMother {
    private var idIncrementor = 0
    private var eventIdIncrementor = 0

    private const val defaultFodselsnummer = "123456789"
    private const val defaultSystembruker = "x-dittnav"

    fun createBeskjed(
        id: Int = ++idIncrementor,
        eventId: String = (++eventIdIncrementor).toString(),
        fodselsnummer: String = defaultFodselsnummer,
        synligFremTil: ZonedDateTime? = OsloDateTime.now().plusDays(7),
        aktiv: Boolean = true,
        systembruker: String = defaultSystembruker,
        namespace: String = "min-side",
        appnavn: String = "test-app",
        produsent: String = "$defaultSystembruker-produsent",
        eventTidspunkt: ZonedDateTime = OsloDateTime.now(),
        forstBehandlet: ZonedDateTime = OsloDateTime.now(),
        grupperingsId: String = "100$defaultFodselsnummer",
        tekst: String = "Dette er beskjed til brukeren",
        link: String = "https://nav.no/systemX/$defaultFodselsnummer",
        sistOppdatert: ZonedDateTime = OsloDateTime.now(),
        sikkerhetsnivaa: Int = 4,
        eksternVarslingSendt: Boolean = false,
        eksternVarslingKanaler: List<String> = listOf(),
        fristUtløpt: Boolean? = null
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
            eksternVarslingSendt = eksternVarslingSendt,
            eksternVarslingKanaler = eksternVarslingKanaler,
            fristUtløpt = fristUtløpt
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
        synligFremTil = OsloDateTime.now().plusHours(1),
        aktiv = true,
        systembruker = systembruker,
        namespace = namespace,
        appnavn = appnavn,
        forstBehandlet = OsloDateTime.now(),
        grupperingsId = grupperingsid,
        eksternVarslingSendt = true,
        eksternVarslingKanaler = listOf("SMS","EPOST"),

        )

    val doknotStatusForBeskjed1 = DoknotifikasjonTestStatus(
        eventId = beskjed1Aktiv.eventId,
        status = EksternVarslingStatus.OVERSENDT.name,
        melding = "melding",
        distribusjonsId = 123L,
        kanaler = "SMS,EPOST"
    )

    internal val beskjed2Aktiv = BeskjedObjectMother.createBeskjed(
        id = 2,
        eventId = eventId,
        fodselsnummer = beskjedTestFnr,
        synligFremTil = OsloDateTime.now().plusHours(1),
        aktiv = true,
        systembruker = systembruker,
        namespace = namespace,
        appnavn = appnavn,
        forstBehandlet = OsloDateTime.now().minusDays(5),
        grupperingsId = grupperingsid,
        eksternVarslingSendt = false,
        eksternVarslingKanaler = emptyList(),
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
        synligFremTil = OsloDateTime.now().plusHours(1),
        aktiv = false,
        systembruker = systembruker,
        namespace = namespace,
        appnavn = appnavn,
        forstBehandlet = OsloDateTime.now().minusDays(15),
        grupperingsId = grupperingsid,
    )
    internal val beskjed4Aktiv = BeskjedObjectMother.createBeskjed(
        id = 4,
        eventId = "789",
        fodselsnummer = "54321",
        synligFremTil = OsloDateTime.now().plusHours(1),
        aktiv = true,
        systembruker = "x-dittnav-2",
        namespace = namespace,
        appnavn = "dittnav-2",
        forstBehandlet = OsloDateTime.now().minusDays(25),
    )
}