package no.nav.personbruker.dittnav.eventhandler.common

import java.time.LocalDate

fun daysAgo(days: Int): LocalDate = LocalDate.now().minusDays(days.toLong())
