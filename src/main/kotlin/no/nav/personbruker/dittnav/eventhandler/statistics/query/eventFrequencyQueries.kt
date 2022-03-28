package no.nav.personbruker.dittnav.eventhandler.statistics.query

import no.nav.personbruker.dittnav.eventhandler.common.database.mapList
import no.nav.personbruker.dittnav.eventhandler.statistics.EventFrequencyDistribution
import no.nav.personbruker.dittnav.eventhandler.statistics.NumberOfEventsFrequency
import java.sql.Connection

fun Connection.getActiveEventsFrequencyDistribution(table: String): EventFrequencyDistribution {
    val statement = prepareStatement(
        """
        select antallEventer, count(*) as antallBrukere
        from (
            select count(*) as antallEventer
            from $table
            where aktiv = true
            group by fodselsnummer
        ) as sub
        group by antallEventer
        order by antallEventer;
    """.trimIndent()
    )

    return EventFrequencyDistribution(
        statement.executeQuery().mapList {
            NumberOfEventsFrequency(getInt("antallEventer"), getInt("antallBrukere"))
        }
    )
}