package no.nav.personbruker.dittnav.eventhandler.backup

import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.common.kafka.KafkaProducerWrapper
import no.nav.personbruker.dittnav.eventhandler.oppgave.Oppgave
import no.nav.personbruker.dittnav.eventhandler.oppgave.getAllInactiveOppgaveEvents
import no.nav.personbruker.dittnav.eventhandler.oppgave.getAllOppgaveEvents

class BackupOppgaveService(
        database: Database,
        private val oppgaveProducer: KafkaProducerWrapper<no.nav.brukernotifikasjon.schemas.Oppgave>,
        private val doneProducer: KafkaProducerWrapper<no.nav.brukernotifikasjon.schemas.Done>,
        private val backupOppgaveTransformer: BackupOppgaveTransformer
): BackupService<Oppgave>(database){

    suspend fun produceOppgaveEventsForAllOppgaveEventsInCache(dryrun: Boolean): Int {
        val allOppgaveEvents = getEventsFromCache { getAllOppgaveEvents() }
        return produceKafkaEventsForAllEventsInCache(oppgaveProducer, dryrun, backupOppgaveTransformer::toSchemasOppgave, allOppgaveEvents)
    }

    suspend fun produceDoneEventsFromAllInactiveOppgaveEvents(dryrun: Boolean): Int {
        var allInactiveOppgaveEvents = getEventsFromCache { getAllInactiveOppgaveEvents() }
        return produceKafkaEventsForAllEventsInCache(doneProducer, dryrun, backupOppgaveTransformer::toSchemasDone, allInactiveOppgaveEvents)
    }
}
