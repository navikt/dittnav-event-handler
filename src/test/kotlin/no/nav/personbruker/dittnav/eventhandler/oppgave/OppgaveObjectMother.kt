package no.nav.personbruker.dittnav.eventhandler.oppgave

import java.time.ZoneId
import java.time.ZonedDateTime

object OppgaveObjectMother {
    fun createOppgave(id: Int, eventId: String, fodselsnummer: String, aktiv: Boolean): Oppgave {
        return Oppgave(
                id = id,
                fodselsnummer = fodselsnummer,
                grupperingsId = "100$fodselsnummer",
                eventId = eventId,
                eventTidspunkt = ZonedDateTime.now(ZoneId.of("Europe/Oslo")),
                produsent = "dittnav",
                systembruker = "x-dittnav",
                sikkerhetsnivaa = 4,
                sistOppdatert = ZonedDateTime.now(ZoneId.of("Europe/Oslo")),
                tekst = "Dette er melding til brukeren",
                link = "https://nav.no/systemX/$fodselsnummer",
                aktiv = aktiv)
    }

    fun createOppgave(id: Int, eventId: String, fodselsnummer: String, systembruker: String, tekst: String, grupperingsId: String, link: String, sikkerhetsnivaa: Int): Oppgave {
        return Oppgave(
                id = id,
                produsent = "dittnav",
                systembruker = systembruker,
                eventTidspunkt = ZonedDateTime.now(ZoneId.of("Europe/Oslo")),
                fodselsnummer = fodselsnummer,
                eventId = eventId,
                grupperingsId = grupperingsId,
                tekst = tekst,
                link = link,
                sistOppdatert = ZonedDateTime.now(ZoneId.of("Europe/Oslo")),
                sikkerhetsnivaa = sikkerhetsnivaa,
                aktiv = true)
    }

    fun createOppgave(id: Int, eventId: String, fodselsnummer: String, aktiv: Boolean, systembruker: String): Oppgave {
        return Oppgave(
                id = id,
                fodselsnummer = fodselsnummer,
                grupperingsId = "100$fodselsnummer",
                eventId = eventId,
                eventTidspunkt = ZonedDateTime.now(ZoneId.of("Europe/Oslo")),
                produsent = "$systembruker-produsent",
                systembruker = systembruker,
                sikkerhetsnivaa = 4,
                sistOppdatert = ZonedDateTime.now(ZoneId.of("Europe/Oslo")),
                tekst = "Dette er melding til brukeren",
                link = "https://nav.no/systemX/$fodselsnummer",
                aktiv = aktiv)
    }
}

