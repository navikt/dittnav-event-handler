@file:UseSerializers(HttpStatusCodeSerializer::class)
package no.nav.personbruker.dittnav.eventhandler.done

import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import no.nav.personbruker.dittnav.eventhandler.common.serializer.HttpStatusCodeSerializer

@Serializable
data class DoneResponse(
    val message: String,
    val httpStatus: HttpStatusCode
)
