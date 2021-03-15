package no.nav.personbruker.dittnav.eventhandler.done

import io.ktor.http.*
import kotlinx.serialization.Serializable
import no.nav.personbruker.dittnav.eventhandler.common.serializer.HttpStatusCodeSerializer

@Serializable
data class DoneResponse(
        val message: String,
        @Serializable(HttpStatusCodeSerializer::class)
        val httpStatus: HttpStatusCode
)
