package no.nav.personbruker.dittnav.eventhandler.done

import no.nav.personbruker.dittnav.eventhandler.backup.BackupDoneProducer
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.config.Kafka
import java.sql.Connection

class BackupDoneService(
        private val backupDoneProducer: BackupDoneProducer,
        private val database: Database
) {
    suspend fun produceDoneEventsForAllDoneEventsInCache(dryrun: Boolean): Int {
        val allDoneEvents = getAllDoneEventsFromCache()
        var batchNumber = 0
        var numberOfProcessedEvents = 0
        if (allDoneEvents.isNotEmpty()) {
            allDoneEvents.chunked(Kafka.BACKUP_EVENT_CHUNCK_SIZE) { listChunkDone ->
                batchNumber++
                val doneEvents = backupDoneProducer.toSchemasDone(batchNumber, listChunkDone)
                numberOfProcessedEvents += if (!dryrun) {
                    backupDoneProducer.produceDoneEvents(batchNumber, doneEvents)
                } else {
                    doneEvents.size
                }
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
