package no.nav.personbruker.dittnav.eventhandler.beskjed

import Beskjed
import no.nav.personbruker.dittnav.eventhandler.common.database.getNullableUtcTimeStamp
import no.nav.personbruker.dittnav.eventhandler.common.database.getUtcTimeStamp
import no.nav.personbruker.dittnav.eventhandler.common.database.mapList
import no.nav.personbruker.dittnav.eventhandler.common.statistics.EventCountForProducer
import no.nav.tms.token.support.tokenx.validation.user.TokenXUser
import java.sql.Connection
import java.sql.ResultSet
import java.time.ZoneId
import java.time.ZonedDateTime

fun Connection.getInaktivBeskjedForInnloggetBruker(bruker: TokenXUser): List<Beskjed> =
        getBeskjedForInnloggetBruker(bruker, false)

fun Connection.getAktivBeskjedForInnloggetBruker(bruker: TokenXUser): List<Beskjed> =
        getBeskjedForInnloggetBruker(bruker, true)

fun Connection.getAllBeskjedForInnloggetBruker(bruker: TokenXUser): List<Beskjed> =
        prepareStatement("""SELECT 
            |beskjed.id, 
            |beskjed.uid, 
            |beskjed.eventTidspunkt,
            |beskjed.fodselsnummer,
            |beskjed.eventId, 
            |beskjed.grupperingsId,
            |beskjed.tekst,
            |beskjed.link,
            |beskjed.sikkerhetsnivaa,
            |beskjed.sistOppdatert,
            |beskjed.synligFremTil,
            |beskjed.aktiv,
            |beskjed.systembruker,
            |beskjed.namespace,
            |beskjed.appnavn,
            |systembrukere.produsentnavn AS produsent
            |FROM (SELECT * FROM beskjed WHERE fodselsnummer = ?) AS beskjed
            |LEFT JOIN systembrukere ON beskjed.systembruker = systembrukere.systembruker""".trimMargin())
                .use {
                    it.setString(1, bruker.ident)
                    it.executeQuery().mapList {
                        toBeskjed()
                    }
                }

fun Connection.getBeskjedByIds(fodselsnummer: String, uid: String, eventId: String): List<Beskjed> =
        prepareStatement("""SELECT 
            |beskjed.id, 
            |beskjed.uid, 
            |beskjed.eventTidspunkt,
            |beskjed.fodselsnummer,
            |beskjed.eventId, 
            |beskjed.grupperingsId,
            |beskjed.tekst,
            |beskjed.link,
            |beskjed.sikkerhetsnivaa,
            |beskjed.sistOppdatert,
            |beskjed.synligFremTil,
            |beskjed.aktiv,
            |beskjed.systembruker,
            |beskjed.namespace,
            |beskjed.appnavn,
            |systembrukere.produsentnavn AS produsent
            |FROM (SELECT * FROM beskjed WHERE fodselsnummer = ? AND uid = ? AND eventId = ?) AS beskjed
            |LEFT JOIN systembrukere ON beskjed.systembruker = systembrukere.systembruker""".trimMargin())
                .use {
                    it.setString(1, fodselsnummer)
                    it.setString(2, uid)
                    it.setString(3, eventId)
                    it.executeQuery().mapList {
                        toBeskjed()
                    }
                }

fun Connection.getAllGroupedBeskjedEventsByIds(bruker: TokenXUser, grupperingsid: String, produsent: String): List<Beskjed> =
        prepareStatement("""SELECT 
            |beskjed.id, 
            |beskjed.uid, 
            |beskjed.eventTidspunkt,
            |beskjed.fodselsnummer,
            |beskjed.eventId, 
            |beskjed.grupperingsId,
            |beskjed.tekst,
            |beskjed.link,
            |beskjed.sikkerhetsnivaa,
            |beskjed.sistOppdatert,
            |beskjed.synligFremTil,
            |beskjed.aktiv,
            |beskjed.systembruker,
            |beskjed.namespace,
            |beskjed.appnavn,
            |systembrukere.produsentnavn AS produsent
            |FROM (SELECT * FROM beskjed WHERE fodselsnummer = ? AND grupperingsid = ?) AS beskjed
            |LEFT JOIN systembrukere ON beskjed.systembruker = systembrukere.systembruker WHERE systembrukere.produsentnavn = ?""".trimMargin())
                .use {
                    it.setString(1, bruker.ident)
                    it.setString(2, grupperingsid)
                    it.setString(3, produsent)
                    it.executeQuery().mapList {
                        toBeskjed()
                    }
                }

