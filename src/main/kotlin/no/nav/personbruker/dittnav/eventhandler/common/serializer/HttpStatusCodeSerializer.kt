package no.nav.personbruker.dittnav.eventhandler.common.serializer

import io.ktor.http.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class HttpStatusCodeSerializer : KSerializer<HttpStatusCode> {

    override fun deserialize(decoder: Decoder): HttpStatusCode {
        val value = decoder.decodeInt()
        return HttpStatusCode.fromValue(value)
    }

    override fun serialize(encoder: Encoder, value: HttpStatusCode) {
        encoder.encodeInt(value.value)
    }

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("HttpStatusCode", PrimitiveKind.INT)
}
