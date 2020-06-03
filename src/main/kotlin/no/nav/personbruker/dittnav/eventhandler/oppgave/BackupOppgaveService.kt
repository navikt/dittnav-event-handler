package no.nav.personbruker.dittnav.eventhandler.oppgave

class BackupOppgaveService(
        private val oppgaveEventService: OppgaveEventService,
        private val oppgaveProducer: OppgaveProducer
) {

    suspend fun produceOppgaveEventsForAllOppgaveEventsInCach() {
        val allOppgaveEvents = oppgaveEventService.getAllOppgaveEventsInCach()
        if (allOppgaveEvents.isNotEmpty()) {
            oppgaveProducer.produceAllOppgaveEventsFromList(allOppgaveEvents)
        }
    }

    suspend fun produceDoneEventsFromAllInactiveOppgaveEvents() {
        var allInactiveOppgaveEvents = oppgaveEventService.getAllInactiveOppgaveEventsInCach()
        if (allInactiveOppgaveEvents.isNotEmpty()) {
            oppgaveProducer.produceDoneEventFromInactiveOppgaveEvents(allInactiveOppgaveEvents)
        }
    }
}