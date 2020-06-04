package no.nav.personbruker.dittnav.eventhandler.oppgave

import no.nav.personbruker.dittnav.eventhandler.config.Kafka.BACKUP_EVENT_CHUNCK_SIZE

class BackupOppgaveService(
        private val oppgaveEventService: OppgaveEventService,
        private val oppgaveProducer: OppgaveProducer
) {

    suspend fun produceOppgaveEventsForAllOppgaveEventsInCache(): Int {
        val allOppgaveEvents = oppgaveEventService.getAllOppgaveEventsInCach()
        var batchNumber = 0
        var numberOfProcessedEvents = 0
        if (allOppgaveEvents.isNotEmpty()) {
            allOppgaveEvents.chunked(BACKUP_EVENT_CHUNCK_SIZE) { allEvents ->
                batchNumber++
                val oppgaveEvents = oppgaveProducer.toSchemasOppgave(batchNumber, allOppgaveEvents)
                numberOfProcessedEvents += oppgaveProducer.produceAllOppgaveEvents(batchNumber, oppgaveEvents)
            }
        }
        return numberOfProcessedEvents
    }

    suspend fun produceDoneEventsFromAllInactiveOppgaveEvents(): Int {
        var allInactiveOppgaveEvents = oppgaveEventService.getAllInactiveOppgaveEventsInCach()
        var batchNumber = 0
        var numberOfProcessedEvents = 0
        if (allInactiveOppgaveEvents.isNotEmpty()) {
            allInactiveOppgaveEvents.chunked(BACKUP_EVENT_CHUNCK_SIZE) { allInactiveEvents ->
                batchNumber++
                val doneEvents = oppgaveProducer.toSchemasDone(batchNumber, allInactiveEvents)
                numberOfProcessedEvents += oppgaveProducer.produceDoneEvents(batchNumber, doneEvents)
            }
        }
        return numberOfProcessedEvents
    }
}
