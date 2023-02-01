package no.nav.personbruker.dittnav.eventhandler.statistics

import no.nav.personbruker.dittnav.eventhandler.common.VarselType
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.statistics.query.*

class EventStatisticsService(private val database: Database) {

    suspend fun getEventsStatisticsPerUser(type: VarselType): IntegerMeasurement {
        return database.queryWithExceptionTranslation {
            when (type) {
                VarselType.BESKJED -> getEventsPerUserForBeskjed()
                VarselType.OPPGAVE -> getEventsPerUserForOppgave()
                VarselType.INNBOKS -> getEventsPerUserForInnboks()
                VarselType.DONE -> throw Exception("Statistik ikke tilgjengelig for done-eventer")
            }
        }
    }

    suspend fun getTotalEventsStatisticsPerUser(): IntegerMeasurement {
        return database.queryWithExceptionTranslation {
            getTotalEventsPerUser()
        }
    }

    suspend fun getActiveEventsStatisticsPerUser(type: VarselType): IntegerMeasurement {
        return database.queryWithExceptionTranslation {
            when (type) {
                VarselType.BESKJED -> getActiveEventsStatisticsPerUserForBeskjed()
                VarselType.OPPGAVE -> getActiveEventsStatisticsPerUserForOppgave()
                VarselType.INNBOKS -> getActiveEventsStatisticsPerUserForInnboks()
                VarselType.DONE -> throw Exception("Statistik ikke tilgjengelig for done-eventer")
            }
        }
    }

    suspend fun getTotalActiveEventsStatisticsPerUser(): IntegerMeasurement {
        return database.queryWithExceptionTranslation {
            getTotalActiveEventsStatisticsPerUser()
        }
    }

    suspend fun getActiveRateEventsStatisticsPerUser(type: VarselType): DecimalMeasurement {
        return database.queryWithExceptionTranslation {
            when (type) {
                VarselType.BESKJED -> getActiveRateEventsStatisticsPerUserForBeskjed()
                VarselType.OPPGAVE -> getActiveRateEventsStatisticsPerUserForOppgave()
                VarselType.INNBOKS -> getActiveRateEventsStatisticsPerUserForInnboks()
                VarselType.DONE -> throw Exception("Statistik ikke tilgjengelig for done-eventer")
            }
        }
    }

    suspend fun getTotalActiveRateEventsStatisticsPerUser(): DecimalMeasurement {
        return database.queryWithExceptionTranslation {
            getTotalActiveRateEventsStatisticsPerUser()
        }
    }

    suspend fun getEventsStatisticsPerGroupId(type: VarselType): IntegerMeasurement {
        return database.queryWithExceptionTranslation {
            when (type) {
                VarselType.BESKJED -> getEventsPerGroupIdForBeskjed()
                VarselType.OPPGAVE -> getEventsPerGroupIdForOppgave()
                VarselType.INNBOKS -> getEventsPerGroupIdForInnboks()
                VarselType.DONE -> throw Exception("Statistik ikke tilgjengelig for done-eventer")
            }
        }
    }

    suspend fun getTotalEventsStatisticsPerGroupId(): IntegerMeasurement {
        return database.queryWithExceptionTranslation {
            getTotalEventsPerGroupId()
        }
    }

    suspend fun getGroupIdsPerUser(type: VarselType): IntegerMeasurement {
        return database.queryWithExceptionTranslation {
            when (type) {
                VarselType.BESKJED -> getEventGroupIdsPerUserForBeskjed()
                VarselType.OPPGAVE -> getEventGroupIdsPerUserForOppgave()
                VarselType.INNBOKS -> getEventGroupIdsPerUserForInnboks()
                VarselType.DONE -> throw Exception("Statistik ikke tilgjengelig for done-eventer")
            }
        }
    }

    suspend fun getTotalGroupIdsPerUser(): IntegerMeasurement {
        return database.queryWithExceptionTranslation {
            getTotalEventGroupIdsPerUser()
        }
    }

    suspend fun getTextLength(type: VarselType): IntegerMeasurement {
        return database.queryWithExceptionTranslation {
            when (type) {
                VarselType.BESKJED -> getTextLengthForBeskjed()
                VarselType.OPPGAVE -> getTextLengthForOppgave()
                VarselType.INNBOKS -> getTextLengthForInnboks()
                VarselType.DONE -> throw Exception("Statistik ikke tilgjengelig for done-eventer")
            }
        }
    }

    suspend fun getTotalTextLength(): IntegerMeasurement {
        return database.queryWithExceptionTranslation {
            getTotalTextLength()
        }
    }

    suspend fun getCountUsersWithEvents(type: VarselType): CountMeasurement {
        return database.queryWithExceptionTranslation {
            when (type) {
                VarselType.BESKJED -> getCountUsersWithEventsForBeskjed()
                VarselType.OPPGAVE -> getCountUsersWithEventsForOppgave()
                VarselType.INNBOKS -> getCountUsersWithEventsForInnboks()
                VarselType.DONE -> throw Exception("Statistik ikke tilgjengelig for done-eventer")
            }
        }
    }

    suspend fun getTotalCountUsersWithEvents(): CountMeasurement {
        return database.queryWithExceptionTranslation {
            getCountUsersWithEvents()
        }
    }

    suspend fun getEventCount(type: VarselType): CountMeasurement {
        return database.queryWithExceptionTranslation {
            when (type) {
                VarselType.BESKJED -> getCountForBeskjed()
                VarselType.OPPGAVE -> getCountForOppgave()
                VarselType.INNBOKS -> getCountForInnboks()
                VarselType.DONE -> getCountForDone()
            }
        }
    }

    suspend fun getTotalEventCount(): CountMeasurement {
        return database.queryWithExceptionTranslation {
            getCountNumberOfEvents()
        }
    }

    suspend fun getActiveEventCount(type: VarselType): CountMeasurement {
        return database.queryWithExceptionTranslation {
            when (type) {
                VarselType.BESKJED -> getActiveCountForBeskjed()
                VarselType.OPPGAVE -> getActiveCountForOppgave()
                VarselType.INNBOKS -> getActiveCountForInnboks()
                VarselType.DONE -> throw Exception("Statistik ikke tilgjengelig for done-eventer")
            }
        }
    }

    suspend fun getTotalActiveEventCount(): CountMeasurement {
        return database.queryWithExceptionTranslation {
            getCountNumberOfActiveEvents()
        }
    }

    suspend fun getActiveEventsFrequencyDistribution(type: VarselType): EventFrequencyDistribution {
        return database.queryWithExceptionTranslation {
            when (type) {
                VarselType.BESKJED -> getActiveEventsFrequencyDistribution("beskjed")
                VarselType.OPPGAVE -> getActiveEventsFrequencyDistribution("oppgave")
                VarselType.INNBOKS -> getActiveEventsFrequencyDistribution("innboks")
                VarselType.DONE -> throw Exception("Statistik ikke tilgjengelig for done-eventer")
            }
        }
    }

    suspend fun getTotalActiveEventsFrequencyDistribution(): EventFrequencyDistribution {
        return database.queryWithExceptionTranslation {
            getTotalActiveEventsFrequencyDistribution()
        }
    }
}
