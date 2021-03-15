package no.nav.personbruker.dittnav.eventhandler.statusoppdatering

fun Statusoppdatering.toDTO() = StatusoppdateringDTO(
    produsent = produsent,
    eventId = eventId,
    eventTidspunkt = eventTidspunkt,
    fodselsnummer = fodselsnummer,
    grupperingsId = grupperingsId,
    link = link,
    sikkerhetsnivaa = sikkerhetsnivaa,
    sistOppdatert = sistOppdatert,
    statusGlobal = statusGlobal,
    statusIntern = statusIntern,
    sakstema = sakstema
)
