package no.nav.personbruker.dittnav.eventhandler.statistics.query

import no.nav.personbruker.dittnav.eventhandler.common.database.mapSingleResult
import no.nav.personbruker.dittnav.eventhandler.common.EventType
import no.nav.personbruker.dittnav.eventhandler.statistics.CountMeasurement
import java.sql.Connection
import java.sql.ResultSet

private fun usersQueryString(type: EventType) = "select count(distinct fodselsnummer) as scalar_value from ${type.eventType}"

val totalUsersQueryString = "select count(distinct fodselsnummer) as scalar_value from brukernotifikasjon_view"
val beskjedUsersQueryString = usersQueryString(EventType.BESKJED)
val oppgaveUsersQueryString = usersQueryString(EventType.OPPGAVE)
val innboksUsersQueryString = usersQueryString(EventType.INNBOKS)
fun Connection.getCountUsersWithEventsForOppgave(): CountMeasurement {
    return prepareStatement(oppgaveUsersQueryString)
        .use {
            it.executeQuery().mapSingleResult {
                toCountMeasurement()
            }
        }
}
fun Connection.getCountUsersWithEventsForInnboks(): CountMeasurement {
    return prepareStatement(innboksUsersQueryString)
        .use {
            it.executeQuery().mapSingleResult {
                toCountMeasurement()
            }
        }
}
fun Connection.getCountUsersWithEventsForBeskjed(): CountMeasurement {
    return prepareStatement(beskjedUsersQueryString)
        .use {
            it.executeQuery().mapSingleResult {
                toCountMeasurement()
            }
        }
}
fun Connection.getCountUsersWithEvents(): CountMeasurement =
    prepareStatement(totalUsersQueryString)
        .use {
            it.executeQuery().mapSingleResult {
                toCountMeasurement()
            }
        }

private fun eventsQueryString(type: EventType) = "select count(1) as scalar_value from ${type.eventType}"
val totalEventsQueryString = "select count(1) as scalar_value from brukernotifikasjon_view"
val cachedDoneEventsQueryString = "select count(1) as scalar_value from done"
val beskjedEventsQueryString = eventsQueryString(EventType.BESKJED)
val oppgaveEventsQueryString = eventsQueryString(EventType.OPPGAVE)
val innboksEventsQueryString = eventsQueryString(EventType.INNBOKS)
fun Connection.getCountForOppgave(): CountMeasurement {
    return prepareStatement(oppgaveEventsQueryString)
        .use {
            it.executeQuery().mapSingleResult {
                toCountMeasurement()
            }
        }
}
fun Connection.getCountForInnboks(): CountMeasurement {
    return prepareStatement(innboksEventsQueryString)
        .use {
            it.executeQuery().mapSingleResult {
                toCountMeasurement()
            }
        }
}
fun Connection.getCountForBeskjed(): CountMeasurement {
    return prepareStatement(beskjedEventsQueryString)
        .use {
            it.executeQuery().mapSingleResult {
                toCountMeasurement()
            }
        }
}
fun Connection.getCountForDone(): CountMeasurement {
    return prepareStatement(cachedDoneEventsQueryString)
        .use {
            it.executeQuery().mapSingleResult {
                toCountMeasurement()
            }
        }
}
fun Connection.getCountNumberOfEvents(): CountMeasurement =
    prepareStatement(totalEventsQueryString)
        .use {
            it.executeQuery().mapSingleResult {
                toCountMeasurement()
            }
        }

private fun eventsActiveQueryString(type: EventType) = "select count(1) as scalar_value from ${type.eventType} where aktiv"

val totalEventsActiveQueryString = "select count(1) as scalar_value from brukernotifikasjon_view where aktiv"
val beskjedEventsActiveQueryString = eventsActiveQueryString(EventType.BESKJED)
val oppgaveEventsActiveQueryString = eventsActiveQueryString(EventType.OPPGAVE)
val innboksEventsActiveQueryString = eventsActiveQueryString(EventType.INNBOKS)
fun Connection.getActiveCountForOppgave(): CountMeasurement {
    return prepareStatement(oppgaveEventsActiveQueryString)
        .use {
            it.executeQuery().mapSingleResult {
                toCountMeasurement()
            }
        }
}
fun Connection.getActiveCountForInnboks(): CountMeasurement {
    return prepareStatement(innboksEventsActiveQueryString)
        .use {
            it.executeQuery().mapSingleResult {
                toCountMeasurement()
            }
        }
}
fun Connection.getActiveCountForBeskjed(): CountMeasurement {
    return prepareStatement(beskjedEventsActiveQueryString)
        .use {
            it.executeQuery().mapSingleResult {
                toCountMeasurement()
            }
        }
}
fun Connection.getCountNumberOfActiveEvents(): CountMeasurement =
    prepareStatement(totalEventsActiveQueryString)
        .use {
            it.executeQuery().mapSingleResult {
                toCountMeasurement()
            }
        }

fun ResultSet.toScalarInt(): Int {
    return getInt("scalar_value")
}
fun ResultSet.toCountMeasurement(): CountMeasurement {
    return CountMeasurement(
        count = toScalarInt()
    )
}
