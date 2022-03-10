package no.nav.personbruker.dittnav.eventhandler.statistics.query

import no.nav.personbruker.dittnav.eventhandler.common.database.mapSingleResult
import no.nav.personbruker.dittnav.eventhandler.statistics.DecimalMeasurement
import no.nav.personbruker.dittnav.eventhandler.statistics.EventActiveRatePerUser
import no.nav.personbruker.dittnav.eventhandler.statistics.EventType
import java.sql.Connection
import java.sql.ResultSet

private fun singleTableQueryString(type: EventType) = """
    select
        min(aggregate.rate) as minRate, 
        max(aggregate.rate) as maxRate,
        avg(aggregate.rate)::decimal as avgRate,
        percentile_disc(0.25) within group ( order by aggregate.rate ) as "25th_percentile",
        percentile_disc(0.50) within group ( order by aggregate.rate ) as "50th_percentile",
        percentile_disc(0.75) within group ( order by aggregate.rate ) as "75th_percentile",
        percentile_disc(0.90) within group ( order by aggregate.rate ) as "90th_percentile",
        percentile_disc(0.99) within group ( order by aggregate.rate ) as "99th_percentile"
    from (select count(1) filter ( where aktiv = true )::decimal / count(1)::decimal as rate from ${type.eventType} group by fodselsnummer) as aggregate;
"""

val beskjedEventActiveRatePerUserQueryString = singleTableQueryString(EventType.BESKJED)
val oppgaveEventActiveRatePerUserQueryString = singleTableQueryString(EventType.OPPGAVE)
val innboksEventActiveRatePerUserQueryString = singleTableQueryString(EventType.INNBOKS)

fun Connection.getActiveRateEventsStatisticsPerUserForOppgave(): DecimalMeasurement {
    return prepareStatement(oppgaveEventActiveRatePerUserQueryString)
        .use {
            it.executeQuery().mapSingleResult {
                toEventActiveRate()
            }
        }
}

fun Connection.getActiveRateEventsStatisticsPerUserForInnboks(): DecimalMeasurement {
    return prepareStatement(innboksEventActiveRatePerUserQueryString)
        .use {
            it.executeQuery().mapSingleResult {
                toEventActiveRate()
            }
        }
}

fun Connection.getActiveRateEventsStatisticsPerUserForBeskjed(): DecimalMeasurement {
    return prepareStatement(beskjedEventActiveRatePerUserQueryString)
        .use {
            it.executeQuery().mapSingleResult {
                toEventActiveRate()
            }
        }
}

val totalEventActiveRatePerUserQueryString = """
    select
        min(aggregate.rate) as minRate, 
        max(aggregate.rate) as maxRate,
        avg(aggregate.rate)::decimal as avgRate,
        percentile_disc(0.25) within group ( order by aggregate.rate ) as "25th_percentile",
        percentile_disc(0.50) within group ( order by aggregate.rate ) as "50th_percentile",
        percentile_disc(0.75) within group ( order by aggregate.rate ) as "75th_percentile",
        percentile_disc(0.90) within group ( order by aggregate.rate ) as "90th_percentile",
        percentile_disc(0.99) within group ( order by aggregate.rate ) as "99th_percentile"
    from (select count(1) filter ( where aktiv = true )::decimal / count(1)::decimal as rate from brukernotifikasjon_view group by fodselsnummer) as aggregate
"""
fun Connection.getTotalActiveRateEventsStatisticsPerUser(): EventActiveRatePerUser =
    prepareStatement(totalEventActiveRatePerUserQueryString)
        .use {
            it.executeQuery().mapSingleResult {
                toEventActiveRate()
            }
        }

fun ResultSet.toEventActiveRate(): EventActiveRatePerUser {
    return EventActiveRatePerUser(
        min = getDouble("minRate"),
        max = getDouble("maxRate"),
        avg = getDouble("avgRate"),
        percentile25 = getDouble("25th_percentile"),
        percentile50 = getDouble("50th_percentile"),
        percentile75 = getDouble("75th_percentile"),
        percentile90 = getDouble("90th_percentile"),
        percentile99 = getDouble("99th_percentile")
    )
}
