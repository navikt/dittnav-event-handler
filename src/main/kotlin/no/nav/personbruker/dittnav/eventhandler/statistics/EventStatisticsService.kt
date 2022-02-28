package no.nav.personbruker.dittnav.eventhandler.statistics

import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.common.statistics.query.*
import no.nav.personbruker.dittnav.eventhandler.statistics.query.getEventGroupIdsPerUserForBeskjed
import no.nav.personbruker.dittnav.eventhandler.statistics.query.getEventGroupIdsPerUserForInnboks
import no.nav.personbruker.dittnav.eventhandler.statistics.query.getEventGroupIdsPerUserForOppgave

class EventStatisticsService(private val database: Database) {

    suspend fun getEventsStatisticsPerUser(type: EventType): IntegerMeasurement {
        return database.queryWithExceptionTranslation {
            when (type) {
                EventType.BESKJED -> getEventsPerUserForBeskjed()
                EventType.OPPGAVE -> getEventsPerUserForOppgave()
                EventType.INNBOKS -> getEventsPerUserForInnboks()
                EventType.DONE -> throw Exception("Statistik ikke tilgjengelig for done-eventer")
            }
        }
    }

    suspend fun getActiveEventsStatisticsPerUser(type: EventType): IntegerMeasurement {
        return database.queryWithExceptionTranslation {
            when (type) {
                EventType.BESKJED -> getActiveEventsStatisticsPerUserForBeskjed()
                EventType.OPPGAVE -> getActiveEventsStatisticsPerUserForOppgave()
                EventType.INNBOKS -> getActiveEventsStatisticsPerUserForInnboks()
                EventType.DONE -> throw Exception("Statistik ikke tilgjengelig for done-eventer")
            }
        }
    }

    suspend fun getActiveRateEventsStatisticsPerUser(type: EventType): DecimalMeasurement {
        return database.queryWithExceptionTranslation {
            when (type) {
                EventType.BESKJED -> getActiveRateEventsStatisticsPerUserForBeskjed()
                EventType.OPPGAVE -> getActiveRateEventsStatisticsPerUserForOppgave()
                EventType.INNBOKS -> getActiveRateEventsStatisticsPerUserForInnboks()
                EventType.DONE -> throw Exception("Statistik ikke tilgjengelig for done-eventer")
            }
        }
    }

    suspend fun getEventsStatisticsPerGroupId(type: EventType): IntegerMeasurement {
        return database.queryWithExceptionTranslation {
            when (type) {
                EventType.BESKJED -> getEventsPerGroupIdForBeskjed()
                EventType.OPPGAVE -> getEventsPerGroupIdForOppgave()
                EventType.INNBOKS -> getEventsPerGroupIdForInnboks()
                EventType.DONE -> throw Exception("Statistik ikke tilgjengelig for done-eventer")
            }
        }
    }

    suspend fun getGroupIdsPerUser(type: EventType): IntegerMeasurement {
        return database.queryWithExceptionTranslation {
            when (type) {
                EventType.BESKJED -> getEventGroupIdsPerUserForBeskjed()
                EventType.OPPGAVE -> getEventGroupIdsPerUserForOppgave()
                EventType.INNBOKS -> getEventGroupIdsPerUserForInnboks()
                EventType.DONE -> throw Exception("Statistik ikke tilgjengelig for done-eventer")
            }
        }
    }

    suspend fun getTextLength(type: EventType): IntegerMeasurement {
        return database.queryWithExceptionTranslation {
            when (type) {
                EventType.BESKJED -> getTextLengthForBeskjed()
                EventType.OPPGAVE -> getTextLengthForOppgave()
                EventType.INNBOKS -> getTextLengthForInnboks()
                EventType.DONE -> throw Exception("Statistik ikke tilgjengelig for done-eventer")
            }
        }
    }

    suspend fun getCountUsersWithEvents(type: EventType): CountMeasurement {
        return database.queryWithExceptionTranslation {
            when (type) {
                EventType.BESKJED -> getCountUsersWithEventsForBeskjed()
                EventType.OPPGAVE -> getCountUsersWithEventsForOppgave()
                EventType.INNBOKS -> getCountUsersWithEventsForInnboks()
                EventType.DONE -> throw Exception("Statistik ikke tilgjengelig for done-eventer")
            }
        }
    }

    suspend fun getEventCount(type: EventType): CountMeasurement {
        return database.queryWithExceptionTranslation {
            when (type) {
                EventType.BESKJED -> getCountForBeskjed()
                EventType.OPPGAVE -> getCountForOppgave()
                EventType.INNBOKS -> getCountForInnboks()
                EventType.DONE -> throw Exception("Statistik ikke tilgjengelig for done-eventer")
            }
        }
    }

    suspend fun getActiveEventCount(type: EventType): CountMeasurement {
        return database.queryWithExceptionTranslation {
            when (type) {
                EventType.BESKJED -> getActiveCountForBeskjed()
                EventType.OPPGAVE -> getActiveCountForOppgave()
                EventType.INNBOKS -> getActiveCountForInnboks()
                EventType.DONE -> throw Exception("Statistik ikke tilgjengelig for done-eventer")
            }
        }
    }
}
