package no.nav.personbruker.dittnav.eventhandler.statusoppdatering

import no.nav.personbruker.dittnav.eventhandler.common.InnloggetBruker
import no.nav.personbruker.dittnav.eventhandler.common.database.getUtcTimeStamp
import no.nav.personbruker.dittnav.eventhandler.common.database.map
import java.sql.Connection
import java.sql.ResultSet
import java.time.ZoneId
import java.time.ZonedDateTime

fun Connection.getAllStatusoppdateringForInnloggetBruker(bruker: InnloggetBruker): List<Statusoppdatering> =
        prepareStatement("""SELECT 
            |statusoppdatering.id,
            |statusoppdatering.eventTidspunkt,
            |statusoppdatering.fodselsnummer,
            |statusoppdatering.eventId, 
            |statusoppdatering.grupperingsId,
            |statusoppdatering.link,
            |statusoppdatering.sikkerhetsnivaa,
            |statusoppdatering.sistOppdatert,
            |statusoppdatering.statusGlobal,
            |statusoppdatering.statusIntern,
            |statusoppdatering.sakstema,
            |statusoppdatering.systembruker,
            |systembrukere.produsentnavn AS produsent
            |FROM (SELECT * FROM statusoppdatering WHERE fodselsnummer = ?) AS statusoppdatering
            |LEFT JOIN systembrukere ON statusoppdatering.systembruker = systembrukere.systembruker""".trimMargin())
                .use {
                    it.setString(1, bruker.ident)
                    it.executeQuery().map {
                        toStatusoppdatering()
                    }
                }

fun Connection.getAllStatusoppdateringEvents(): List<Statusoppdatering> =
        prepareStatement("""SELECT 
            |statusoppdatering.id,
            |statusoppdatering.eventTidspunkt,
            |statusoppdatering.fodselsnummer,
            |statusoppdatering.eventId, 
            |statusoppdatering.grupperingsId,
            |statusoppdatering.link,
            |statusoppdatering.sikkerhetsnivaa,
            |statusoppdatering.sistOppdatert,
            |statusoppdatering.statusGlobal,
            |statusoppdatering.statusIntern,
            |statusoppdatering.sakstema,
            |statusoppdatering.systembruker,
            |systembrukere.produsentnavn AS produsent
            |FROM (SELECT * FROM statusoppdatering) AS statusoppdatering
            |LEFT JOIN systembrukere ON statusoppdatering.systembruker = systembrukere.systembruker""".trimMargin())
                .use {
                    it.executeQuery().map {
                        toStatusoppdatering()
                    }
                }

fun ResultSet.toStatusoppdatering(): Statusoppdatering {
    return Statusoppdatering(
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
