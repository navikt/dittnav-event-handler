package no.nav.personbruker.dittnav.eventhandler.config

import io.ktor.request.*
import no.nav.personbruker.dittnav.eventhandler.common.exceptions.MissingHeaderException

typealias Systembruker = String

fun ApplicationRequest.systembrukerHeader(): Systembruker =
    this.header("systembruker")?.removePrefix("Bearer ") ?: throw MissingHeaderException("Header med systembruker mangler.")
