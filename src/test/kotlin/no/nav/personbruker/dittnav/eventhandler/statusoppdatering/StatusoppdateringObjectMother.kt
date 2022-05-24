package no.nav.personbruker.dittnav.eventhandler.statusoppdatering

import no.nav.tms.token.support.tokenx.validation.user.TokenXUser
import java.time.ZoneId
import java.time.ZonedDateTime

object StatusoppdateringObjectMother {

    private const val dummyEventId = "1"
    private const val dummyStatusGlobal = "dummyStatusGlobal"
    private const val dummyStatusIntern = "dummyStatusIntern"
    private const val dummySakstema = "dummySakstema"
    private const val dummyNamespace = "dummyNamespace"
    private const val dummyAppnavn = "dummyAppnavn"


    fun createStatusoppdatering(id: Int,
                                eventId: String,
                                fodselsnummer: String,
                                statusGlobal: String,
                                statusIntern: String,
                                sakstema: String,
                                systembruker: String,
                                namespace: String = dummyNamespace,
                                appnavn: String = dummyAppnavn): Statusoppdatering {
        return Statusoppdatering(
                id = id,
                systembruker = systembruker,
                eventId = eventId,
                eventTidspunkt = ZonedDateTime.now(ZoneId.of("Europe/Oslo")),
                forstBehandlet = ZonedDateTime.now(ZoneId.of("Europe/Oslo")),
                fodselsnummer = fodselsnummer,
                grupperingsId = "100$fodselsnummer",
                link = "https://nav.no/systemX/$fodselsnummer",
                sikkerhetsnivaa = 4,
                sistOppdatert = ZonedDateTime.now(ZoneId.of("Europe/Oslo")),
                statusGlobal = statusGlobal,
                statusIntern = statusIntern,
                sakstema = sakstema,
                produsent = "$systembruker-produsent",
                namespace = namespace,
                appnavn = appnavn)
    }

    fun createStatusoppdateringWithFodselsnummer(id: Int, fodselsnummer: String): Statusoppdatering {
        return Statusoppdatering(
                id = id,
                fodselsnummer = fodselsnummer,
                grupperingsId = "100",
                eventId = "123",
                eventTidspunkt = ZonedDateTime.now(ZoneId.of("Europe/Oslo")),
                forstBehandlet = ZonedDateTime.now(ZoneId.of("Europe/Oslo")),
                produsent = "dittnav",
                systembruker = "x-dittnav",
                sikkerhetsnivaa = 4,
                sistOppdatert = ZonedDateTime.now(ZoneId.of("Europe/Oslo")),
                link = "https://nav.no/systemX",
                statusGlobal = dummyStatusGlobal,
                statusIntern = dummyStatusIntern,
                sakstema = dummySakstema,
                namespace = dummyNamespace,
                appnavn = dummyAppnavn)
    }

    fun getStatusoppdateringEvents(bruker: TokenXUser): MutableList<Statusoppdatering> {
        return mutableListOf(
                createStatusoppdatering(1, "$dummyEventId+1", bruker.ident, "$dummyStatusGlobal+1", "$dummyStatusIntern+1", "$dummySakstema+1", systembruker = "x-dittnav", appnavn = "dittnav"),
                createStatusoppdatering(2, "$dummyEventId+2", bruker.ident, "$dummyStatusGlobal+2", "$dummyStatusIntern+2", "$dummySakstema+2", systembruker = "x-dittnav", appnavn = "dittnav"),
                createStatusoppdatering(3, "$dummyEventId+3", bruker.ident, "$dummyStatusGlobal+3", "$dummyStatusIntern+3", "$dummySakstema+3", systembruker = "x-dittnav", appnavn = "dittnav"),
                createStatusoppdatering(4, "$dummyEventId+4", bruker.ident, "$dummyStatusGlobal+4", "$dummyStatusIntern+4", "$dummySakstema+4", systembruker = "y-dittnav", appnavn = "dittnav-2"))
    }
}
