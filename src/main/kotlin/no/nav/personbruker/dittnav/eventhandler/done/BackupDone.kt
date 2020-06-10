package no.nav.personbruker.dittnav.eventhandler.done

import java.time.ZonedDateTime

data class BackupDone(
        val eventId: String,
        val systembruker: String,
        val fodselsnummer: String,
        val grupperingsId: String,
        val eventTidspunkt: ZonedDateTime
)