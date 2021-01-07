package no.nav.personbruker.dittnav.eventhandler.backup

import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.config.Kafka
import java.sql.Connection

abstract class BackupService<T>(private val database: Database) {

    suspend fun getEventsFromCache(operationToExecute: Connection.() -> List<T>): List<T> {
        return database.queryWithExceptionTranslation {
            operationToExecute()
        }
    }

    fun  produceKafkaEventsForAllEventsInCache(dryRun: Boolean, producer: (dryRun: Boolean, batchNumber: Int, events: List<T>) -> Int, events: List<T>): Int {
        var batchNumber = 0
        var numberOfProcessedEvents = 0
        if (events.isNotEmpty()) {
            events.chunked(Kafka.BACKUP_EVENT_CHUNCK_SIZE) { listChunck ->
                batchNumber++
                numberOfProcessedEvents += producer.invoke(dryRun, batchNumber, listChunck)
            }
        }
        return numberOfProcessedEvents
    }
}
