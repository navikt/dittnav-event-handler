package no.nav.personbruker.dittnav.eventhandler.common.validation

import no.nav.personbruker.dittnav.eventhandler.common.exceptions.BackupEventException
import java.time.ZonedDateTime

private val fodselsnummerRegEx = """[\d]{1,11}""".toRegex()

fun validateFodselsnummer(field: String): String {
    validateNonNullField(field, "fødselsnummer")
    if (isNotValidFodselsnummer(field)) {
        val fve = BackupEventException("Feltet fodselsnummer kan kun innholde siffer, og maks antall er 11.")
        fve.addContext("rejectedFieldValue", field)
        throw fve
    }
    return field
}

private fun isNotValidFodselsnummer(field: String) = !fodselsnummerRegEx.matches(field)

fun validateNonNullFieldMaxLength(field: String, fieldName: String, maxLength: Int): String {
    validateNonNullField(field, fieldName)
    return validateMaxLength(field, fieldName, maxLength)
}

fun validateMaxLength(field: String, fieldName: String, maxLength: Int): String {
    if (field.length > maxLength) {
        val fve = BackupEventException("Feltet $fieldName kan ikke inneholde mer enn $maxLength tegn.")
        fve.addContext("rejectedFieldValue", field)
        throw fve
    }
    return field
}

fun validateNonNullField(field: String?, fieldName: String): String {
    if (field.isNullOrBlank()) {
        throw BackupEventException("$fieldName var null eller tomt.")
    }
    return field
}

fun zonedDateTimeToEpochSecond(date: ZonedDateTime): Long { //TODO kan denne være null?
    return date.toEpochSecond()
}

fun UTCDateToTimestampOrNull(date: ZonedDateTime?): Long? {
    return date?.let { datetime -> datetime.toEpochSecond() }
}

fun validateSikkerhetsnivaa(sikkerhetsnivaa: Int): Int {
    return when (sikkerhetsnivaa) {
        3, 4 -> sikkerhetsnivaa
        else -> throw BackupEventException("Sikkerhetsnivaa kan bare være 3 eller 4.")
    }
}
