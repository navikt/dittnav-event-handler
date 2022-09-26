package no.nav.personbruker.dittnav.eventhandler.common

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

object LocalDateTimeHelper {
    fun nowAtUtc(): LocalDateTime = LocalDateTime.now(ZoneId.of("UTC"))
}