fun Connection.getAllGroupedBeskjedEventsBySystemuser(): Map<String, Int> {
    return prepareStatement("SELECT systembruker, COUNT(*) FROM beskjed GROUP BY systembruker",
            ResultSet.TYPE_SCROLL_INSENSITIVE,
            ResultSet.CONCUR_READ_ONLY)
            .use { statement ->
                val resultSet = statement.executeQuery()
                mutableMapOf<String, Int>().apply {
                    while (resultSet.next()) {
                        put(resultSet.getString(1), resultSet.getInt(2))
                    }
                }
            }
}

fun Connection.getAllGroupedBeskjedEventsByProducer(): List<EventCountForProducer> {
    return prepareStatement("SELECT namespace, appnavn, COUNT(*) FROM beskjed GROUP BY namespace, appnavn")
            .use { statement ->
                statement.executeQuery().mapList {
                    EventCountForProducer(
                        namespace = getString(1),
                        appName = getString(2),
                        count = getInt(3),
                    )
                }
            }
}

fun ResultSet.toBeskjed(): Beskjed {
    return Beskjed(
            id = getInt("id"),
            uid = getString("uid"),
            fodselsnummer = getString("fodselsnummer"),
            grupperingsId = getString("grupperingsId"),
            eventId = getString("eventId"),
            eventTidspunkt = ZonedDateTime.ofInstant(getUtcTimeStamp("eventTidspunkt").toInstant(), ZoneId.of("Europe/Oslo")),
            produsent = getString("produsent") ?: "",
            systembruker = getString("systembruker"),
            namespace = getString("namespace"),
            appnavn = getString("appnavn"),
            sikkerhetsnivaa = getInt("sikkerhetsnivaa"),
            sistOppdatert = ZonedDateTime.ofInstant(getUtcTimeStamp("sistOppdatert").toInstant(), ZoneId.of("Europe/Oslo")),
            synligFremTil = getNullableZonedDateTime("synligFremTil"),
            tekst = getString("tekst"),
            link = getString("link"),
            aktiv = getBoolean("aktiv")
    )
}

private fun Connection.getBeskjedForInnloggetBruker(bruker: TokenXUser, aktiv: Boolean): List<Beskjed> =
        prepareStatement("""SELECT 
            |beskjed.id, 
            |beskjed.uid, 
            |beskjed.eventTidspunkt,
            |beskjed.fodselsnummer,
            |beskjed.eventId, 
            |beskjed.grupperingsId,
            |beskjed.tekst,
            |beskjed.link,
            |beskjed.sikkerhetsnivaa,
            |beskjed.sistOppdatert,
            |beskjed.synligFremTil,
            |beskjed.aktiv,
            |beskjed.systembruker,
            |beskjed.namespace,
            |beskjed.appnavn,
            |systembrukere.produsentnavn AS produsent
            |FROM (SELECT * FROM BESKJED WHERE beskjed.fodselsnummer = ? AND beskjed.aktiv = ?) AS beskjed
            |LEFT JOIN systembrukere ON beskjed.systembruker = systembrukere.systembruker""".trimMargin())
                .use {
                    it.setString(1, bruker.ident)
                    it.setBoolean(2, aktiv)
                    it.executeQuery().mapList {
                        toBeskjed()
                    }
                }

private fun ResultSet.getNullableZonedDateTime(label: String): ZonedDateTime? {
    return getNullableUtcTimeStamp(label)?.let { timestamp -> ZonedDateTime.ofInstant(timestamp.toInstant(), ZoneId.of("Europe/Oslo")) }
}
