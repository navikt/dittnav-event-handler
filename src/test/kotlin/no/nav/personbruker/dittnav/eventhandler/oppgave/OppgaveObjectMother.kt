package no.nav.personbruker.dittnav.eventhandler.oppgave

import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedObjectMother
import java.time.ZoneId
import java.time.ZonedDateTime

object OppgaveObjectMother {

    private var idIncrementor = 0
    private var eventIdIncrementor = 0

    val defaultFodselsnummer = "123456789"
    val defaultAktiv = true
    val defaultSystembruker = "x-dittnav"
    val defaultNamespace = "min-side"
    val defaultAppnavn = "test-app"
    val defaultProdusent = "$defaultSystembruker-produsent"
    val defaultEventTidspunkt = ZonedDateTime.now(ZoneId.of("Europe/Oslo"))
    val defaultGrupperingsId = "100$defaultFodselsnummer"
    val defaultTekst = "Dette er oppgave til brukeren"
    val defaultLink = "https://nav.no/systemX/$defaultFodselsnummer"
    val defaultSistOppdatert = ZonedDateTime.now(ZoneId.of("Europe/Oslo"))
    val defaultSikkerhetsnivaa = 4

    fun createOppgave(
        id: Int = ++idIncrementor,
        eventId: String = (++eventIdIncrementor).toString(),
        fodselsnummer: String = defaultFodselsnummer,
        aktiv: Boolean = defaultAktiv,
        systembruker: String = defaultSystembruker,
        namespace: String = defaultNamespace,
        appnavn: String = defaultAppnavn,
        grupperingsId: String = defaultGrupperingsId,
        eventTidspunkt: ZonedDateTime = defaultEventTidspunkt,
        produsent: String = defaultProdusent,
        sistOppdatert: ZonedDateTime = defaultSistOppdatert,
        tekst: String = defaultTekst,
        link: String = defaultLink,
        sikkerhetsnivaa: Int = defaultSikkerhetsnivaa
    ): Oppgave {
        return Oppgave(
            id = id,
            fodselsnummer = fodselsnummer,
            grupperingsId = grupperingsId,
            eventId = eventId,
            eventTidspunkt = eventTidspunkt,
            produsent = produsent,
            systembruker = systembruker,
            namespace = namespace,
            appnavn = appnavn,
            sikkerhetsnivaa = sikkerhetsnivaa,
            sistOppdatert = sistOppdatert,
            tekst = tekst,
            link = link,
            aktiv = aktiv
        )
    }
}

