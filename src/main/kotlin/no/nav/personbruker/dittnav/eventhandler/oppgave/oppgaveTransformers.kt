package no.nav.personbruker.dittnav.eventhandler.oppgave

fun Oppgave.toDTO() = OppgaveDTO(
    fodselsnummer = fodselsnummer,
    grupperingsId = grupperingsId,
    eventId = eventId,
    produsent = appnavn,
    sikkerhetsnivaa = sikkerhetsnivaa,
    sistOppdatert = sistOppdatert,
    tekst = tekst,
    link = link,
    aktiv = aktiv,
    forstBehandlet = forstBehandlet,
    eksternVarslingSendt = eksternVarslingInfo.sendt,
    eksternVarslingKanaler = eksternVarslingInfo.sendteKanaler
)
