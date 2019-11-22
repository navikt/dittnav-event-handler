package no.nav.personbruker.dittnav.eventhandler.informasjon

import Informasjon
import no.nav.personbruker.dittnav.eventhandler.common.database.map
import java.sql.Connection
import java.sql.ResultSet
import java.time.LocalDateTime
import java.time.ZoneId

fun Connection.getAllInformasjonByAktorId(aktorId: String): List<Informasjon> =
        prepareStatement("""SELECT * FROM INFORMASJON WHERE aktorId = ?""")
                .use {
                    it.setString(1, aktorId)
                    it.executeQuery().map {
                        toInformasjon()
                    }
                }

fun Connection.getActiveInformasjonByAktorId(aktorId: String): List<Informasjon> =
        prepareStatement("""SELECT * FROM INFORMASJON WHERE aktorId = ? AND aktiv = true""")
                .use {
                    it.setString(1, aktorId)
                    it.executeQuery().map {
                        toInformasjon()
                    }
                }

private fun ResultSet.toInformasjon(): Informasjon {
    return Informasjon(
            id = getInt("id"),
            produsent = getString("produsent"),
            eventTidspunkt = LocalDateTime.ofInstant(getTimestamp("eventTidspunkt").toInstant(), ZoneId.of("Europe/Oslo")),
            aktorId = getString("aktorId"),
            eventId = getString("eventId"),
            dokumentId = getString("dokumentId"),
            tekst = getString("tekst"),
            link = getString("link"),
            sikkerhetsnivaa = getInt("sikkerhetsnivaa"),
            sistOppdatert = LocalDateTime.ofInstant(getTimestamp("sistOppdatert").toInstant(), ZoneId.of("Europe/Oslo")),
            aktiv = getBoolean("aktiv")
    )
}