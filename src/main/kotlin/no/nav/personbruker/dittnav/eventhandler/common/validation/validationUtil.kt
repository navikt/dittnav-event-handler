package no.nav.personbruker.dittnav.eventhandler.common.validation

import no.nav.personbruker.dittnav.eventhandler.common.exceptions.FieldValidationException
import java.time.*


fun validateNonNullFieldMaxLength(field: String?, fieldName: String, maxLength: Int): String {
    return validateMaxLength(validateNonNullField(field, fieldName), fieldName, maxLength)
}

fun zonedDateTimeToUTCLocalDate(zonedDateTime: ZonedDateTime?): LocalDateTime {
    return LocalDateTime.ofInstant(zonedDateTime?.toEpochSecond()?.let { Instant.ofEpochMilli(it) }, ZoneOffset.UTC)
}

private fun validateMaxLength(field: String, fieldName: String, maxLength: Int): String {
    if (field.length > maxLength) {
        val fve = FieldValidationException("Feltet $fieldName kan ikke inneholde mer enn $maxLength tegn.")
        fve.addContext("rejectedFieldValue", field)
        throw fve
    }
    return field
}

private fun validateNonNullField(field: String?, fieldName: String): String {
    if (field.isNullOrBlank()) {
        throw FieldValidationException("$fieldName var null eller tomt.")
    }
    return field
}

