package no.nav.personbruker.dittnav.eventhandler.backup

import Beskjed
import no.nav.personbruker.dittnav.eventhandler.beskjed.getAllBeskjedEvents
import no.nav.personbruker.dittnav.eventhandler.beskjed.getAllInactiveBeskjedEvents
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.config.Kafka.BACKUP_EVENT_CHUNCK_SIZE

class BackupBeskjedService(
        database: Database,
        private val backupBeskjedProducer: BackupBeskjedProducer
) : BackupService<Beskjed>(database) {

    suspend fun produceBeskjedEventsForAllBeskjedEventsInCache(dryrun: Boolean): Int {
        val allBeskjedEvents = getEventsFromCache { getAllBeskjedEvents() }
        return produceKafkaEventsForAllEventsInCache(dryrun, backupBeskjedProducer::produceAllBeskjedEvents, allBeskjedEvents)
    }

    suspend fun produceDoneEventsFromAllInactiveBeskjedEvents(dryrun: Boolean): Int {
        var allInactiveBeskjedEvents = getEventsFromCache { getAllInactiveBeskjedEvents() }
        var batchNumber = 0
        var numberOfProcessedEvents = 0
        if (allInactiveBeskjedEvents.isNotEmpty()) {
            allInactiveBeskjedEvents.chunked(BACKUP_EVENT_CHUNCK_SIZE) { listChunkInactiveBeskjed ->
                batchNumber++
                val doneEvents = backupBeskjedProducer.toSchemasDone(batchNumber, listChunkInactiveBeskjed)
                numberOfProcessedEvents += if (!dryrun) {
                    backupBeskjedProducer.produceDoneEvents(batchNumber, doneEvents)
                } else {
                    doneEvents.size
                }
            }
        }

        return numberOfProcessedEvents

    }
}
