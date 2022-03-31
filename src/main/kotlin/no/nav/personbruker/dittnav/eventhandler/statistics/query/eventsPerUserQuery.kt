package no.nav.personbruker.dittnav.eventhandler.statistics.query

import no.nav.personbruker.dittnav.eventhandler.common.database.mapSingleResult
import no.nav.personbruker.dittnav.eventhandler.event.EventType
import no.nav.personbruker.dittnav.eventhandler.statistics.EventsPerUser
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
    from (select count(1) as events from ${type.eventType} group by fodselsnummer) as aggregate
"""

val beskjedEventsPerUserQueryString = singleTableQueryString(EventType.BESKJED)
val oppgaveEventsPerUserQueryString = singleTableQueryString(EventType.OPPGAVE)
val innboksEventsPerUserQueryString = singleTableQueryString(EventType.INNBOKS)

fun Connection.getEventsPerUserForOppgave(): IntegerMeasurement {
    return prepareStatement(oppgaveEventsPerUserQueryString)
        .use {
            it.executeQuery().mapSingleResult {
                toEventsPerUser()
            }
        }
}
fun Connection.getEventsPerUserForInnboks(): IntegerMeasurement {
    return prepareStatement(innboksEventsPerUserQueryString)
        .use {
            it.executeQuery().mapSingleResult {
                toEventsPerUser()
            }
        }
}
fun Connection.getEventsPerUserForBeskjed(): IntegerMeasurement {
    return prepareStatement(beskjedEventsPerUserQueryString)
        .use {
            it.executeQuery().mapSingleResult {
                toEventsPerUser()
            }
        }
}

val totalEventsPerUserQueryString = """
    select
        min(aggregate.events) as minEvents,
        max(aggregate.events) as maxEvents,
        avg(aggregate.events)::decimal as avgEvents,
        percentile_disc(0.25) within group ( order by aggregate.events ) as "25th_percentile",
        percentile_disc(0.50) within group ( order by aggregate.events ) as "50th_percentile",
        percentile_disc(0.75) within group ( order by aggregate.events ) as "75th_percentile",
        percentile_disc(0.90) within group ( order by aggregate.events ) as "90th_percentile",
        percentile_disc(0.99) within group ( order by aggregate.events ) as "99th_percentile"
    from (select count(1) as events from brukernotifikasjon_view group by fodselsnummer) as aggregate
"""
fun Connection.getTotalEventsPerUser(): IntegerMeasurement {
    return prepareStatement(totalEventsPerUserQueryString)
        .use {
            it.executeQuery().mapSingleResult {
                toEventsPerUser()
            }
        }
}

fun ResultSet.toEventsPerUser(): EventsPerUser {
    return EventsPerUser(
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
