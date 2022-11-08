package no.nav.personbruker.dittnav.eventhandler

import no.nav.personbruker.dittnav.eventhandler.beskjed.Beskjed
import java.time.ZonedDateTime
import kotlin.random.Random

fun createBeskjed(
    id: Int = Random.nextInt(),
    eventId: String = (Random.nextInt()).toString(),
    fodselsnummer: String = "123456789",
    synligFremTil: ZonedDateTime? = OsloDateTime.now().plusDays(7),
    aktiv: Boolean = true,
    systembruker: String = "x-dittnav",
    namespace: String = "min-side",
    appnavn: String = "test-app",
    produsent: String = "x-dittnav-produsent",
    eventTidspunkt: ZonedDateTime = OsloDateTime.now(),
    forstBehandlet: ZonedDateTime = OsloDateTime.now(),
    grupperingsId: String = "100$123456789r",
    tekst: String = "Dette er beskjed til brukeren",
    link: String = "https://nav.no/systemX/$123456789",
    sistOppdatert: ZonedDateTime = OsloDateTime.now(),
    sikkerhetsnivaa: Int = 4,
    eksternVarslingSendt: Boolean = false,
    eksternVarslingKanaler: List<String> = listOf(),
): Beskjed {
    return Beskjed(
        id = id,
        produsent = produsent,
        systembruker = systembruker,
        namespace = namespace,
        appnavn = appnavn,
        eventTidspunkt = eventTidspunkt,
        forstBehandlet = forstBehandlet,
        fodselsnummer = fodselsnummer,
        eventId = eventId,
        grupperingsId = grupperingsId,
        tekst = tekst,
        link = link,
        sistOppdatert = sistOppdatert,
        synligFremTil = synligFremTil,
        sikkerhetsnivaa = sikkerhetsnivaa,
        aktiv = aktiv,
        eksternVarslingSendt = eksternVarslingSendt,
        eksternVarslingKanaler = eksternVarslingKanaler
    )
}
