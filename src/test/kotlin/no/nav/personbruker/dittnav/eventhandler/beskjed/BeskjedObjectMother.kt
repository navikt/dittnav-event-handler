package no.nav.personbruker.dittnav.eventhandler.beskjed

import Beskjed
import java.time.ZoneId
import java.time.ZonedDateTime

object BeskjedObjectMother {

    fun createBeskjed(id: Int, eventId: String, fodselsnummer: String, synligFremTil: ZonedDateTime?, uid: String, aktiv: Boolean, systembruker: String): Beskjed {
        return Beskjed(
                uid = uid,
                id = id,
                produsent = "$systembruker-produsent",
                systembruker = systembruker,
                eventTidspunkt = ZonedDateTime.now(ZoneId.of("Europe/Oslo")),
                fodselsnummer = fodselsnummer,
                eventId = eventId,
                grupperingsId = "100$fodselsnummer",
                tekst = "Dette er beskjed til brukeren",
                link = "https://nav.no/systemX/$fodselsnummer",
                sistOppdatert = ZonedDateTime.now(ZoneId.of("Europe/Oslo")),
                synligFremTil = synligFremTil,
                sikkerhetsnivaa = 4,
                aktiv = aktiv)
    }

    fun createBeskjed(id: Int, eventId: String, fodselsnummer: String, synligFremTil: ZonedDateTime?, uid: String, aktiv: Boolean): Beskjed {
        return Beskjed(
                uid = uid,
                id = id,
                produsent = "dittnav",
                systembruker = "x-dittnav",
                eventTidspunkt = ZonedDateTime.now(ZoneId.of("Europe/Oslo")),
                fodselsnummer = fodselsnummer,
                eventId = eventId,
                grupperingsId = "100$fodselsnummer",
                tekst = "Dette er beskjed til brukeren",
                link = "https://nav.no/systemX/$fodselsnummer",
                sistOppdatert = ZonedDateTime.now(ZoneId.of("Europe/Oslo")),
                synligFremTil = synligFremTil,
                sikkerhetsnivaa = 4,
                aktiv = aktiv)
    }

    fun createBeskjed(id: Int, eventId: String, fodselsnummer: String, systembruker: String, tekst: String, grupperingsId: String, link: String, sikkerhetsnivaa: Int, eventTidspunkt: ZonedDateTime): Beskjed {
        return Beskjed(
                uid = "abc",
                id = id,
                produsent = "dittnav",
                systembruker = systembruker,
                eventTidspunkt = eventTidspunkt,
                fodselsnummer = fodselsnummer,
                eventId = eventId,
                grupperingsId = grupperingsId,
                tekst = tekst,
                link = link,
                sistOppdatert = ZonedDateTime.now(ZoneId.of("Europe/Oslo")),
                synligFremTil = null,
                sikkerhetsnivaa = sikkerhetsnivaa,
                aktiv = true)
    }

}
