package no.nav.personbruker.dittnav.eventhandler.eksternvarsling

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.personbruker.dittnav.eventhandler.common.serializer.DefaultUtcZonedDateTimeDeserializer
import java.sql.ResultSet
import java.time.ZonedDateTime

private val objectMapper = jacksonMapperBuilder()
    .addModule(
        JavaTimeModule()
            .addDeserializer(ZonedDateTime::class.java, DefaultUtcZonedDateTimeDeserializer())
    )
    .build()

fun ResultSet.getEksternVarslingHistorikk(label: String): List<EksternVarslingHistorikkEntry> {

    return getString(label)?.let { objectMapper.readValue(it) } ?: emptyList()
}
