package no.nav.personbruker.dittnav.eventhandler.beskjed

import Beskjed
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.EksternVarslingInfo
import no.nav.personbruker.dittnav.eventhandler.eksternvarsling.EksternVarslingInfoObjectMother.createEskternVarslingInfo
import java.time.ZoneId
import java.time.ZonedDateTime

object BeskjedObjectMother {

    private var idIncrementor = 0
    private var eventIdIncrementor = 0

    val defaultFodselsnummer = "123456789"
    val defaultSynligFremTil = ZonedDateTime.now(ZoneId.of("Europe/Oslo")).plusDays(7)
    val defaultAktiv = true
    val defaultSystembruker = "x-dittnav"
    val defaultNamespace = "min-side"
    val defaultAppnavn = "test-app"
    val defaultProdusent = "$defaultSystembruker-produsent"
    val defaultEventTidspunkt = ZonedDateTime.now(ZoneId.of("Europe/Oslo"))
    val defaultForstBehandlet = ZonedDateTime.now(ZoneId.of("Europe/Oslo"))
    val defaultGrupperingsId = "100$defaultFodselsnummer"
    val defaultTekst = "Dette er beskjed til brukeren"
    val defaultLink = "https://nav.no/systemX/$defaultFodselsnummer"
    val defaultSistOppdatert = ZonedDateTime.now(ZoneId.of("Europe/Oslo"))
    val defaultSikkerhetsnivaa = 4
    val defaultEksternVarslinginfo = createEskternVarslingInfo()

    fun createBeskjed(
        id: Int = ++idIncrementor,
        eventId: String = (++eventIdIncrementor).toString(),
        fodselsnummer: String = defaultFodselsnummer,
        synligFremTil: ZonedDateTime? = defaultSynligFremTil,
        aktiv: Boolean = defaultAktiv,
        systembruker: String = defaultSystembruker,
        namespace: String = defaultNamespace,
        appnavn: String = defaultAppnavn,
        produsent: String = defaultProdusent,
        eventTidspunkt: ZonedDateTime = defaultEventTidspunkt,
        forstBehandlet: ZonedDateTime = defaultForstBehandlet,
        grupperingsId: String = defaultGrupperingsId,
        tekst: String = defaultTekst,
        link: String = defaultLink,
        sistOppdatert: ZonedDateTime = defaultSistOppdatert,
        sikkerhetsnivaa: Int = defaultSikkerhetsnivaa,
        eksternVarslingInfo: EksternVarslingInfo = defaultEksternVarslinginfo
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
            eksternVarslingInfo = eksternVarslingInfo
        )
    }
}
