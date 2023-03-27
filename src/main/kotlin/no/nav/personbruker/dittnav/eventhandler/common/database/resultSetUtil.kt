package no.nav.personbruker.dittnav.eventhandler.common.database

import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Timestamp
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

fun <T> ResultSet.mapList(result: ResultSet.() -> T): List<T> =
    mutableListOf<T>().apply {
        while (next()) {
            add(result())
        }
    }

fun <T> ResultSet.mapSingleResult(resultMapper: ResultSet.() -> T): T =
    if (next()) {
        resultMapper()
    } else {
        throw SQLException("Found no rows")
    }

const val YEAR_LOWER_LIMIT = 1975
val timeZone = ZoneId.of("Europe/Oslo")

fun convertIfUnlikelyDate(originalTimestamp: Timestamp): ZonedDateTime {
    var convertedDateTime = ZonedDateTime.ofInstant(originalTimestamp.toInstant(), timeZone)
    if (convertedDateTime.year < YEAR_LOWER_LIMIT) {
        convertedDateTime = covertFromEpochSecondsToEpochMillis(originalTimestamp)
    }
    return convertedDateTime
}

private fun covertFromEpochSecondsToEpochMillis(originalTimestamp: Timestamp): ZonedDateTime? {
    val epochSeconds = originalTimestamp.toInstant().toEpochMilli()
    val epochMillis = epochSeconds * 1000
    return ZonedDateTime.ofInstant(Timestamp(epochMillis).toInstant(), timeZone)
}

fun ResultSet.getUtcTimeStamp(label: String): Timestamp = getTimestamp(label, Calendar.getInstance(TimeZone.getTimeZone("UTC")))

fun ResultSet.getZonedDateTime(label: String, zone: ZoneId = ZoneId.of("UTC")) = getUtcTimeStamp(label).let {
    ZonedDateTime.ofInstant(it.toInstant(), zone)
}

fun ResultSet.getNullableZonedDateTime(label: String, zone: ZoneId = ZoneId.of("UTC")): ZonedDateTime? {
    val result = getTimestamp(label, Calendar.getInstance(TimeZone.getTimeZone("UTC")))

    return result?.let { ZonedDateTime.ofInstant(it.toInstant(), zone) }
}

fun ResultSet.getListFromString(columnLabel: String, separator: String = ","): List<String> {
    val stringValue = getString(columnLabel)
    return if (stringValue.isNullOrEmpty()) {
        emptyList()
    } else if (separator.isBlank()) {
        stringValue.split(separator)
    } else {
        stringValue.replace("$separator\\s*".toRegex(), separator).split(separator)
    }
}
