package no.nav.personbruker.dittnav.eventhandler.statistics.query

import no.nav.personbruker.dittnav.eventhandler.common.database.mapList
import no.nav.personbruker.dittnav.eventhandler.statistics.NumberOfEventsFrequency
import java.sql.Connection

fun Connection.getActiveEventsFrequencyDistribution(table: String): List<NumberOfEventsFrequency> =
    prepareStatement("""
        select antallEventer, count(*) as antallBrukere
        from (
            select count(*) as antallEventer
            from $table
            where aktiv = true
            group by fodselsnummer
        ) as sub
        group by antallEventer
        order by antallEventer;
    """.trimIndent())
        .use {
            it.executeQuery().mapList {
                NumberOfEventsFrequency(getInt("antallEventer"), getInt("antallBrukere"))
            }
        }