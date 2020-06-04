package no.nav.personbruker.dittnav.eventhandler.beskjed

import no.nav.personbruker.dittnav.eventhandler.config.Kafka.BACKUP_EVENT_CHUNCK_SIZE

class BackupBeskjedService(
        private val beskjedEventService: BeskjedEventService,
        private val beskjedProducer: BeskjedProducer
) {

    suspend fun produceBeskjedEventsForAllBeskjedEventsInCache(): Int {
        val allBeskjedEvents = beskjedEventService.getAllBeskjedEventsInCach()
        var batchNumber = 0
        var numberOfProcessedEvents = 0
        if (allBeskjedEvents.isNotEmpty()) {
            allBeskjedEvents.chunked(BACKUP_EVENT_CHUNCK_SIZE) { allEvents ->
                batchNumber++
                val beskjedEvents = beskjedProducer.toSchemasBeskjed(batchNumber, allEvents)
                numberOfProcessedEvents += beskjedProducer.produceAllBeskjedEvents(batchNumber, beskjedEvents)
            }
        }
        return numberOfProcessedEvents
    }

    suspend fun produceDoneEventsFromAllInactiveBeskjedEvents(): Int {
        var allInactiveBeskjedEvents = beskjedEventService.getAllInactiveBeskjedEventsInCach()
        var batchNumber = 0
        var numberOfProcessedEvents = 0
        if (allInactiveBeskjedEvents.isNotEmpty()) {
            allInactiveBeskjedEvents.chunked(BACKUP_EVENT_CHUNCK_SIZE) { allInactiveEvents ->
                batchNumber++
                val doneEvents = beskjedProducer.toSchemasDone(batchNumber, allInactiveEvents)
                numberOfProcessedEvents += beskjedProducer.produceDoneEvents(batchNumber, doneEvents)
            }
        }
        return numberOfProcessedEvents
    }
}
