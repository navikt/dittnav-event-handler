package no.nav.personbruker.dittnav.eventhandler.oppgave

fun createOppgaveEvent(oppgave: Oppgave): no.nav.brukernotifikasjon.schemas.Oppgave {
    val build = no.nav.brukernotifikasjon.schemas.Oppgave.newBuilder()
            .setFodselsnummer(oppgave.fodselsnummer)
            .setGrupperingsId(oppgave.grupperingsId)
            .setLink(oppgave.link)
            .setSikkerhetsnivaa(oppgave.sikkerhetsnivaa)
            .setTekst(oppgave.tekst)
            .setTidspunkt(oppgave.eventTidspunkt.toEpochSecond())
    return build.build()
}