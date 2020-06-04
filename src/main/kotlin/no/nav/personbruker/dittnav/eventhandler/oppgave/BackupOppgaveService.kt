package no.nav.personbruker.dittnav.eventhandler.oppgave

class BackupOppgaveService(
        private val oppgaveEventService: OppgaveEventService,
        private val oppgaveProducer: OppgaveProducer
) {

    suspend fun produceOppgaveEventsForAllOppgaveEventsInCach() {
        val allOppgaveEvents = oppgaveEventService.getAllOppgaveEventsInCach()
        if (allOppgaveEvents.isNotEmpty()) {
            val oppgaveEvents = oppgaveProducer.toSchemasOppgave(allOppgaveEvents)
            oppgaveProducer.produceAllOppgaveEvents(oppgaveEvents)
        }
    }

    suspend fun produceDoneEventsFromAllInactiveOppgaveEvents() {
        var allInactiveOppgaveEvents = oppgaveEventService.getAllInactiveOppgaveEventsInCach()
        if (allInactiveOppgaveEvents.isNotEmpty()) {
            val doneEvents = oppgaveProducer.toSchemasDone(allInactiveOppgaveEvents)
            oppgaveProducer.produceDoneEvents(doneEvents)
        }
    }
}