package no.nav.personbruker.dittnav.eventhandler.brukernotifikasjon

import no.nav.personbruker.dittnav.eventhandler.common.database.Database
import no.nav.tms.token.support.tokenx.validation.user.TokenXUser

class BrukernotifikasjonService(
        private val database: Database
) {

    suspend fun numberOfInactiveEvents(bruker: TokenXUser): Int {
        val numberOfInactive = database.queryWithExceptionTranslation {
            getNumberOfBrukernotifikasjonerByActiveStatus(bruker, false)
        }
        return numberOfInactive
    }

    suspend fun numberOfActiveEvents(bruker: TokenXUser): Int {
        val numberOfActive = database.queryWithExceptionTranslation {
            getNumberOfBrukernotifikasjonerByActiveStatus(bruker, true)
        }
        return numberOfActive
    }

    suspend fun totalNumberOfEvents(bruker: TokenXUser): Int {
        val totalNumberOfEvents = database.queryWithExceptionTranslation {
            getNumberOfBrukernotifikasjoner(bruker)
        }
        return totalNumberOfEvents
    }

}
