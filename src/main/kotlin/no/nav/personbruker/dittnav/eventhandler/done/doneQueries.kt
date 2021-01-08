package no.nav.personbruker.dittnav.eventhandler.done

import no.nav.personbruker.dittnav.common.util.database.fetching.mapList
import no.nav.personbruker.dittnav.eventhandler.common.database.getUtcTimeStamp
import no.nav.personbruker.dittnav.eventhandler.config.Kafka
import java.sql.Connection
import java.sql.ResultSet
import java.time.ZoneId
import java.time.ZonedDateTime


fun Connection.getAllDoneEvents(): List<Done> =
        prepareStatement("""SELECT 
            |done.fodselsnummer,
            |done.grupperingsId,
            |done.eventId, 
            |done.eventTidspunkt,
            |done.systembruker 
            |FROM done""".trimMargin())
                .use {
                    it.fetchSize = Kafka.BACKUP_EVENT_CHUNCK_SIZE
                    it.executeQuery().mapList {
                        toDone()
                    }
                }

fun ResultSet.toDone(): Done {
    return Done(
            fodselsnummer = getString("fodselsnummer"),
            grupperingsId = getString("grupperingsId"),
            eventId = getString("eventId"),
            eventTidspunkt = ZonedDateTime.ofInstant(getUtcTimeStamp("eventTidspunkt").toInstant(), ZoneId.of("Europe/Oslo")),
            systembruker = getString("systembruker")
    )
}
