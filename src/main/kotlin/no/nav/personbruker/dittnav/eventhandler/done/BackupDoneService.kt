package no.nav.personbruker.dittnav.eventhandler.done

import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import java.sql.Connection

class BackupDoneService(
        private val backupDoneProducer: BackupDoneProducer,
        private val database: Database
) {
    suspend fun produceDoneEventsForAllDoneEventsInCache(dryrun: Boolean): Int {
        val allDoneEvents = getAllDoneEventsFromCache()
        var batchNumber = 1
        var numberOfProcessedEvents = 0
        if (allDoneEvents.isNotEmpty()) {
            val doneEvents = backupDoneProducer.toSchemasDone(batchNumber, allDoneEvents)
            numberOfProcessedEvents += if (!dryrun) {
                backupDoneProducer.produceDoneEvents(batchNumber, doneEvents)
            } else {
                doneEvents.size
            }
        }

        return numberOfProcessedEvents
    }

    private suspend fun getAllDoneEventsFromCache(): List<BackupDone> {
        return getEvents { getAllDoneEvents() }
    }

    private suspend fun getEvents(operationToExecute: Connection.() -> List<BackupDone>): List<BackupDone> {
        val events = database.queryWithExceptionTranslation {
            operationToExecute()
        }
        return events
    }
}