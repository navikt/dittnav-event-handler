package no.nav.personbruker.dittnav.eventhandler.done

import no.nav.personbruker.dittnav.eventhandler.common.database.getUtcTimeStamp
import no.nav.personbruker.dittnav.eventhandler.common.database.map
import no.nav.personbruker.dittnav.eventhandler.config.Kafka
import java.sql.Connection
import java.sql.ResultSet
import java.time.ZoneId
import java.time.ZonedDateTime


fun Connection.getAllDoneEvents(): List<BackupDone> =
        prepareStatement("""SELECT
            |eventTidspunkt,
            |fodselsnummer,
            |eventId, 
            |grupperingsId,
            |systembruker,
            |FROM done""".trimMargin())
                .use {
                    it.fetchSize = Kafka.BACKUP_EVENT_CHUNCK_SIZE
                    it.executeQuery().map {
                        toDone()
                    }
                }

fun ResultSet.toDone(): BackupDone {
    return BackupDone(
            fodselsnummer = getString("fodselsnummer"),
            grupperingsId = getString("grupperingsId"),
            eventId = getString("eventId"),
            eventTidspunkt = ZonedDateTime.ofInstant(getUtcTimeStamp("eventTidspunkt").toInstant(), ZoneId.of("Europe/Oslo")),
            systembruker = getString("systembruker")
    )
}