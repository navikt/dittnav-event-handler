package no.nav.personbruker.dittnav.eventhandler.statusoppdatering

import no.nav.tms.token.support.tokenx.validation.user.TokenXUser
import java.time.ZoneId
import java.time.ZonedDateTime

object StatusoppdateringObjectMother {

    private val dummyEventId = "1"
    private val dummyStatusGlobal = "dummyStatusGlobal"
    private val dummyStatusIntern = "dummyStatusIntern"
    private val dummySakstema = "dummySakstema"

    fun createStatusoppdatering(id: Int,
                                eventId: String,
                                fodselsnummer: String,
                                statusGlobal: String,
                                statusIntern: String,
                                sakstema: String,
                                systembruker: String): Statusoppdatering {
        return Statusoppdatering(
                id = id,
                systembruker = systembruker,
                eventId = eventId,
                eventTidspunkt = ZonedDateTime.now(ZoneId.of("Europe/Oslo")),
                fodselsnummer = fodselsnummer,
                grupperingsId = "100$fodselsnummer",
                link = "https://nav.no/systemX/$fodselsnummer",
                sikkerhetsnivaa = 4,
                sistOppdatert = ZonedDateTime.now(ZoneId.of("Europe/Oslo")),
                statusGlobal = statusGlobal,
                statusIntern = statusIntern,
                sakstema = sakstema,
                produsent = "$systembruker-produsent")
    }

    fun createStatusoppdateringWithSystembruker(id: Int, systembruker: String): Statusoppdatering {
        return Statusoppdatering(
                id = id,
                fodselsnummer = "112233",
                grupperingsId = "100",
                eventId = "123",
                eventTidspunkt = ZonedDateTime.now(ZoneId.of("Europe/Oslo")),
                produsent = "",
                systembruker = systembruker,
                sikkerhetsnivaa = 4,
                sistOppdatert = ZonedDateTime.now(ZoneId.of("Europe/Oslo")),
                link = "https://nav.no/systemX",
                statusGlobal = "dummyStatusGlobal",
                statusIntern = "dummyStatusIntern",
                sakstema = "dummySakstema")
    }

    fun createStatusoppdateringWithFodselsnummer(id: Int, fodselsnummer: String): Statusoppdatering {
        return Statusoppdatering(
                id = id,
                fodselsnummer = fodselsnummer,
                grupperingsId = "100",
                eventId = "123",
                eventTidspunkt = ZonedDateTime.now(ZoneId.of("Europe/Oslo")),
                produsent = "dittnav",
                systembruker = "x-dittnav",
                sikkerhetsnivaa = 4,
                sistOppdatert = ZonedDateTime.now(ZoneId.of("Europe/Oslo")),
                link = "https://nav.no/systemX",
                statusGlobal = "dummyStatusGlobal",
                statusIntern = "dummyStatusIntern",
                sakstema = "dummySakstema")
    }

    fun getStatusoppdateringEvents(bruker: TokenXUser): MutableList<Statusoppdatering> {
        return mutableListOf(
                createStatusoppdatering(1, "$dummyEventId+1", bruker.ident, "$dummyStatusGlobal+1", "$dummyStatusIntern+1", "$dummySakstema+1", systembruker = "x-dittnav"),
                createStatusoppdatering(2, "$dummyEventId+2", bruker.ident, "$dummyStatusGlobal+2", "$dummyStatusIntern+2", "$dummySakstema+2", systembruker = "x-dittnav"),
                createStatusoppdatering(3, "$dummyEventId+3", bruker.ident, "$dummyStatusGlobal+3", "$dummyStatusIntern+3", "$dummySakstema+3", systembruker = "x-dittnav"),
                createStatusoppdatering(4, "$dummyEventId+4", bruker.ident, "$dummyStatusGlobal+4", "$dummyStatusIntern+4", "$dummySakstema+4", systembruker = "y-dittnav"))
    }
}
