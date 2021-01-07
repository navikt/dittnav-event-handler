package no.nav.personbruker.dittnav.eventhandler.backup

import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.config.Kafka.BACKUP_EVENT_CHUNCK_SIZE
import no.nav.personbruker.dittnav.eventhandler.oppgave.Oppgave
import no.nav.personbruker.dittnav.eventhandler.oppgave.getAllInactiveOppgaveEvents
import no.nav.personbruker.dittnav.eventhandler.oppgave.getAllOppgaveEvents

class BackupOppgaveService(
        database: Database,
        private val backupOppgaveProducer: BackupOppgaveProducer
): BackupService<Oppgave>(database){

    suspend fun produceOppgaveEventsForAllOppgaveEventsInCache(dryrun: Boolean): Int {
        val allOppgaveEvents = getEventsFromCache { getAllOppgaveEvents() }
        return produceKafkaEventsForAllEventsInCache(dryrun, backupOppgaveProducer::produceAllOppgaveEvents, allOppgaveEvents)
    }

    suspend fun produceDoneEventsFromAllInactiveOppgaveEvents(dryrun: Boolean): Int {
        var allInactiveOppgaveEvents = getEventsFromCache { getAllInactiveOppgaveEvents() }
        var batchNumber = 0
        var numberOfProcessedEvents = 0
        if (allInactiveOppgaveEvents.isNotEmpty()) {
            allInactiveOppgaveEvents.chunked(BACKUP_EVENT_CHUNCK_SIZE) { listChunkInactiveBeskjed ->
                batchNumber++
                val doneEvents = backupOppgaveProducer.toSchemasDone(batchNumber, listChunkInactiveBeskjed)
                numberOfProcessedEvents += if (!dryrun) {
                    backupOppgaveProducer.produceDoneEvents(batchNumber, doneEvents)
                } else {
                    doneEvents.size
                }
            }
        }
        return numberOfProcessedEvents
    }
}
