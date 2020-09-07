package no.nav.personbruker.dittnav.eventhandler.statusOppdatering

import java.time.ZoneId
import java.time.ZonedDateTime

object StatusOppdateringObjectMother {

    fun createStatusOppdatering(id: Int,
                                eventId: String,
                                fodselsnummer: String,
                                statusGlobal: String,
                                statusIntern: String,
                                sakstema: String): StatusOppdatering {
        return StatusOppdatering(
                id = id,
                systembruker = "x-dittnav",
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
                produsent = "dittnav")
    }

    fun createStatusOppdateringWithSystembruker(id: Int, systembruker: String): StatusOppdatering {
        return StatusOppdatering(
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
}