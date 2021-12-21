package no.nav.personbruker.dittnav.eventhandler.backup

import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.common.kafka.KafkaProducerWrapper
import no.nav.personbruker.dittnav.eventhandler.done.Done
import no.nav.personbruker.dittnav.eventhandler.done.getAllDoneEvents

class BackupDoneService(
        database: Database,
        private val doneProducer: KafkaProducerWrapper<no.nav.brukernotifikasjon.schemas.Done>
): BackupService<Done>(database){

    suspend fun produceDoneEventsForAllDoneEventsInCache(dryrun: Boolean): Int {
        val allDoneEvents = getEventsFromCache { getAllDoneEvents() }
        return produceKafkaEventsForAllEventsInCache(doneProducer, dryrun, BackupDoneTranformer::toSchemasDone, allDoneEvents)
    }
}