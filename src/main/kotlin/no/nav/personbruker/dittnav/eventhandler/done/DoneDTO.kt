package no.nav.personbruker.dittnav.eventhandler.done

import kotlinx.serialization.Serializable

@Serializable
data class DoneDTO(
        val uid: String,
        val eventId: String
)
