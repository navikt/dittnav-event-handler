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

fun validateNonNullFieldMaxLength(field: String?, fieldName: String, maxLength: Int): String {
    return validateMaxLength(validateNonNullField(field, fieldName), fieldName, maxLength)
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

fun zonedDateTimeToEpochMilli(date: ZonedDateTime, fieldName: String): Long {
    if (date == null) {
        throw BackupEventException("$fieldName var null eller tomt.")
    }
    return date.toInstant().toEpochMilli()
}

fun UTCDateToTimestampOrNull(date: ZonedDateTime?): Long? {
    return date?.let { datetime -> datetime.toInstant().toEpochMilli() }
}

fun validateSikkerhetsnivaa(sikkerhetsnivaa: Int): Int {
    return when (sikkerhetsnivaa) {
        3, 4 -> sikkerhetsnivaa
        else -> throw BackupEventException("Sikkerhetsnivaa kan bare være 3 eller 4.")
    }
}
