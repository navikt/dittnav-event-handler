package no.nav.personbruker.dittnav.eventhandler.innboks

import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.statistics.EventCountForProducer
import java.sql.Connection

class InnboksEventService(private val database: Database) {

    suspend fun getActiveEventsForFodselsnummer(fodselsnummer: String): List<InnboksDTO> {
        return getEvents {
            getAktivInnboksForFodselsnummer(fodselsnummer)
        }.map { innboks -> innboks.toDTO() }
    }

    suspend fun getInactiveEventsForFodselsnummer(fodselsnummer: String): List<InnboksDTO> {
        return getEvents {
            getInaktivInnboksForFodselsnummer(fodselsnummer)
        }.map { innboks -> innboks.toDTO() }
    }

    suspend fun getAllEventsForFodselsnummer(fodselsnummer: String): List<InnboksDTO> {
        return getEvents {
            getAllInnboksForFodselsnummer(fodselsnummer)
        }.map { innboks -> innboks.toDTO() }
    }

    suspend fun getAllGroupedEventsByProducerFromCache(): List<EventCountForProducer> {
        return database.queryWithExceptionTranslation { getAllGroupedInnboksEventsByProducer() }
    }

    private suspend fun getEvents(operationToExecute: Connection.() -> List<Innboks>): List<Innboks> {
        val events = database.queryWithExceptionTranslation {
            operationToExecute()
        }
        return events
    }
}
