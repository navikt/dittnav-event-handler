package no.nav.personbruker.dittnav.eventhandler.beskjed

import Beskjed
import no.nav.personbruker.dittnav.eventhandler.common.InnloggetBruker
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.config.Kafka.BACKUP_EVENT_CHUNCK_SIZE
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.time.Instant
import java.time.ZoneId

class BeskjedEventService(
        private val database: Database,
        private val beskjedProducer: BeskjedProducer
) {

    private val log = LoggerFactory.getLogger(BeskjedEventService::class.java)

    suspend fun getActiveCachedEventsForUser(bruker: InnloggetBruker): List<Beskjed> {
        return getEvents { getAktivBeskjedForInnloggetBruker(bruker) }
                .filter { beskjed -> !beskjed.isExpired() }
    }

    suspend fun getInactiveCachedEventsForUser(bruker: InnloggetBruker): List<Beskjed> {
        val all = getAllEventsFromCacheForUser(bruker)
        val inactive = all.filter { beskjed -> !beskjed.aktiv }
        val expired = all.filter { beskjed -> beskjed.isExpired() }
        return inactive + expired
    }

    suspend fun getAllEventsFromCacheForUser(bruker: InnloggetBruker): List<Beskjed> {
        return getEvents { getAllBeskjedForInnloggetBruker(bruker) }
    }

    suspend fun produceBeskjedEventsForAllBeskjedEventsInCache(): Int {
        val allBeskjedEvents = getEvents { getAllBeskjedEvents() }
        var numberOfProcessedEvents = 0
        var batchNumber = 0
        if (allBeskjedEvents.isNotEmpty()) {
            allBeskjedEvents.chunked(BACKUP_EVENT_CHUNCK_SIZE) { allBeskjedChunk->
                numberOfProcessedEvents += beskjedProducer.produceAllBeskjedEventsFromList(++batchNumber, allBeskjedChunk)
                log.info("Prosesserte beskjed-backup, batch nr $batchNumber")
            }
        }
        return numberOfProcessedEvents
    }

    suspend fun produceDoneEventsFromAllInactiveBeskjedEvents(): Int {
        val allInactiveBeskjedEvents = getEvents { getAllInactiveBeskjed() }
        var numberOfProcessedEvents = 0
        var batchNumber = 0;
        if (allInactiveBeskjedEvents.isNotEmpty()) {
            allInactiveBeskjedEvents.chunked(BACKUP_EVENT_CHUNCK_SIZE) { allInactiveBeskjedChunk ->
                    numberOfProcessedEvents += beskjedProducer.produceDoneEventFromInactiveBeskjedEvents(++batchNumber, allInactiveBeskjedChunk)
                    log.info("Prosesserte beskjed-done-backup, batch nr $batchNumber")
            }
        }
        return numberOfProcessedEvents
    }

    private fun Beskjed.isExpired() : Boolean = synligFremTil?.isBefore(Instant.now().atZone(ZoneId.of("Europe/Oslo")))?: false

    private suspend fun getEvents(operationToExecute: Connection.() -> List<Beskjed>): List<Beskjed> {
        val events = database.queryWithExceptionTranslation {
            operationToExecute()
        }
        if(produsentIsEmpty(events)) {
            log.warn("Returnerer beskjed-eventer med tom produsent til frontend. Kanskje er ikke systembrukeren lagt inn i systembruker-tabellen?")
        }
        return events
    }

    private fun produsentIsEmpty(events: List<Beskjed>): Boolean {
        return events.any { beskjed -> beskjed.produsent.isNullOrEmpty() }
    }
}
