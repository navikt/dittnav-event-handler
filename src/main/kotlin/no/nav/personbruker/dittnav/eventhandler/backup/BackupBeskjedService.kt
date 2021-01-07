package no.nav.personbruker.dittnav.eventhandler.backup

import no.nav.personbruker.dittnav.eventhandler.beskjed.BeskjedEventService
import no.nav.personbruker.dittnav.eventhandler.config.Kafka.BACKUP_EVENT_CHUNCK_SIZE

class BackupBeskjedService(
        private val beskjedEventService: BeskjedEventService,
        private val beskjedProducer: BackupBeskjedProducer
) {

    suspend fun produceBeskjedEventsForAllBeskjedEventsInCache(dryrun: Boolean): Int {
        val allBeskjedEvents = beskjedEventService.getAllBeskjedEventsInCach()
        var batchNumber = 0
        var numberOfProcessedEvents = 0
        if (allBeskjedEvents.isNotEmpty()) {
            allBeskjedEvents.chunked(BACKUP_EVENT_CHUNCK_SIZE) { listChunkBeskjed ->
                batchNumber++
                val beskjedEvents = beskjedProducer.toSchemasBeskjed(batchNumber, listChunkBeskjed)
                numberOfProcessedEvents += if (!dryrun) {
                    beskjedProducer.produceAllBeskjedEvents(batchNumber, beskjedEvents)
                } else {
                    beskjedEvents.size
                }
            }
        }
        return numberOfProcessedEvents

    }

    suspend fun produceDoneEventsFromAllInactiveBeskjedEvents(dryrun: Boolean): Int {
        var allInactiveBeskjedEvents = beskjedEventService.getAllInactiveBeskjedEventsInCach()
        var batchNumber = 0
        var numberOfProcessedEvents = 0
        if (allInactiveBeskjedEvents.isNotEmpty()) {
            allInactiveBeskjedEvents.chunked(BACKUP_EVENT_CHUNCK_SIZE) { listChunkInactiveBeskjed ->
                batchNumber++
                val doneEvents = beskjedProducer.toSchemasDone(batchNumber, listChunkInactiveBeskjed)
                numberOfProcessedEvents += if (!dryrun) {
                    beskjedProducer.produceDoneEvents(batchNumber, doneEvents)
                } else {
                    doneEvents.size
                }
            }
        }

        return numberOfProcessedEvents

    }
}
