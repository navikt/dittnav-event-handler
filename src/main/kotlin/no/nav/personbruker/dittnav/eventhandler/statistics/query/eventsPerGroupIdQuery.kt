package no.nav.personbruker.dittnav.eventhandler.statistics.query

import no.nav.personbruker.dittnav.eventhandler.common.database.mapSingleResult
import no.nav.personbruker.dittnav.eventhandler.statistics.EventType
import no.nav.personbruker.dittnav.eventhandler.statistics.EventsPerGroupId
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
    from (select count(1) as events from ${type.eventType} group by systembruker, fodselsnummer, grupperingsid) as aggregate 
"""

val beskjedEventsPerGroupIdQueryString = singleTableQueryString(EventType.BESKJED)
val oppgaveEventsPerGroupIdQueryString = singleTableQueryString(EventType.OPPGAVE)
val innboksEventsPerGroupIdQueryString = singleTableQueryString(EventType.INNBOKS)

fun Connection.getEventsPerGroupIdForOppgave(): IntegerMeasurement {
    return prepareStatement(oppgaveEventsPerGroupIdQueryString)
        .use {
            it.executeQuery().mapSingleResult {
                toEventsPerGroupId()
            }
        }
}

fun Connection.getEventsPerGroupIdForInnboks(): IntegerMeasurement {
    return prepareStatement(innboksEventsPerGroupIdQueryString)
        .use {
            it.executeQuery().mapSingleResult {
                toEventsPerGroupId()
            }
        }
}

fun Connection.getEventsPerGroupIdForBeskjed(): IntegerMeasurement {
    return prepareStatement(beskjedEventsPerGroupIdQueryString)
        .use {
            it.executeQuery().mapSingleResult {
                toEventsPerGroupId()
            }
        }
}

val totalEventsPerGroupIdQueryString = """
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
        select count(1) as events from (
            SELECT systembruker, fodselsnummer, grupperingsid FROM BESKJED
                UNION ALL
            SELECT systembruker, fodselsnummer, grupperingsid FROM OPPGAVE
                UNION ALL
            SELECT systembruker, fodselsnummer, grupperingsid FROM INNBOKS
            ) as inner_view group by systembruker, fodselsnummer, grupperingsid
        ) as aggregate
"""
fun Connection.getTotalEventsPerGroupId(): EventsPerGroupId =
    prepareStatement(totalEventsPerGroupIdQueryString)
        .use {
            it.executeQuery().mapSingleResult {
                toEventsPerGroupId()
            }
        }

fun ResultSet.toEventsPerGroupId(): EventsPerGroupId {
    return EventsPerGroupId(
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
