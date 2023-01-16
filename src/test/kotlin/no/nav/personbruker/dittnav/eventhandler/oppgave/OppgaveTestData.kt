package no.nav.personbruker.dittnav.eventhandler.oppgave

import no.nav.personbruker.dittnav.eventhandler.OsloDateTime
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.DoknotifikasjonTestStatus
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.EksternVarslingInfo
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.EksternVarslingInfoObjectMother
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.EksternVarslingStatus
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
        eventTidspunkt: ZonedDateTime = OsloDateTime.now(),
        forstBehandlet: ZonedDateTime = OsloDateTime.now(),
        produsent: String = "$defaultSystembruker-produsent",
        sistOppdatert: ZonedDateTime = OsloDateTime.now(),
        tekst: String = "Dette er oppgave til brukeren",
        link: String = "https://nav.no/systemX/$defaultFodselsnummer",
        sikkerhetsnivaa: Int = 4,
        eksternVarslingInfo: EksternVarslingInfo = defaultEksternVarslinginfo,
        fristUtløpt: Boolean? =null
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
            eksternVarslingInfo = eksternVarslingInfo,
            fristUtløpt = fristUtløpt
        )
    }
}

object OppgaveTestData {
    internal val oppgaveTestFnr = "12345"
    internal val systembruker = "x-dittnav"
    internal val namespace = "localhost"
    internal val appnavn = "dittnav"
    internal val grupperingsid = "100$oppgaveTestFnr"

    internal val oppgave1Aktiv = OppgaveObjectMother.createOppgave(
        id = 1,
        eventId = "123",
        fodselsnummer = oppgaveTestFnr,
        aktiv = true,
        systembruker = systembruker,
        namespace = namespace,
        appnavn = appnavn,
        grupperingsId = grupperingsid,
        forstBehandlet = OsloDateTime.now(),
        eksternVarslingInfo = EksternVarslingInfoObjectMother.createEskternVarslingInfo(
            bestilt = true,
            prefererteKanaler = listOf("SMS", "EPOST")
        ),
        fristUtløpt = null
    )

    val doknotStatusForOppgave1 = DoknotifikasjonTestStatus(
        eventId = oppgave1Aktiv.eventId,
        status = EksternVarslingStatus.OVERSENDT.name,
        melding = "melding",
        distribusjonsId = 123L,
        kanaler = "SMS"
    )

    internal val oppgave2Aktiv = OppgaveObjectMother.createOppgave(
        id = 2,
        eventId = "345",
        fodselsnummer = oppgaveTestFnr,
        aktiv = true,
        systembruker = systembruker,
        namespace = namespace,
        appnavn = appnavn,
        grupperingsId = grupperingsid,
        forstBehandlet = OsloDateTime.now().minusDays(5),
        eksternVarslingInfo = EksternVarslingInfoObjectMother.createEskternVarslingInfo(
            bestilt = true,
            prefererteKanaler = listOf("SMS", "EPOST")
        ),
        fristUtløpt = null
    )

    internal val doknotStatusForOppgave2 = DoknotifikasjonTestStatus(
        eventId = oppgave2Aktiv.eventId,
        status = EksternVarslingStatus.FEILET.name,
        melding = "feilet",
        distribusjonsId = null,
        kanaler = ""
    )

    internal val oppgave3Inaktiv = OppgaveObjectMother.createOppgave(
        id = 3,
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
    internal val oppgave4 = OppgaveObjectMother.createOppgave(
        id = 4,
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