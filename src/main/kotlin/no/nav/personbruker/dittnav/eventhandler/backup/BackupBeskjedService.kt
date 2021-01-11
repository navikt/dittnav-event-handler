package no.nav.personbruker.dittnav.eventhandler.backup

import Beskjed
import no.nav.personbruker.dittnav.eventhandler.beskjed.getAllBeskjedEvents
import no.nav.personbruker.dittnav.eventhandler.beskjed.getAllInactiveBeskjedEvents
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.common.kafka.KafkaProducerWrapper

class BackupBeskjedService(
        database: Database,
        private val beskjedProducer: KafkaProducerWrapper<no.nav.brukernotifikasjon.schemas.Beskjed>,
        private val doneProducer: KafkaProducerWrapper<no.nav.brukernotifikasjon.schemas.Done>
) : BackupService<Beskjed>(database) {

    suspend fun produceBeskjedEventsForAllBeskjedEventsInCache(dryrun: Boolean): Int {
        val allBeskjedEvents = getEventsFromCache { getAllBeskjedEvents() }
        return produceKafkaEventsForAllEventsInCache(beskjedProducer, dryrun, BackupBeskjedTransformer::toSchemasBeskjed, allBeskjedEvents)
    }

    suspend fun produceDoneEventsFromAllInactiveBeskjedEvents(dryrun: Boolean): Int {
        var allInactiveBeskjedEvents = getEventsFromCache { getAllInactiveBeskjedEvents() }
        return produceKafkaEventsForAllEventsInCache(doneProducer, dryrun, BackupBeskjedTransformer::toSchemasDone, allInactiveBeskjedEvents)
    }
}
