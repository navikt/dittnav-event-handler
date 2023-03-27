package no.nav.personbruker.dittnav.eventhandler.oppgave

import no.nav.personbruker.dittnav.eventhandler.OsloDateTime
import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedTestData
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.*
import java.time.ZonedDateTime

object OppgaveObjectMother {
    private var eventIdIncrementor = 0

    private const val defaultFodselsnummer = "123456789"
    private const val defaultSystembruker = "x-dittnav"

    fun createOppgave(
        eventId: String = (++eventIdIncrementor).toString(),
        fodselsnummer: String = defaultFodselsnummer,
        aktiv: Boolean = true,
        systembruker: String = defaultSystembruker,
        namespace: String = "min-side",
        appnavn: String = "test-app",
        grupperingsId: String = "100$defaultFodselsnummer",
        eventTidspunkt: ZonedDateTime = OsloDateTime.now(),
        forstBehandlet: ZonedDateTime = OsloDateTime.now(),
        produsent: String = "$defaultSystembruker-produsent",
        sistOppdatert: ZonedDateTime = OsloDateTime.now(),
        tekst: String = "Dette er oppgave til brukeren",
        link: String = "https://nav.no/systemX/$defaultFodselsnummer",
        sikkerhetsnivaa: Int = 4,
        eksternVarslingInfo: EksternVarslingInfo? = null,
        synligFremTil: ZonedDateTime = OsloDateTime.now(),
        fristUtløpt: Boolean? =null
    ): Oppgave {
        return Oppgave(
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
            fristUtløpt = fristUtløpt,
            eksternVarslingSendt = eksternVarslingInfo?.sendt ?: false,
            eksternVarslingKanaler = eksternVarslingInfo?.sendteKanaler ?: emptyList(),
            eksternVarsling = eksternVarslingInfo,
            synligFremTil = synligFremTil
        )
    }
}

object OppgaveTestData {
    val oppgaveTestFnr = "12345"
    val systembruker = "x-dittnav"
    val namespace = "localhost"
    val appnavn = "dittnav"
    val grupperingsid = "100$oppgaveTestFnr"

    val oppgave1Aktiv = OppgaveObjectMother.createOppgave(
        eventId = "123",
        fodselsnummer = oppgaveTestFnr,
        aktiv = true,
        systembruker = systembruker,
        namespace = namespace,
        appnavn = appnavn,
        grupperingsId = grupperingsid,
        forstBehandlet = OsloDateTime.now(),
        eksternVarslingInfo = EksternVarslingInfoObjectMother.createEskternVarslingInfo(
            prefererteKanaler = listOf("SMS", "EPOST")
        ),
        fristUtløpt = null
    )

    val eksternVarslingForOppgave1 get() = EksternVarslingInfo(
        sendt = false,
        renotifikasjonSendt = false,
        prefererteKanaler = listOf("SMS","EPOST"),
        sendteKanaler = emptyList(),
        historikk = listOf(
            EksternVarslingHistorikkEntry(
                status = "bestilt",
                melding = "Notifikasjon er behandlet og distribusjon er bestilt",
                tidspunkt = OsloDateTime.now()
            )
        )
    )

    val oppgave2Aktiv = OppgaveObjectMother.createOppgave(
        eventId = "345",
        fodselsnummer = oppgaveTestFnr,
        aktiv = true,
        systembruker = systembruker,
        namespace = namespace,
        appnavn = appnavn,
        grupperingsId = grupperingsid,
        forstBehandlet = OsloDateTime.now().minusDays(5),
        eksternVarslingInfo = eksternVarslingForOppgave2,
        fristUtløpt = null
    )


    val eksternVarslingForOppgave2 get() = EksternVarslingInfo(
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

    val oppgave3Inaktiv = OppgaveObjectMother.createOppgave(
        eventId = "567",
        fodselsnummer = oppgaveTestFnr,
        aktiv = false,
        systembruker = systembruker,
        namespace = namespace,
        appnavn = appnavn,
        grupperingsId = grupperingsid,
        forstBehandlet = OsloDateTime.now().minusDays(15),
        fristUtløpt = null
    )
    val oppgave4 = OppgaveObjectMother.createOppgave(
        eventId = "789",
        fodselsnummer = "54321",
        aktiv = true,
        systembruker = "x-dittnav-2",
        namespace = namespace,
        appnavn = "x-dittnav",
        forstBehandlet = OsloDateTime.now().minusDays(25),
        fristUtløpt = null
    )
}
