package no.nav.personbruker.dittnav.eventhandler.brukernotifikasjon

import no.nav.personbruker.dittnav.eventhandler.common.InnloggetBruker
import java.sql.Connection
import java.sql.ResultSet

private val countBrukernotifikasjonerQuery = """SELECT count(*) from brukernotifikasjon_view WHERE fodselsnummer = ?"""

fun Connection.getNumberOfBrukernotifikasjonerByActiveStatus(bruker: InnloggetBruker, aktiv: Boolean): Int {
    val numberOfEvents = prepareStatement(
            """$countBrukernotifikasjonerQuery AND aktiv = ?""".trimMargin(),
            ResultSet.TYPE_SCROLL_INSENSITIVE,
            ResultSet.CONCUR_READ_ONLY)
            .use { statement ->
                statement.setString(1, bruker.ident)
                statement.setBoolean(2, aktiv)
                val rs = statement.executeQuery()
                rs.last()
                val indexOfRow = rs.row
                rs.getInt(indexOfRow)
            }
    return numberOfEvents
}

fun Connection.getNumberOfBrukernotifikasjoner(bruker: InnloggetBruker): Int {
    val numberOfEvents = prepareStatement(countBrukernotifikasjonerQuery,
            ResultSet.TYPE_SCROLL_INSENSITIVE,
            ResultSet.CONCUR_READ_ONLY)
            .use { statement ->
                statement.setString(1, bruker.ident)
                val rs = statement.executeQuery()
                rs.last()
                val indexOfRow = rs.row
                rs.getInt(indexOfRow)
            }
    return numberOfEvents
}
