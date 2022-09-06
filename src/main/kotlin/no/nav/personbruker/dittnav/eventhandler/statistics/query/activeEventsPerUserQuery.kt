package no.nav.personbruker.dittnav.eventhandler.statistics.query

import no.nav.personbruker.dittnav.eventhandler.common.database.mapSingleResult
import no.nav.personbruker.dittnav.eventhandler.event.EventType
import no.nav.personbruker.dittnav.eventhandler.statistics.ActiveEventsPerUser
import no.nav.personbruker.dittnav.eventhandler.statistics.IntegerMeasurement
import java.sql.Connection
import java.sql.ResultSet

private fun singleTableQueryString(type: EventType) = """
    select
        min(aggregate.events) as minEvents,
        max(aggregate.events) as maxEvents,
        avg(aggregate.events)::decimal as avgEvents,
        percentile_disc(0.25) within group ( order by aggregate.events ) as "25th_percentile",
        percentile_disc(0.50) within group ( order by aggregate.events ) as "50th_percentile",
        percentile_disc(0.75) within group ( order by aggregate.events ) as "75th_percentile",
        percentile_disc(0.90) within group ( order by aggregate.events ) as "90th_percentile",
        percentile_disc(0.99) within group ( order by aggregate.events ) as "99th_percentile"
    from (select count(1) filter (where aktiv = true) as events from ${type.eventType} group by fodselsnummer) as aggregate
"""

val activeBeskjedEventsPerUserQueryString = singleTableQueryString(EventType.BESKJED)
val activeOppgaveEventsPerUserQueryString = singleTableQueryString(EventType.OPPGAVE)
val activeInnboksEventsPerUserQueryString = singleTableQueryString(EventType.INNBOKS)

val totalActiveEventsPerUserQueryString = """
    select
        min(aggregate.events) as minEvents,
        max(aggregate.events) as maxEvents,
        avg(aggregate.events)::decimal as avgEvents,
        percentile_disc(0.25) within group ( order by aggregate.events ) as "25th_percentile",
        percentile_disc(0.50) within group ( order by aggregate.events ) as "50th_percentile",
        percentile_disc(0.75) within group ( order by aggregate.events ) as "75th_percentile",
        percentile_disc(0.90) within group ( order by aggregate.events ) as "90th_percentile",
        percentile_disc(0.99) within group ( order by aggregate.events ) as "99th_percentile"
from (
    select count(1) filter (where aktiv = true) as events from (
            SELECT aktiv, fodselsnummer FROM BESKJED
            UNION ALL
            SELECT aktiv, fodselsnummer FROM OPPGAVE
            UNION ALL
            SELECT aktiv, fodselsnummer FROM INNBOKS
        ) as inner_view group by fodselsnummer
    ) as aggregate; 
"""

fun Connection.getActiveEventsStatisticsPerUserForOppgave(): IntegerMeasurement {
    return prepareStatement(activeOppgaveEventsPerUserQueryString)
        .use {
            it.executeQuery().mapSingleResult {
                toActiveEventsPerUser()
            }
        }
}

fun Connection.getActiveEventsStatisticsPerUserForInnboks(): IntegerMeasurement {
    return prepareStatement(activeInnboksEventsPerUserQueryString)
        .use {
            it.executeQuery().mapSingleResult {
                toActiveEventsPerUser()
            }
        }
}

fun Connection.getActiveEventsStatisticsPerUserForBeskjed(): IntegerMeasurement {
    return prepareStatement(activeBeskjedEventsPerUserQueryString)
        .use {
            it.executeQuery().mapSingleResult {
                toActiveEventsPerUser()
            }
        }
}
fun Connection.getTotalActiveEventsStatisticsPerUser(): ActiveEventsPerUser =
    prepareStatement(totalActiveEventsPerUserQueryString)
        .use {
            it.executeQuery().mapSingleResult {
                toActiveEventsPerUser()
            }
        }
fun ResultSet.toActiveEventsPerUser(): ActiveEventsPerUser {
    return ActiveEventsPerUser(
        min = getInt("minEvents"),
        max = getInt("maxEvents"),
        avg = getDouble("avgEvents"),
        percentile25 = getInt("25th_percentile"),
        percentile50 = getInt("50th_percentile"),
        percentile75 = getInt("75th_percentile"),
        percentile90 = getInt("90th_percentile"),
        percentile99 = getInt("99th_percentile")
    )
}
