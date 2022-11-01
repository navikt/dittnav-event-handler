package no.nav.personbruker.dittnav.eventhandler.done

import java.time.ZonedDateTime

data class Done(
    val eventId: String,
    val systembruker: String,
    val namespace: String,
    val appnavn: String,
    val fodselsnummer: String,
    val grupperingsId: String,
    val eventTidspunkt: ZonedDateTime,
    val forstBehandlet: ZonedDateTime
)
