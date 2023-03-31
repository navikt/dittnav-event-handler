package no.nav.personbruker.dittnav.eventhandler.innboks

import no.nav.personbruker.dittnav.eventhandler.OsloDateTime
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.EksternVarslingHistorikkEntry
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.EksternVarsling
import java.time.ZonedDateTime

object InnboksObjectMother {

    private var eventIdIncrementor = 0

    private const val defaultFodselsnummer = "123456789"
    private const val defaultSystembruker = "x-dittnav"

    fun createInnboks(
        eventId: String = (++eventIdIncrementor).toString(),
        fodselsnummer: String = defaultFodselsnummer,
        aktiv: Boolean = true,
        systembruker: String = defaultSystembruker,
        namespace: String = "min-side",
        appnavn: String = "test-app",
        produsent: String = "$defaultSystembruker-produsent",
        eventTidspunkt: ZonedDateTime = OsloDateTime.now(),
        forstBehandlet: ZonedDateTime = OsloDateTime.now(),
        grupperingsId: String = "100$defaultFodselsnummer",
        tekst: String = "Dette er innboks melding til brukeren",
        link: String = "https://nav.no/systemX/$defaultFodselsnummer",
        sistOppdatert: ZonedDateTime = OsloDateTime.now(),
        sikkerhetsnivaa: Int = 4,
        eksternVarsling: EksternVarsling? = null
    ): Innboks {
        return Innboks(
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
            eksternVarslingSendt = eksternVarsling?.sendt ?: false,
            eksternVarslingKanaler = eksternVarsling?.sendteKanaler ?: emptyList(),
            eksternVarsling = eksternVarsling
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
    eventId = "123",
    fodselsnummer = innboksTestFnr1,
    grupperingsId = innboksTestgrupperingsid,
    aktiv = true,
    systembruker = innboksTestSystembruker,
    namespace = innboksTestnamespace,
    appnavn = innboksTestAppnavn,
    eksternVarsling = eksternVarslingForInnboks1
)

val eksternVarslingForInnboks1 get() = EksternVarsling(
    sendt = true,
    renotifikasjonSendt = false,
    prefererteKanaler = listOf("SMS","EPOST"),
    sendteKanaler = listOf("SMS","EPOST"),
    historikk = listOf(
        EksternVarslingHistorikkEntry(
            status = "bestilt",
            melding = "Notifikasjon er behandlet og distribusjon er bestilt",
            tidspunkt = OsloDateTime.now()
        ),
        EksternVarslingHistorikkEntry(
            status =  "sendt",
            melding =  "notifikajon sendt via sms",
            distribusjonsId =  123,
            kanal =  "SMS",
            renotifikasjon =  false,
            tidspunkt =  OsloDateTime.now()
        ),
        EksternVarslingHistorikkEntry(
            status =  "sendt",
            melding =  "notifikajon sendt via epost",
            distribusjonsId =  123,
            kanal =  "EPOST",
            renotifikasjon =  false,
            tidspunkt =  OsloDateTime.now()
        )
    )
)

internal val innboks2Aktiv = InnboksObjectMother.createInnboks(
    eventId = "345",
    fodselsnummer = innboksTestFnr1,
    grupperingsId = innboksTestgrupperingsid,
    aktiv = true,
    systembruker = innboksTestSystembruker,
    namespace = innboksTestnamespace,
    appnavn = innboksTestAppnavn,
    eksternVarsling = eksternVarslingForInnboks2
)

val eksternVarslingForInnboks2 get() = EksternVarsling(
    sendt = false,
    renotifikasjonSendt = false,
    prefererteKanaler = listOf("SMS","EPOST"),
    sendteKanaler = emptyList(),
    historikk = listOf(
        EksternVarslingHistorikkEntry(
            status = "bestilt",
            melding = "Notifikasjon er behandlet og distribusjon er bestilt",
            tidspunkt = OsloDateTime.now()
        ),
        EksternVarslingHistorikkEntry(
            status =  "feilet",
            melding =  "Notifikasjon feiler",
            tidspunkt =  OsloDateTime.now()
        )
    )
)

internal val innboks3Aktiv = InnboksObjectMother.createInnboks(
    eventId = "567",
    fodselsnummer = innboksTestFnr2,
    aktiv = true,
    systembruker = "x-dittnav-2",
    namespace = innboksTestnamespace,
    appnavn = "dittnav-2",
    forstBehandlet = OsloDateTime.now().minusDays(5),
)
internal val innboks4Inaktiv = InnboksObjectMother.createInnboks(
    eventId = "789",
    fodselsnummer = innboksTestFnr2,
    aktiv = false,
    systembruker = innboksTestSystembruker,
    namespace = innboksTestnamespace,
    appnavn = innboksTestAppnavn,
    forstBehandlet = OsloDateTime.now().minusDays(15),
)

