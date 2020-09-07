package no.nav.personbruker.dittnav.eventhandler.statusOppdatering

import no.nav.personbruker.dittnav.eventhandler.common.InnloggetBruker
import no.nav.personbruker.dittnav.eventhandler.common.database.getUtcTimeStamp
import no.nav.personbruker.dittnav.eventhandler.common.database.map
import java.sql.Connection
import java.sql.ResultSet
import java.time.ZoneId
import java.time.ZonedDateTime


fun Connection.getAllStatusOppdateringForInnloggetBruker(bruker: InnloggetBruker): List<StatusOppdatering> =
        prepareStatement("""SELECT 
            |statusOppdatering.id,
            |statusOppdatering.eventTidspunkt,
            |statusOppdatering.fodselsnummer,
            |statusOppdatering.eventId, 
            |statusOppdatering.grupperingsId,
            |statusOppdatering.link,
            |statusOppdatering.sikkerhetsnivaa,
            |statusOppdatering.sistOppdatert,
            |statusOppdatering.statusGlobal,
            |statusOppdatering.statusIntern,
            |statusOppdatering.sakstema,
            |statusOppdatering.systembruker,
            |systembrukere.produsentnavn AS produsent
            |FROM (SELECT * FROM statusOppdatering WHERE fodselsnummer = ?) AS statusOppdatering
            |LEFT JOIN systembrukere ON statusOppdatering.systembruker = systembrukere.systembruker""".trimMargin())
                .use {
                    it.setString(1, bruker.ident)
                    it.executeQuery().map {
                        toStatusOppdatering()
                    }
                }

fun Connection.getAllStatusOppdateringEvents(): List<StatusOppdatering> =
        prepareStatement("""SELECT 
            |statusOppdatering.id,
            |statusOppdatering.eventTidspunkt,
            |statusOppdatering.fodselsnummer,
            |statusOppdatering.eventId, 
            |statusOppdatering.grupperingsId,
            |statusOppdatering.link,
            |statusOppdatering.sikkerhetsnivaa,
            |statusOppdatering.sistOppdatert,
            |statusOppdatering.statusGlobal,
            |statusOppdatering.statusIntern,
            |statusOppdatering.sakstema,
            |statusOppdatering.systembruker,
            |systembrukere.produsentnavn AS produsent
            |FROM (SELECT * FROM statusOppdatering) AS statusOppdatering
            |LEFT JOIN systembrukere ON statusOppdatering.systembruker = systembrukere.systembruker""".trimMargin())
                .use {
                    it.executeQuery().map {
                        toStatusOppdatering()
                    }
                }

fun ResultSet.toStatusOppdatering(): StatusOppdatering {
    return StatusOppdatering(
            id = getInt("id"),
            fodselsnummer = getString("fodselsnummer"),
            grupperingsId = getString("grupperingsId"),
            eventId = getString("eventId"),
            eventTidspunkt = ZonedDateTime.ofInstant(getUtcTimeStamp("eventTidspunkt").toInstant(), ZoneId.of("Europe/Oslo")),
            produsent = getString("produsent") ?: "",
            systembruker = getString("systembruker"),
            sikkerhetsnivaa = getInt("sikkerhetsnivaa"),
            sistOppdatert = ZonedDateTime.ofInstant(getUtcTimeStamp("sistOppdatert").toInstant(), ZoneId.of("Europe/Oslo")),
            link = getString("link"),
            statusGlobal = getString("statusGlobal"),
            statusIntern = getString("statusIntern"),
            sakstema = getString("sakstema")
    )
}
