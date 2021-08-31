package no.nav.personbruker.dittnav.eventhandler.brukernotifikasjon

import no.nav.tms.token.support.tokenx.validation.user.TokenXUser
import java.sql.Connection
import java.sql.ResultSet

private const val countBrukernotifikasjonerQuery = "SELECT count(*) from brukernotifikasjon_view WHERE fodselsnummer = ?"

private const val countResultColumnIndex = 1

fun Connection.getNumberOfBrukernotifikasjonerByActiveStatus(bruker: TokenXUser, aktiv: Boolean): Int {
    val numberOfEvents = prepareStatement(
            "$countBrukernotifikasjonerQuery AND aktiv = ?",
            ResultSet.TYPE_SCROLL_INSENSITIVE,
            ResultSet.CONCUR_READ_ONLY)
            .use { statement ->
                statement.setString(1, bruker.ident)
                statement.setBoolean(2, aktiv)
                val resultSet = statement.executeQuery()
                resultSet.last()
                resultSet.getInt(countResultColumnIndex)
            }
    return numberOfEvents
}

fun Connection.getNumberOfBrukernotifikasjoner(bruker: TokenXUser): Int {
    val numberOfEvents = prepareStatement(countBrukernotifikasjonerQuery,
            ResultSet.TYPE_SCROLL_INSENSITIVE,
            ResultSet.CONCUR_READ_ONLY)
            .use { statement ->
                statement.setString(1, bruker.ident)
                val resultSet = statement.executeQuery()
                resultSet.last()
                resultSet.getInt(countResultColumnIndex)
            }
    return numberOfEvents
}
