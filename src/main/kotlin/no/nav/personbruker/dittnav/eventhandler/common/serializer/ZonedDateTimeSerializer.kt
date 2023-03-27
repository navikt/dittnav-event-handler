package no.nav.personbruker.dittnav.eventhandler.common.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.DateTimeException
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ZonedDateTimeSerializer : KSerializer<ZonedDateTime> {

    override fun deserialize(decoder: Decoder): ZonedDateTime {
        val value = decoder.decodeString()

        return try {
            ZonedDateTime.parse(value)
        } catch (e: DateTimeException) {
            ZonedDateTime.parse("$value+00:00")
        }
    }

    override fun serialize(encoder: Encoder, value: ZonedDateTime) {
        encoder.encodeString(value.toString())
    }

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("ZonedDateTime", PrimitiveKind.STRING)
}
