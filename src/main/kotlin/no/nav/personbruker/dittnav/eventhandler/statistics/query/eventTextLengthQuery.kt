package no.nav.personbruker.dittnav.eventhandler.statistics.query

import no.nav.personbruker.dittnav.eventhandler.common.database.mapSingleResult
import no.nav.personbruker.dittnav.eventhandler.common.EventType
import no.nav.personbruker.dittnav.eventhandler.statistics.EventTextLength
import no.nav.personbruker.dittnav.eventhandler.statistics.IntegerMeasurement
import java.sql.Connection
import java.sql.ResultSet

private fun singleTableQueryString(type: EventType) = """
    select
        min(length(tekst)) as minLength,
        max(length(tekst)) as maxLength,
        avg(length(tekst))::decimal as avgLength,
        percentile_disc(0.25) within group ( order by length(tekst) ) as "25th_percentile",
        percentile_disc(0.50) within group ( order by length(tekst) ) as "50th_percentile",
        percentile_disc(0.75) within group ( order by length(tekst) ) as "75th_percentile",
        percentile_disc(0.90) within group ( order by length(tekst) ) as "90th_percentile",
        percentile_disc(0.99) within group ( order by length(tekst) ) as "99th_percentile"
    from ${type.eventType}
"""

val beskjedEventTextLengthQueryString = singleTableQueryString(EventType.BESKJED)
val oppgaveEventTextLengthQueryString = singleTableQueryString(EventType.OPPGAVE)
val innboksEventTextLengthQueryString = singleTableQueryString(EventType.INNBOKS)

fun Connection.getTextLengthForOppgave(): IntegerMeasurement {
    return prepareStatement(oppgaveEventTextLengthQueryString)
        .use {
            it.executeQuery().mapSingleResult {
                toEventTextLength()
            }
        }
}

fun Connection.getTextLengthForInnboks(): IntegerMeasurement {
    return prepareStatement(innboksEventTextLengthQueryString)
        .use {
            it.executeQuery().mapSingleResult {
                toEventTextLength()
            }
        }
}

fun Connection.getTextLengthForBeskjed(): IntegerMeasurement {
    return prepareStatement(beskjedEventTextLengthQueryString)
        .use {
            it.executeQuery().mapSingleResult {
                toEventTextLength()
            }
        }
}

val totalEventTextLengthQueryString = """
    select
        min(inner_view.tekstLength) as minLength,
        max(inner_view.tekstLength) as maxLength,
        avg(inner_view.tekstLength)::decimal as avgLength,
        percentile_disc(0.25) within group ( order by inner_view.tekstLength ) as "25th_percentile",
        percentile_disc(0.50) within group ( order by inner_view.tekstLength ) as "50th_percentile",
        percentile_disc(0.75) within group ( order by inner_view.tekstLength ) as "75th_percentile",
        percentile_disc(0.90) within group ( order by inner_view.tekstLength ) as "90th_percentile",
        percentile_disc(0.99) within group ( order by inner_view.tekstLength ) as "99th_percentile"
    from (
        SELECT length(tekst) as tekstLength FROM BESKJED
        UNION ALL
        SELECT length(tekst) as tekstLength FROM OPPGAVE
        UNION ALL
        SELECT length(tekst) as tekstLength FROM INNBOKS
    ) as inner_view
"""
fun Connection.getTotalTextLength(): EventTextLength =
    prepareStatement(totalEventTextLengthQueryString)
        .use {
            it.executeQuery().mapSingleResult {
                toEventTextLength()
            }
        }

fun ResultSet.toEventTextLength(): EventTextLength {
    return EventTextLength(
        min = getInt("minLength"),
        max = getInt("maxLength"),
        avg = getDouble("avgLength"),
        percentile25 = getInt("25th_percentile"),
        percentile50 = getInt("50th_percentile"),
        percentile75 = getInt("75th_percentile"),
        percentile90 = getInt("90th_percentile"),
        percentile99 = getInt("99th_percentile")
    )
}
