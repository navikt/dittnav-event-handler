package no.nav.personbruker.dittnav.eventhandler.innboks

fun Innboks.toDTO() = InnboksDTO(
    produsent = appnavn,
    eventTidspunkt = eventTidspunkt,
    fodselsnummer = fodselsnummer,
    eventId = eventId,
    grupperingsId = grupperingsId,
    tekst = tekst,
    link = link,
    sikkerhetsnivaa = sikkerhetsnivaa,
    sistOppdatert = sistOppdatert,
    aktiv = aktiv
)
