package no.nav.personbruker.dittnav.eventhandler.beskjed

import Beskjed

fun Beskjed.toDTO() = BeskjedDTO(
    uid = uid,
    fodselsnummer = fodselsnummer,
    grupperingsId = grupperingsId,
    eventId = eventId,
    eventTidspunkt = eventTidspunkt,
    produsent = appnavn,
    sikkerhetsnivaa = sikkerhetsnivaa,
    sistOppdatert = sistOppdatert,
    tekst = tekst,
    link = link,
    aktiv = aktiv
)
