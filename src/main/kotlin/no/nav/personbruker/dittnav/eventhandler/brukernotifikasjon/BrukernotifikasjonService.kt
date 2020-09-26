package no.nav.personbruker.dittnav.eventhandler.brukernotifikasjon

import no.nav.personbruker.dittnav.eventhandler.common.InnloggetBruker
import no.nav.personbruker.dittnav.eventhandler.common.database.Database

class BrukernotifikasjonService(
        private val database: Database
) {

    suspend fun numberOfActiveEvents(bruker: InnloggetBruker): Int {
        val numberOfActive = database.queryWithExceptionTranslation {
            getNumberOfBrukernotifikasjonerByActiveStatus(bruker, true)
        }
        return numberOfActive
    }

    suspend fun totalNumberOfEvents(bruker: InnloggetBruker): Int {
        val totalNumberOfEvents = database.queryWithExceptionTranslation {
            getNumberOfBrukernotifikasjoner(bruker)
        }
        return totalNumberOfEvents
    }

}
