package no.nav.personbruker.dittnav.eventhandler.beskjed

import no.nav.personbruker.dittnav.eventhandler.OsloDateTime
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.EksternVarslingHistorikkEntry
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.EksternVarsling
import java.time.ZonedDateTime
import java.util.*

object BeskjedObjectMother {
    private const val defaultFodselsnummer = "123456789"
    private const val defaultSystembruker = "x-dittnav"

    fun createBeskjed(
        eventId: String = UUID.randomUUID().toString(),
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
        eksternVarsling: EksternVarsling? = null,
        fristUtløpt: Boolean? = null
    ): Beskjed {
        return Beskjed(
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
            eksternVarslingSendt = eksternVarsling?.sendt ?: false,
            eksternVarslingKanaler = eksternVarsling?.sendteKanaler ?: emptyList(),
            eksternVarsling = eksternVarsling,
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
        eventId = "123",
        fodselsnummer = beskjedTestFnr,
        synligFremTil = OsloDateTime.now().plusHours(1),
        aktiv = true,
        systembruker = systembruker,
        namespace = namespace,
        appnavn = appnavn,
        forstBehandlet = OsloDateTime.now(),
        grupperingsId = grupperingsid,
        eksternVarsling = eksternVarslingForBeskjed1
    )

    val eksternVarslingForBeskjed1 get() = EksternVarsling(
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

    internal val beskjed2Aktiv = BeskjedObjectMother.createBeskjed(
        eventId = eventId,
        fodselsnummer = beskjedTestFnr,
        synligFremTil = OsloDateTime.now().plusHours(1),
        aktiv = true,
        systembruker = systembruker,
        namespace = namespace,
        appnavn = appnavn,
        forstBehandlet = OsloDateTime.now().minusDays(5),
        grupperingsId = grupperingsid,
        eksternVarsling = eksternVarslingForBeskjed2
    )

    val eksternVarslingForBeskjed2 get() = EksternVarsling(
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
                melding =  "Notifikasjon feilet",
                tidspunkt =  OsloDateTime.now()
            )
        )
    )
    internal val beskjed3Inaktiv = BeskjedObjectMother.createBeskjed(
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
