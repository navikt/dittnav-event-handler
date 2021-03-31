package no.nav.personbruker.dittnav.eventhandler.config

import io.ktor.request.*

typealias Systembruker = String

fun ApplicationRequest.systembrukerHeader(): Systembruker =
    this.header("systembruker")?.removePrefix("Bearer ") ?: throw RuntimeException("Header med systembruker mangler.")
