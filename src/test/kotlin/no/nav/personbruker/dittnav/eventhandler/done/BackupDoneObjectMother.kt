package no.nav.personbruker.dittnav.eventhandler.done

import java.time.ZoneId
import java.time.ZonedDateTime

object BackupDoneObjectMother {

    fun createBackupDone(eventId: String, fodselsnummer: String): BackupDone {
        return BackupDone(
                eventId = eventId,
                systembruker = "x-dittnav",
                fodselsnummer = fodselsnummer,
                grupperingsId = "100$fodselsnummer",
                eventTidspunkt = ZonedDateTime.now(ZoneId.of("Europe/Oslo"))
        )
    }
}
