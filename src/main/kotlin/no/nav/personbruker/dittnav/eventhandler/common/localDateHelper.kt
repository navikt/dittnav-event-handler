package no.nav.personbruker.dittnav.eventhandler.common

import java.time.LocalDate

fun oneYearAgo(): LocalDate = LocalDate.now().minusYears(1)
