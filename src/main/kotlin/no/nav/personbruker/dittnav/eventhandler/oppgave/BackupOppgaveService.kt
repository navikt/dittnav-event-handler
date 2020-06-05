package no.nav.personbruker.dittnav.eventhandler.oppgave

import no.nav.personbruker.dittnav.eventhandler.config.Kafka.BACKUP_EVENT_CHUNCK_SIZE

class BackupOppgaveService(
        private val oppgaveEventService: OppgaveEventService,
        private val oppgaveProducer: OppgaveProducer
) {

    suspend fun produceOppgaveEventsForAllOppgaveEventsInCache(dryrun: Boolean): Int {
        val allOppgaveEvents = oppgaveEventService.getAllOppgaveEventsInCach()
        var batchNumber = 0
        var numberOfProcessedEvents = 0
        if (allOppgaveEvents.isNotEmpty()) {
            allOppgaveEvents.chunked(BACKUP_EVENT_CHUNCK_SIZE) { listChunkBeskjed ->
                batchNumber++
                val oppgaveEvents = oppgaveProducer.toSchemasOppgave(batchNumber, listChunkBeskjed)
                numberOfProcessedEvents += if (!dryrun) {
                    oppgaveProducer.produceAllOppgaveEvents(batchNumber, oppgaveEvents)
                } else {
                    oppgaveEvents.size
                }
            }
        }
        return numberOfProcessedEvents
    }

    suspend fun produceDoneEventsFromAllInactiveOppgaveEvents(dryrun: Boolean): Int {
        var allInactiveOppgaveEvents = oppgaveEventService.getAllInactiveOppgaveEventsInCach()
        var batchNumber = 0
        var numberOfProcessedEvents = 0
        if (allInactiveOppgaveEvents.isNotEmpty()) {
            allInactiveOppgaveEvents.chunked(BACKUP_EVENT_CHUNCK_SIZE) { listChunkInactiveBeskjed ->
                batchNumber++
                val doneEvents = oppgaveProducer.toSchemasDone(batchNumber, listChunkInactiveBeskjed)
                numberOfProcessedEvents += if (!dryrun) {
                    oppgaveProducer.produceDoneEvents(batchNumber, doneEvents)
                } else {
                    doneEvents.size
                }
            }
        }
        return numberOfProcessedEvents
    }
}
