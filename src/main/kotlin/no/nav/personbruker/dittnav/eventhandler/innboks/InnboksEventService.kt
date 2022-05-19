package no.nav.personbruker.dittnav.eventhandler.innboks

import no.nav.brukernotifikasjon.schemas.builders.util.ValidationUtil.validateNonNullFieldMaxLength
import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.personbruker.dittnav.eventhandler.common.modia.User
import no.nav.personbruker.dittnav.eventhandler.common.oneYearAgo
import no.nav.personbruker.dittnav.eventhandler.statistics.EventCountForProducer
import no.nav.tms.token.support.tokenx.validation.user.TokenXUser
import org.slf4j.LoggerFactory
import java.sql.Connection

class InnboksEventService(private val database: Database) {

    private val log = LoggerFactory.getLogger(InnboksEventService::class.java)

    suspend fun getActiveCachedEventsForUser(bruker: TokenXUser): List<InnboksDTO> {
        return getEvents { getAktivInnboksForInnloggetBruker(bruker.ident) }
            .map { innboks -> innboks.toDTO()}
    }

    suspend fun getActiveCachedEventsForUser(bruker: User): List<InnboksDTO> {
        return getEvents { getAktivInnboksForInnloggetBruker(bruker.fodselsnummer) }
            .map { innboks -> innboks.toDTO()}
    }

    suspend fun getInactiveCachedEventsForUser(bruker: TokenXUser): List<InnboksDTO> {
        return getEvents { getInaktivInnboksForInnloggetBruker(bruker.ident) }
            .map { innboks -> innboks.toDTO()}
    }

    suspend fun getInactiveCachedEventsForUser(bruker: User): List<InnboksDTO> {
        return getEvents { getInaktivInnboksForInnloggetBruker(bruker.fodselsnummer) }
            .map { innboks -> innboks.toDTO()}
    }

    suspend fun getAllCachedEventsForUser(bruker: TokenXUser): List<InnboksDTO> {
        return getEvents { getAllInnboksForInnloggetBruker(bruker.ident) }
            .map { innboks -> innboks.toDTO()}
    }

    suspend fun getAllCachedEventsForUser(bruker: User): List<InnboksDTO> {
        return getEvents { getAllInnboksForInnloggetBruker(bruker.fodselsnummer) }
            .map { innboks -> innboks.toDTO()}
    }

    suspend fun getRecentActiveEventsForUser(bruker: TokenXUser): List<InnboksDTO> {
        return getEvents { getAktivInnboksForFodselsnummerByForstBehandlet(bruker.ident, oneYearAgo()) }
            .map { innboks -> innboks.toDTO()}
    }

    suspend fun getRecentActiveEventsForUser(bruker: User): List<InnboksDTO> {
        return getEvents { getAktivInnboksForFodselsnummerByForstBehandlet(bruker.fodselsnummer, oneYearAgo()) }
            .map { innboks -> innboks.toDTO()}
    }

    suspend fun getRecentInactiveEventsForUser(bruker: TokenXUser): List<InnboksDTO> {
        return getEvents { getInaktivInnboksForFodselsnummerByForstBehandlet(bruker.ident, oneYearAgo()) }
            .map { innboks -> innboks.toDTO()}
    }

    suspend fun getRecentInactiveEventsForUser(bruker: User): List<InnboksDTO> {
        return getEvents { getInaktivInnboksForFodselsnummerByForstBehandlet(bruker.fodselsnummer, oneYearAgo()) }
            .map { innboks -> innboks.toDTO()}
    }

    suspend fun getAllRecentEventsForUser(bruker: TokenXUser): List<InnboksDTO> {
        return getEvents { getInnboksForFodselsnummerByForstBehandlet(bruker.ident, oneYearAgo()) }
            .map { innboks -> innboks.toDTO()}
    }

    suspend fun getAllRecentEventsForUser(bruker: User): List<InnboksDTO> {
        return getEvents { getInnboksForFodselsnummerByForstBehandlet(bruker.fodselsnummer, oneYearAgo()) }
            .map { innboks -> innboks.toDTO()}
    }

    suspend fun getAllGroupedEventsFromCacheForUser(bruker: TokenXUser, grupperingsid: String?, appnavn: String?): List<InnboksDTO> {
        val grupperingsId = validateNonNullFieldMaxLength(grupperingsid, "grupperingsid", 100)
        val app = validateNonNullFieldMaxLength(appnavn, "appnavn", 100)
        return getEvents { getAllGroupedInnboksEventsByIds(bruker.ident, grupperingsId, app) }
            .map { innboks -> innboks.toDTO()}
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
